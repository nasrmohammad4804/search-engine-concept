package com.nasr.uniquenesschecker.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "redis")
@NoArgsConstructor
@AllArgsConstructor
public class RedisProperties {

    private String host;
    private int port;
}
