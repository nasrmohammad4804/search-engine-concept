package com.nasr.uniquenesschecker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UniquenessCheckerServiceImpl implements UniquenessCheckerService {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<String> foundUrlNotExists(String[] urls) {

        List<String> result = new ArrayList<>();

        for (String url : urls) {

            boolean isExists = checkUrlExists(url);
            if (!isExists) {
                saveUrl(url);
                result.add(url);
            }
        }
        return result;
    }

    private boolean checkUrlExists(String url) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(url));
    }

    private void saveUrl(String url) {
        redisTemplate.opsForValue().set(url, String.valueOf(true));
    }

}
