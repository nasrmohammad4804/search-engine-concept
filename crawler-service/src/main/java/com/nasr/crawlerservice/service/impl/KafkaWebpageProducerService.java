package com.nasr.crawlerservice.service.impl;

import com.nasr.crawlerservice.constant.ApplicationConstant;
import com.nasr.crawlerservice.domain.ExtractedData;
import com.nasr.crawlerservice.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaWebpageProducerService extends KafkaProducerService<ExtractedData> {

    @Autowired
    public KafkaWebpageProducerService(KafkaTemplate<String, ExtractedData> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish(ExtractedData data) {
        kafkaTemplate.send(ApplicationConstant.WEB_PAGE_TOPIC_NAME,data);
    }
}
