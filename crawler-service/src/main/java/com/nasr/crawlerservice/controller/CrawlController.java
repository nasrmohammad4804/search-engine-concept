package com.nasr.crawlerservice.controller;

import com.nasr.crawlerservice.service.CrawlService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlController {

    @Autowired
    private CrawlService crawlService;

    @PostMapping("/crawl")
    public ResponseEntity<?> crawlDomain(@RequestParam @NotEmpty String domain) {
        crawlService.crawl(domain);
        return ResponseEntity.ok().build();
    }
}
