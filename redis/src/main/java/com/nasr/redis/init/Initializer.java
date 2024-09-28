package com.nasr.redis.init;

import com.nasr.redis.service.RedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Initializer {

    public static final String INPUT_DELIMITER = ":";
    public static final String FILE_NAME = "data.txt";

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void initialize() throws IOException {

        checkDuplicateData(extractDataFromFile());
    }


    private List<String> extractDataFromFile() throws IOException {

        List<String> infos = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] data = line.split(INPUT_DELIMITER);
            infos.addAll(List.of(data));
        }
        return infos;

    }

    private void checkDuplicateData(List<String> information) {


        int count = 0;

        long startTime = System.currentTimeMillis();

        for (String data : information) {
            if (!redisService.checkKeyExists(data)) {
                redisService.saveKey(data);
            } else {
                count++;
            }
        }

        System.out.println("duplicate string count  : " + count);
        System.out.println("processed at : " + (System.currentTimeMillis() - startTime));
        System.out.println("percent of duplicate data : " + ((double) count / information.size()) * 100);

    }
}
