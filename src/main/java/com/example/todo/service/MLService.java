package com.example.todo.service;

import com.example.todo.dto.MLRequestDto;
import com.example.todo.dto.MLResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// MLService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class MLService {
    private final RestTemplate restTemplate;
    private static final String ML_SERVER_URL = "http://localhost:5000/predict"; // ML 서버 주소

    public String predictCategory(String content, String title) {
        try {
            // 요청 데이터 준비
            MLRequestDto request = new MLRequestDto();
            request.setContent(content);
            request.setTitle(title);

            // ML 서버로 요청 보내기
            ResponseEntity<MLResponseDto> response = restTemplate.postForEntity(
                    ML_SERVER_URL,
                    request,
                    MLResponseDto.class
            );

            // 응답에서 카테고리 추출
            if (response.getBody() != null) {
                return response.getBody().getCategory();
            }

            return "기타"; // 기본 카테고리

        } catch (Exception e) {
            log.error("ML 서버 통신 중 오류 발생: ", e);
            return "기타"; // 에러 시 기본 카테고리
        }
    }
}