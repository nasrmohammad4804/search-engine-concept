package com.nasr.crawlerservice.service.impl;//package com.nasr.crawlerservice.service.impl;

import com.nasr.crawlerservice.domain.ExtractedData;
import com.nasr.crawlerservice.domain.UrlData;
import com.nasr.crawlerservice.enumaration.ValidatorResult;
import com.nasr.crawlerservice.service.CrawlService;
import com.nasr.crawlerservice.service.external.UniquenessCheckerExternalService;
import com.nasr.crawlerservice.util.SynchronizedBigInteger;
import com.nasr.crawlerservice.validator.CrawlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.nasr.crawlerservice.enumaration.ValidatorResult.OK;
import static com.nasr.crawlerservice.util.CrawlerExtractionUtility.extract;
import static com.nasr.crawlerservice.util.DepthLimitUtility.isAllowed;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlServiceImpl implements CrawlService {

    private long crawlStartTime;
    private static final int MAX_SUCCESS_CRAWL_PAGE = 4000;

    private final ConcurrentLinkedQueue<UrlData> domains = new ConcurrentLinkedQueue<>();
    private final UniquenessCheckerExternalService uniquenessCheckerExternalService;
    private final KafkaWebpageProducerService kafkaProducerService;


    private final AtomicInteger successPage = new AtomicInteger(0);
    private final AtomicInteger errorPage = new AtomicInteger(0);
    private final AtomicInteger invalidPage = new AtomicInteger(0);
    private final SynchronizedBigInteger textContentBytes = new SynchronizedBigInteger(BigInteger.ZERO);

    @Override
    public void crawl(String domain) {

        domains.add(new UrlData(domain, 0));

        CompletableFuture<Void> crawlTasks = CompletableFuture.allOf(
                CompletableFuture.runAsync(this::startCrawlProcess)

                        .exceptionally(throwable -> {
                            logCrawlingSummary();
                            return null;
                        })
                        .thenRun(() -> System.exit(0))
        );

    }

    private void startCrawlProcess() {

        crawlStartTime = System.currentTimeMillis();

        while (true) {

            if (!domains.isEmpty()) {

                checkExceed();

                UrlData urlData = domains.poll();  // Use poll to avoid blocking
                if (urlData == null) break; // Exit if no more URLs to process

                CompletableFuture.runAsync(() -> processUrl(urlData));

            }
        }

    }

    private void checkExceed() {
        if (successPage.get() >= MAX_SUCCESS_CRAWL_PAGE)
            throw new RuntimeException("Crawl exceeded maximum number of success pages");
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
            List<String> distinctUrls = getDistinctUrls(extractedData.getLinks());
            kafkaProducerService.publish(extractedData);

            if (isAllowed(urlData, urlData.getUrl())) {
                domains.addAll(mapUrlToUrlData(distinctUrls, urlData.getDepth()));
            }
            successPage.incrementAndGet();
        } else {
            invalidPage.incrementAndGet();
        }
    }

    private void calculateTextContentLength(String text) {
        textContentBytes.addAndGet(BigInteger.valueOf(text.getBytes(StandardCharsets.UTF_8).length));
    }

    private List<UrlData> mapUrlToUrlData(List<String> urls, int depth) {
        return urls.stream()
                .map(url -> new UrlData(url, depth + 1))
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

    private void logCrawlingSummary() {

        log.info("successful page number : {}", successPage.get());
        log.info("error page number : {}", errorPage.get());
        log.info("crawl process take : {} seconds", (System.currentTimeMillis() - crawlStartTime) / 1000);
        log.info("all crawled html text take size : {} KB", ( textContentBytes.get().divide(BigInteger.valueOf(1000))));
        log.info("unCrawled urls in queue : {}", domains.size());

    }
}
