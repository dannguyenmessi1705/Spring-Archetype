package com.didan.archetype.service;

import org.springframework.stereotype.Service;

@Service
public interface ErrorService {
  String getErrorDetail(String errorCode, String language); // Hàm này dùng để lấy thông tin lỗi từ file properties
}
