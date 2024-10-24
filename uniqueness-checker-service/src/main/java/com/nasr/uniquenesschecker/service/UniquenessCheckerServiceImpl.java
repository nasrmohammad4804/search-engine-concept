package com.nasr.uniquenesschecker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UniquenessCheckerServiceImpl implements UniquenessCheckerService {

    private static final String VISITED_URLS ="visited_urls";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<String> foundUrlNotExists(Set<String> urls) {

        List<String> uniqueUrls = new ArrayList<>();

        for (String url : urls) {

            Long result = redisTemplate.opsForSet().add(VISITED_URLS, url);

            boolean isUnique = checkIsUnique(result);
            if (isUnique)
                uniqueUrls.add(url);
        }
        return uniqueUrls;
    }



    private boolean checkIsUnique(Long result) {
        return (result!=null && result>0);
    }

}
