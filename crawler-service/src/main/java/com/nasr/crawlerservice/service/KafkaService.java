package com.nasr.crawlerservice.service;

public interface KafkaService<T> {

    void publish(T data);
}
