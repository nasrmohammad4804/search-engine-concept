package com.nasr.crawlerservice.service;

public interface KafkaProducerService<T> {

    void publish(T data);
}
