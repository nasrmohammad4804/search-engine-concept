package com.nasr.searchservice.service;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.dto.SearchResult;
import com.nasr.searchservice.entities.WebpageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface WebpageService {

    WebpageEntity save(ETLData etlWebpageData);

    Iterable<WebpageEntity> saveAll(List<ETLData> etlWebpageData);

    List<String> suggest(String query) throws IOException;

    SearchResult search(String query, Pageable pageable) throws IOException;

    SearchResult embedSearch(String query, Pageable pageable) throws IOException;
}
