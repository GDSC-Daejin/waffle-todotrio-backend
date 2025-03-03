package com.example.todo.service;

import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MLService {
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://1c52-112-152-82-7.ngrok-free.app";
    private static final String ML_SERVER_URL = BASE_URL + "/category";
    private static final String SUCCESS_PROB_API_URL = BASE_URL + "/prob";
    private static final String TRAIN_API_URL = BASE_URL + "/train";

    public String predictCategory(String content, String title) {
        try {
            MLRequestDto request = new MLRequestDto(content, title);
            ResponseEntity<MLResponseDto> response = restTemplate.postForEntity(
                    ML_SERVER_URL,
                    request,
                    MLResponseDto.class
            );

            if (response.getBody() != null) {
                return response.getBody().getCategory();
            }

            return "기타";
        } catch (Exception e) {
            log.error("ML 서버 통신 중 오류 발생: ", e);
            return "기타";
        }
    }

    public String predictSuccess(Todo todo) {
        try {
            if (todo == null || todo.getDeadline() == null) {
                log.warn("유효하지 않은 Todo 데이터");
                return "50%";
            }

            PredictSuccessRequestDto request = new PredictSuccessRequestDto(
                    todo.getPriority().toString(),
                    todo.getCreatedDate().toString(),
                    todo.getDeadline().toString(),
                    todo.getCategory(),
                    todo.getStatus().toString()
            );

            log.debug("ML 서버 요청 데이터: {}", request);

            try {
                ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                        SUCCESS_PROB_API_URL,
                        request,
                        String.class
                );

                if (rawResponse.getBody() != null) {
                    String jsonFixed = rawResponse.getBody().replace('\'', '"');
                    ObjectMapper mapper = new ObjectMapper();
                    PredictSuccessResponseDto responseDto = mapper.readValue(jsonFixed, PredictSuccessResponseDto.class);
                    return responseDto.getProbability() + "";
                }
            } catch (Exception e) {
                log.error("ML 서버 통신 오류: {}", e.getMessage());
                return "50%";
            }

            return "50%";
        } catch (Exception e) {
            log.error("성공 확률 예측 중 오류 발생: {}", e.getMessage());
            return "50%";
        }
    }

    public TrainModelResponseDto trainModel(Todo todo) {
        try {
            TrainModelRequestDto request = new TrainModelRequestDto(
                    todo.getPriority().toString(),
                    todo.getCreatedDate().toString(),
                    todo.getDeadline().toString(),
                    todo.getCategory(),
                    todo.getStatus().toString()
            );

            log.debug("학습 요청 데이터: {}", request);

            ResponseEntity<TrainModelResponseDto> response = restTemplate.postForEntity(
                    TRAIN_API_URL,
                    request,
                    TrainModelResponseDto.class
            );

            if (response.getBody() != null) {
                log.debug("학습 결과: {}", response.getBody());
                return response.getBody();
            }

            throw new RuntimeException("학습 결과가 없습니다.");
        } catch (Exception e) {
            log.error("모델 학습 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("모델 학습 중 오류 발생: " + e.getMessage());
        }
    }
}