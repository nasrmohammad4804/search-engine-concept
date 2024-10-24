package com.nasr.crawlerservice.service.external;

import com.nasr.crawlerservice.config.UniquenessCheckerExternalProperties;
import com.nasr.crawlerservice.domain.external.UniquenessCheckerItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Service
public class UniquenessCheckerExternalService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UniquenessCheckerExternalProperties properties;

    public List<String> getDistinctUrl(Set<String> urls) {

        String uri = properties.getBaseurl() + properties.getCheckEndPoint();

        ResponseEntity<?> responseEntity = restTemplate.postForEntity(uri, new UniquenessCheckerItemRequest(urls), List.class);
        return (List<String>) responseEntity.getBody();
    }
}
