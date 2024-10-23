package com.nasr.crawlersubscriber.service.impl;

import com.nasr.crawlersubscriber.dto.ExtractedData;
import com.nasr.crawlersubscriber.entities.ExtractedDataEntity;
import com.nasr.crawlersubscriber.service.ExtractedDataService;
import com.nasr.crawlersubscriber.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.nasr.crawlersubscriber.constant.ApplicationConstant.*;
import static com.nasr.crawlersubscriber.util.MapperUtility.*;

@Component
@Slf4j
public class KafkaWebpageConsumerService implements KafkaConsumerService {

    @Autowired
    private ExtractedDataService extractedDataService;

    @Override
    @KafkaListener(topics = WEB_PAGE_TOPIC_NAME,groupId = WEB_PAGE_GROUP_ID,containerFactory = "containerFactory")
    public void consume(String data) {

        ExtractedData extractedData = map(ExtractedData.class)
                .apply(data);

        ExtractedDataEntity entity = extractedDataService.save(extractedData);
        log.info("extracted data saved with id : {}",entity.getId());
    }
}
