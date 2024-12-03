package com.nasr.crawlersubscriber.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.nasr.crawlersubscriber.constant.ApplicationConstant.*;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ETLDataTopic(){
        return TopicBuilder.name(ETL_DATA_TOPIC_NAME)
                .partitions(ETL_DATA_TOPIC_PARTITION_NUMBER)
                .replicas(ETL_DATA_TOPIC_REPLICA_NUMBER)
                .build();

    }
}
