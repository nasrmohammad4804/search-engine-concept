package com.nasr.crawlerservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.nasr.crawlerservice.constant.ApplicationConstant.*;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic webpageTopic(){
        return TopicBuilder.name(WEB_PAGE_TOPIC_NAME)
                .partitions(WEB_PAGE_TOPIC_PARTITION_NUMBER)
                .replicas(WEB_PAGE_TOPIC_REPLICA_NUMBER)
                .build();

    }
}
