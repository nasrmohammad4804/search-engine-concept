package com.nasr.crawlersubscriber.service.impl;

import com.nasr.crawlersubscriber.dto.ExtractedData;
import com.nasr.crawlersubscriber.entities.ExtractedDataEntity;
import com.nasr.crawlersubscriber.repository.ExtractedDataRepository;
import com.nasr.crawlersubscriber.service.ExtractedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtractedDataServiceImpl implements ExtractedDataService {

    @Autowired
    private ExtractedDataRepository repository;

    @Override
    public ExtractedDataEntity save(ExtractedData data) {

        ExtractedDataEntity entity = convertExtractedDataToEntity(data);
        return repository.save(entity);
    }
    private ExtractedDataEntity convertExtractedDataToEntity(ExtractedData data){

        return ExtractedDataEntity.Builder.builder()
                .content(data.getContent())
                .lastModifiedResponseHeader(data.getLastModifiedResponseHeader())
                .url(data.getUrl())
                .links(data.getLinks())
                .responseStatus(data.getResponseStatus())
                .title(data.getTitle())
                .iconUrl(data.getIconUrl())
                .siteName(data.getSiteName())
                .build();
    }
}
