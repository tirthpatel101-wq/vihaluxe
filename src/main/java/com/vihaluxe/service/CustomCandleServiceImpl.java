package com.vihaluxe.service;

import com.vihaluxe.model.CustomCandle;
import com.vihaluxe.repository.CustomCandleRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomCandleServiceImpl implements CustomCandleService {

    private final CustomCandleRepository customCandleRepository;

    public CustomCandleServiceImpl(CustomCandleRepository customCandleRepository) {
        this.customCandleRepository = customCandleRepository;
    }

    @Override
    public CustomCandle save(CustomCandle customCandle) {
        return customCandleRepository.save(customCandle);
    }
}