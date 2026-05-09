package com.pori.sample.service;

import com.pori.global.exception.BusinessException;
import com.pori.sample.dto.request.SampleRequest;
import com.pori.sample.dto.response.SampleResponse;
import com.pori.sample.exception.SampleErrorCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SampleService {

    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, SampleResponse> samples = new ConcurrentHashMap<>();

    public List<SampleResponse> findAll() {
        return new ArrayList<>(samples.values());
    }

    public SampleResponse create(SampleRequest request) {
        Long id = idGenerator.incrementAndGet();
        SampleResponse response = new SampleResponse(id, request.title(), request.content());
        samples.put(id, response);
        return response;
    }

    public SampleResponse findById(Long id) {
        SampleResponse response = samples.get(id);
        if (response == null) {
            throw new BusinessException(SampleErrorCode.SAMPLE_NOT_FOUND);
        }
        return response;
    }

    public SampleResponse update(Long id, SampleRequest request) {
        findById(id);
        SampleResponse response = new SampleResponse(id, request.title(), request.content());
        samples.put(id, response);
        return response;
    }

    public void delete(Long id) {
        findById(id);
        samples.remove(id);
    }
}
