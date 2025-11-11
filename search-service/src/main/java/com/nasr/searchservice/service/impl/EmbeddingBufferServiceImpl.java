package com.nasr.searchservice.service.impl;

import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.service.EmbeddingBufferService;
import com.nasr.searchservice.service.WebpageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class EmbeddingBufferServiceImpl implements EmbeddingBufferService {

    private final List<ETLData> bufferMessages = Collections.synchronizedList(new ArrayList<>());
    private static final int BUFFER_SIZE = 50;
    private final ReentrantLock lock;
    private final WebpageService webpageService;

    public EmbeddingBufferServiceImpl(WebpageService webpageService) {
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
            webpageService.saveAll(batchData);
            bufferMessages.clear();
        } finally {
            lock.unlock();
        }
    }

}
