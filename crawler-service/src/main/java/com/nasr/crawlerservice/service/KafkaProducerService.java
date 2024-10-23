package com.nasr.crawlerservice.service;

import com.nasr.crawlerservice.constant.ApplicationConstant;
import com.nasr.crawlerservice.domain.ExtractedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, ExtractedData> kafkaTemplate;

    public void publish(ExtractedData data) {
        kafkaTemplate.send(ApplicationConstant.WEB_PAGE_TOPIC_NAME,data);
    }
}
