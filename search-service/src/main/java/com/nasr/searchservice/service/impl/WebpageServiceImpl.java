package com.nasr.searchservice.service.impl;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.entities.WebpageEntity;
import com.nasr.searchservice.repository.WebpageRepository;
import com.nasr.searchservice.service.WebpageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebpageServiceImpl implements WebpageService {

    @Autowired
    private WebpageRepository repository;

    @Override
    public WebpageEntity save(ETLData etlWebpageData) {

        WebpageEntity entity = convertDTOToEntity(etlWebpageData);

        return repository.save(entity) ;
    }

    private WebpageEntity convertDTOToEntity(ETLData data) {
        return new WebpageEntity(data.getId(),data.getTitle(),data.getUrl(),data.getBody());
    }
}
