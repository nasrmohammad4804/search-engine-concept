package com.nasr.searchservice.service.impl;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.dto.EmbeddingDataRequest;
import com.nasr.searchservice.external.EmbeddingExternalService;
import com.nasr.searchservice.service.EmbeddingBufferService;
import com.nasr.searchservice.service.WebpageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Service
public class EmbeddingBufferServiceImpl implements EmbeddingBufferService {

    private final List<ETLData> bufferMessages = Collections.synchronizedList(new ArrayList<>());
    private static final int BUFFER_SIZE = 50;
    private final ReentrantLock lock;
    private final EmbeddingExternalService embeddingExternalService;
    private final WebpageService webpageService;

    public EmbeddingBufferServiceImpl(EmbeddingExternalService embeddingExternalService, WebpageService webpageService) {
        this.embeddingExternalService = embeddingExternalService;
        this.webpageService = webpageService;
        lock = new ReentrantLock();
    }

    @Override
    public void addMessage(ETLData message) {
        bufferMessages.add(message);

        if (bufferMessages.size() >= BUFFER_SIZE)
            flushBuffer();
    }

    @Override
    public void flushBuffer() {

        try {

            lock.lock();
            if (bufferMessages.size() != BUFFER_SIZE) return;
            List<ETLData> batchData = new ArrayList<>(bufferMessages);
            bufferMessages.clear();

            List<String> messageBodies = batchData.stream().map(ETLData::getBody).toList();
            List<List<Float>> batchEmbeddingData = embeddingExternalService
                    .getBatchEmbeddingData(new EmbeddingDataRequest(messageBodies));
            saveBatchData(batchEmbeddingData, batchData);


        } finally {
            lock.unlock();
        }
    }

    private void saveBatchData(List<List<Float>> batchEmbeddingData, List<ETLData> data) {

        IntStream.range(0, data.size())
                .forEach(index -> data.get(index).setDimensions(batchEmbeddingData.get(index)));

        webpageService.saveAll(data);

    }

}
