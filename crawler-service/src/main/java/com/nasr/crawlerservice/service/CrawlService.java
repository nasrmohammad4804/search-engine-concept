package com.nasr.crawlerservice.service;

import java.io.IOException;

public interface CrawlService {

    void crawl(String domain) throws IOException;
}
