package com.nasr.crawlerservice.service.impl;

import com.nasr.crawlerservice.constant.ApplicationConstant;
import com.nasr.crawlerservice.domain.ExtractedData;
import com.nasr.crawlerservice.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaWebpageProducerService implements KafkaProducerService<ExtractedData> {

    @Autowired
    private KafkaTemplate<String, ExtractedData> kafkaTemplate;

    public void publish(ExtractedData data) {
        kafkaTemplate.send(ApplicationConstant.WEB_PAGE_TOPIC_NAME,data);
    }
}
