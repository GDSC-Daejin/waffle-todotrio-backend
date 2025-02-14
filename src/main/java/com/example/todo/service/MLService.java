package com.example.todo.service;

import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

// MLService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class MLService {
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://d772-112-152-82-7.ngrok-free.app";
    private static final String ML_SERVER_URL = BASE_URL + "/category";
    private static final String SUCCESS_PROB_API_URL = BASE_URL + "/prob";
    private static final String TRAIN_API_URL = BASE_URL + "/train";
    //카테고리 예측
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

    public Double predictSuccess(Todo todo) {
        try {
            if (todo == null || todo.getDeadline() == null) {
                log.warn("유효하지 않은 Todo 데이터");
                return 0.0;
            }
            PredictSuccessRequestDto request = new PredictSuccessRequestDto();
            request.setPriority(todo.getPriority().toString());
            request.setCreatedDate(todo.getCreatedDate().toString());
            request.setDeadline(todo.getDeadline().toString());
            request.setCategory(todo.getCategory());
            request.setStatus(null);

            log.info("ML 서버 요청 데이터: {}", request);

            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                    SUCCESS_PROB_API_URL,
                    request,
                    String.class
            );

            if (rawResponse.getBody() != null) {
                String jsonFixed = rawResponse.getBody().replace('\'', '"');
                ObjectMapper mapper = new ObjectMapper();
                PredictSuccessResponseDto responseDto = mapper.readValue(jsonFixed, PredictSuccessResponseDto.class);

                // 백분율 문자열("87%")을 Double로 변환
                String probString = responseDto.getProbability().toString().replace("%", "");
                return Double.parseDouble(probString) / 100.0;
            }

            return 0.0;
        } catch (Exception e) {
            log.error("성공 확률 예측 중 오류 발생: {}", e.getMessage());
            return 0.0;
        }
    }


    public TrainModelResponseDto trainModel(Todo todo) {
        try {
            TrainModelRequestDto request = new TrainModelRequestDto();
            request.setPriority(todo.getPriority().toString());
            request.setCreatedDate(todo.getCreatedDate().toString());
            request.setDeadline(todo.getDeadline().toString());
            request.setCategory(todo.getCategory());
            request.setStatus(null);

            log.info("학습 요청 데이터: {}", request);

            ResponseEntity<TrainModelResponseDto> response = restTemplate.postForEntity(
                    TRAIN_API_URL,
                    request,
                    TrainModelResponseDto.class
            );

            if (response.getBody() != null) {
                log.info("학습 결과: {}", response.getBody());
                return response.getBody();
            }

            throw new RuntimeException("학습 결과가 없습니다.");
        } catch (Exception e) {
            log.error("모델 학습 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("모델 학습 중 오류 발생: " + e.getMessage());
        }
    }
}