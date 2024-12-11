package com.nasr.searchservice.service.impl;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.entities.WebpageEntity;
import com.nasr.searchservice.service.KafkaConsumerService;
import com.nasr.searchservice.service.WebpageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.nasr.searchservice.constant.ApplicationConstant.ETL_DATA_GROUP_ID;
import static com.nasr.searchservice.constant.ApplicationConstant.ETL_DATA_TOPIC_NAME;
import static com.nasr.searchservice.util.MapperUtility.map;

@Service
public class KafkaETLDataConsumerService implements KafkaConsumerService {

    @Autowired
    private WebpageService webpageService;

    @Override
    @KafkaListener(topics = ETL_DATA_TOPIC_NAME,groupId =ETL_DATA_GROUP_ID )
    public void consume(String data) {

        ETLData etlData = map(ETLData.class)
                .apply(data);

        webpageService.save(etlData);
    }
}
