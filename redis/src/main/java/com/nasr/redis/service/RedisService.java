package com.nasr.redis.service;

public interface RedisService {

    boolean checkKeyExists(String key);

    void saveKey(String key);
}
