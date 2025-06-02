package com.nasr.searchservice.service.impl;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.service.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.nasr.searchservice.constant.ApplicationConstant.ETL_DATA_GROUP_ID;
import static com.nasr.searchservice.constant.ApplicationConstant.ETL_DATA_TOPIC_NAME;
import static com.nasr.searchservice.util.MapperUtility.map;

@Service
public class KafkaETLDataConsumerService implements KafkaConsumerService {


    @Autowired
    private EmbeddingBufferServiceImpl embeddingBufferService;

    @Override
    @KafkaListener(topics = ETL_DATA_TOPIC_NAME, groupId = ETL_DATA_GROUP_ID)
    public void consume(String data) {

        ETLData etlData = map(ETLData.class)
                .apply(data);

        embeddingBufferService.addMessage(etlData);
    }
}
