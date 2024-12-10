package com.nasr.crawlersubscriber.service.impl;

import com.nasr.crawlersubscriber.dto.ETLData;
import com.nasr.crawlersubscriber.dto.ExtractedData;
import com.nasr.crawlersubscriber.entities.ExtractedDataEntity;
import com.nasr.crawlersubscriber.service.ExtractedDataService;
import com.nasr.crawlersubscriber.service.KafkaConsumerService;
import com.nasr.crawlersubscriber.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.nasr.crawlersubscriber.constant.ApplicationConstant.WEB_PAGE_GROUP_ID;
import static com.nasr.crawlersubscriber.constant.ApplicationConstant.WEB_PAGE_TOPIC_NAME;
import static com.nasr.crawlersubscriber.util.MapperUtility.map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaWebpageConsumerService implements KafkaConsumerService {

    private final ExtractedDataService extractedDataService;

    private final KafkaProducerService<ETLData> kafkaProducerService;

    @Override
    @KafkaListener(topics = WEB_PAGE_TOPIC_NAME,groupId = WEB_PAGE_GROUP_ID,containerFactory = "containerFactory")
    public void consume(String data) {

        ExtractedData extractedData = map(ExtractedData.class)
                .apply(data);

        ExtractedDataEntity entity = extractedDataService.save(extractedData);

        ETLData etlData = convertEntityToEtlData(entity);
        kafkaProducerService.publish(etlData);

        log.info("extracted data saved with id : {}",entity.getId());
    }

    private ETLData convertEntityToEtlData(ExtractedDataEntity entity) {
        return ETLData.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .title(entity.getTitle())
                .body(entity.getContent())
                .iconUrl(entity.getIconUrl())
                .siteName(entity.getSiteName())
                .build();
    }
}
