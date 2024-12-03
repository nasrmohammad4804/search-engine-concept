package com.nasr.crawlersubscriber.service.impl;

import com.nasr.crawlersubscriber.dto.ETLData;
import com.nasr.crawlersubscriber.service.KafkaProducerService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.nasr.crawlersubscriber.constant.ApplicationConstant.ETL_DATA_TOPIC_NAME;

@Component
public class KafkaETLDataProducerService extends KafkaProducerService<ETLData> {

    public KafkaETLDataProducerService(KafkaTemplate<String, ETLData> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(ETLData data) {
        kafkaTemplate.send(ETL_DATA_TOPIC_NAME,data);
    }
}
