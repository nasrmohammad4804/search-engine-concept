package com.nasr.crawlerservice.domain;

import lombok.Builder;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExtractedData {

    private String title;
    private int responseStatus;
    private String LastModifiedResponseHeader;
    private String content;
    private List<String> links = new ArrayList<>();
}
