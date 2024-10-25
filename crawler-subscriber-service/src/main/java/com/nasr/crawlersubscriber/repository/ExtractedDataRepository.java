package com.nasr.crawlersubscriber.repository;

import com.nasr.crawlersubscriber.entities.ExtractedDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExtractedDataRepository extends MongoRepository<ExtractedDataEntity,String> {
}
