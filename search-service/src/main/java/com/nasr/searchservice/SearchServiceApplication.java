package com.nasr.searchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class SearchServiceApplication {

    public static void main(String[] args) throws IOException {
       SpringApplication.run(SearchServiceApplication.class, args);
    }
}


