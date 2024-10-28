package com.nasr.crawlerservice.service.impl;

import com.nasr.crawlerservice.domain.ExtractedData;
import com.nasr.crawlerservice.domain.UrlData;
import com.nasr.crawlerservice.enumaration.ValidatorResult;
import com.nasr.crawlerservice.service.CrawlService;
import com.nasr.crawlerservice.service.external.UniquenessCheckerExternalService;
import com.nasr.crawlerservice.util.SynchronizedBigInteger;
import com.nasr.crawlerservice.validator.CrawlValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.nasr.crawlerservice.enumaration.ValidatorResult.OK;
import static com.nasr.crawlerservice.util.CrawlerExtractionUtility.extract;
import static com.nasr.crawlerservice.util.DepthLimitUtility.isAllowedDepth;
import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlServiceImpl implements CrawlService {

    private long crawlStartTime;
    private static final int MAX_SUCCESS_CRAWL_PAGE = 4000;
    private static final int MAX_CONCURRENT_DOWNLOADS = 100; // Adjust based on system capacity
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_DOWNLOADS);

    private final ConcurrentLinkedQueue<UrlData> domains = new ConcurrentLinkedQueue<>();
    private final UniquenessCheckerExternalService uniquenessCheckerExternalService;
    private final KafkaWebpageProducerService kafkaProducerService;


    private String baseDomain;
    private final AtomicInteger successPage = new AtomicInteger(0);
    private final AtomicInteger errorPage = new AtomicInteger(0);
    private final AtomicInteger invalidPage = new AtomicInteger(0);
    private final AtomicLong totalUrlFounded = new AtomicLong(1);
    private final SynchronizedBigInteger textContentBytes = new SynchronizedBigInteger(BigInteger.ZERO);

    @SneakyThrows
    @Override
    public void crawl(String domain) {

        baseDomain = domain;
        domains.add(new UrlData(domain, 0));

        CompletableFuture.runAsync(this::startCrawlProcess)
                .thenRun(this::clearData);

        System.out.println("test");

    }

    private void startCrawlProcess() {

        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            crawlStartTime = System.currentTimeMillis();

            while (true) {

                if (checkIsExceed()) break;

                if (!domains.isEmpty()) {

                    UrlData urlData = domains.poll();  // Use poll to avoid blocking
                    if (urlData == null) break; // Exit if no more URLs to process

                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    runAsync(() -> {
                        try {
                            processUrl(urlData);
                        }finally {
                            semaphore.release();
                        }
                    },executorService);
                }
            }
            logCrawlingSummary();
            executorService.shutdownNow();
        }




    }

    private boolean checkIsExceed() {
        return (successPage.get() >= MAX_SUCCESS_CRAWL_PAGE);

    }

    private void processUrl(UrlData urlData) {
        try {
            Connection.Response response = Jsoup.connect(urlData.getUrl()).execute();
            handleResponse(response, urlData);
        } catch (IOException | URISyntaxException e) {
            log.error("exception occurred while processing url : {}", urlData.getUrl(), e);
            errorPage.incrementAndGet();
        }
    }

    private void handleResponse(Connection.Response response, UrlData urlData) throws IOException, URISyntaxException {
        ValidatorResult result = validateResponse(response);

        if (result == OK) {

            ExtractedData extractedData = extract(response, urlData.getUrl());
            calculateTextContentLength(extractedData.getContent());
            addTotalUrlFounded(extractedData.getLinks());
            List<String> distinctUrls = getDistinctUrls(extractedData.getLinks());
            kafkaProducerService.publish(extractedData);

            if (isAllowedDepth(urlData, baseDomain)) {
                domains.addAll(mapUrlToUrlData(distinctUrls, urlData));
            }
            successPage.incrementAndGet();

        } else {
            invalidPage.incrementAndGet();
        }
    }

    private void addTotalUrlFounded(Collection<?> collection) {
        totalUrlFounded.set(totalUrlFounded.get() + collection.size());
    }

    private void calculateTextContentLength(String text) {
        textContentBytes.addAndGet(BigInteger.valueOf(text.getBytes(StandardCharsets.UTF_8).length));
    }

    private List<UrlData> mapUrlToUrlData(List<String> urls, UrlData urlData) {
        return urls.stream()
                .map(url -> new UrlData(url, urlData.getDepth() + 1, urlData.getUrl()))
                .collect(Collectors.toList());
    }

    private ValidatorResult validateResponse(Connection.Response response) {
        return CrawlValidator.verifyContentAccessible()
                .and(CrawlValidator.verifyHtmlType())
                .apply(response);
    }

    private List<String> getDistinctUrls(Set<String> urls) {
        if (urls.isEmpty()) return Collections.emptyList();
        return uniquenessCheckerExternalService.getDistinctUrl(urls);
    }

    private void clearData() {
        successPage.set(0);
        errorPage.set(0);
        invalidPage.set(0);
        totalUrlFounded.set(0);
        textContentBytes.set(BigInteger.ZERO);
    }

    private void logCrawlingSummary() {

        System.out.println("-".repeat(200));
        log.info("crawl process take : {} seconds", (System.currentTimeMillis() - crawlStartTime) / 1000);
        log.info("all crawled html text take size : {} KB", (textContentBytes.get().divide(BigInteger.valueOf(1000))));
        log.info("total url founded is : {}", totalUrlFounded.get());
        log.info("unCrawled urls in queue : {}", domains.size());
        log.info("error page number : {}", errorPage.get());
        log.info("successful page number : {}", successPage.get());
        System.out.println("-".repeat(200));
    }
}
