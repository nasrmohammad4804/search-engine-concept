package com.nasr.searchservice.service;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.dto.SearchDto;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface WebpageService {

    void saveAll(List<ETLData> etlWebpageData);

    List<SearchDto> search(String query, Pageable pageable) throws IOException;

    List<SearchDto> embedSearch(String query, Pageable pageable) throws IOException;
}
