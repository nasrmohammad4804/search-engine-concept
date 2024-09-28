package com.nasr.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService{


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public boolean checkKeyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void saveKey(String key) {
        redisTemplate.opsForValue().set(key,String.valueOf(true));
    }
}
