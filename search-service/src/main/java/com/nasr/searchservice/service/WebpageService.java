package com.nasr.searchservice.service;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.entities.WebpageEntity;

public interface WebpageService {

    WebpageEntity save(ETLData etlWebpageData);

}
