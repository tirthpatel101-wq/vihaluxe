package com.vihaluxe.repository;

import com.vihaluxe.model.CustomCandle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomCandleRepository extends JpaRepository<CustomCandle, Long> {
}