package com.nasr.crawlerservice.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "uniqueness.checker.server")
@Data
public class UniquenessCheckerExternalProperties {

    private String baseurl;
    private String checkEndPoint;
}
