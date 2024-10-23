package com.nasr.crawlerservice.service.external;

import com.nasr.crawlerservice.config.UniquenessCheckerExternalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UniquenessCheckerExternalService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UniquenessCheckerExternalProperties properties;

    public ResponseEntity<List<String>> getDistinctUrl(List<String> urls){
        return restTemplate.exchange(
                properties.getBaseurl() + properties.getCheckEndPoint(),
                HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<String>>() {
                }, urls
        );
    }
}
