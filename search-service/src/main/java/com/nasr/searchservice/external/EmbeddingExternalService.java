package com.nasr.searchservice.external;

import com.nasr.searchservice.dto.EmbeddingDataRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "embedding-service", url = "http://localhost:5000")
public interface EmbeddingExternalService {

    @GetMapping("/embed")
    List<Float> getEmbeddingData(@RequestParam String data);

    @PostMapping("/batch-embed")
    List<List<Float>> getBatchEmbeddingData(@RequestBody EmbeddingDataRequest request);

}
