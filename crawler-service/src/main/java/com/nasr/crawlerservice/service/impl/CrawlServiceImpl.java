package com.nasr.crawlerservice.service.impl;

import com.nasr.crawlerservice.domain.ExtractedData;
import com.nasr.crawlerservice.enumaration.ValidatorResult;
import com.nasr.crawlerservice.service.CrawlService;
import com.nasr.crawlerservice.service.external.UniquenessCheckerExternalService;
import com.nasr.crawlerservice.validator.CrawlValidator;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.nasr.crawlerservice.enumaration.ValidatorResult.*;
import static com.nasr.crawlerservice.util.CrawlerExtractionUtility.*;

@Service
@RequiredArgsConstructor
public class CrawlServiceImpl implements CrawlService {

    private final ConcurrentLinkedQueue<String> domains = new ConcurrentLinkedQueue<>();

    private final UniquenessCheckerExternalService uniquenessCheckerExternalService;

    private final KafkaWebpageProducerService kafkaProducerService;

    @Override
    public void crawl(String domain) throws IOException {

        domains.add(domain);
        startCrawlProcess();
    }

    public void startCrawlProcess() throws IOException {

        while (!domains.isEmpty()) {

            String url = domains.remove();
            Connection.Response response = Jsoup.connect(url).execute();
            ValidatorResult result = validateResponse(response);

            if (result == OK){
                ExtractedData extractedData = extract(response);
                List<String> distinctUrls = getDistinctUrls(extractedData.getLinks());
                kafkaProducerService.publish(extractedData);
                domains.addAll(distinctUrls);
            }
        }
    }

    private ValidatorResult validateResponse(Connection.Response response) {

        return CrawlValidator.verifyContentAccessible()
                .and(CrawlValidator.verifyContentAccessible())
                .apply(response);
    }
    private List<String> getDistinctUrls(List<String> urls){

        if (urls.isEmpty())
            return Collections.emptyList();

        return uniquenessCheckerExternalService.getDistinctUrl(urls).getBody();
    }

}
