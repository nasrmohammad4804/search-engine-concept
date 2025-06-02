package com.nasr.searchservice.service;

import com.nasr.searchservice.dto.ETLData;

public interface EmbeddingBufferService {

    void addMessage(ETLData message);

    void flushBuffer() throws InterruptedException;
}
