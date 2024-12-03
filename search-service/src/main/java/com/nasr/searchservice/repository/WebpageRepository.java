package com.nasr.searchservice.repository;

import com.nasr.searchservice.entities.WebpageEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebpageRepository extends ElasticsearchRepository<WebpageEntity,String> {
}
