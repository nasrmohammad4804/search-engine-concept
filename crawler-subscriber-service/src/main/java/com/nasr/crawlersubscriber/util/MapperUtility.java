package com.nasr.crawlersubscriber.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.protocol.types.Field;

import java.util.function.Function;

public class MapperUtility {

    private static final ObjectMapper mapper = new ObjectMapper();

     public static  <T> Function<String,T> map(Class<T> classType){
         return (source) -> {
             try {
                 return mapper.readValue(source,classType);
             } catch (JsonProcessingException e) {
                 throw new RuntimeException("cant read value from type of : "+classType);
             }
         };
     }
}
