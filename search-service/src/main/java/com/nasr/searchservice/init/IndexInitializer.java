package com.nasr.searchservice.init;

import com.nasr.searchservice.entities.WebpageEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import static com.nasr.searchservice.constant.WebpageSettingTemplate.SETTING;
import static com.nasr.searchservice.entities.WebpageEntity.*;

@Component
public class IndexInitializer {


    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {

        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(INDEX_NAME));
        if (!indexOps.exists())
            createIndex(indexOps);
    }

    private void createIndex(IndexOperations indexOperations) {

        Document settings = Document.parse(SETTING);
        indexOperations.create(settings);
        indexOperations.putMapping(WebpageEntity.class);
    }
}
