package com.nasr.crawlersubscriber.service;

import org.springframework.kafka.core.KafkaTemplate;

public abstract class KafkaProducerService<T> {

    protected final KafkaTemplate<String, T> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public  abstract void publish(T data);
}
