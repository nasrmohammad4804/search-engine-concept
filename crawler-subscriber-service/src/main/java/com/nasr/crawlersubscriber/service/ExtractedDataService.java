package com.nasr.crawlersubscriber.service;

import com.nasr.crawlersubscriber.dto.ExtractedData;
import com.nasr.crawlersubscriber.entities.ExtractedDataEntity;

public interface ExtractedDataService {

    ExtractedDataEntity save(ExtractedData data);
}
