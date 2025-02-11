package com.example.todo.dto;

import com.example.todo.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "할일 생성 요청 DTO")
public class TodoRequestDto {
    @Schema(description = "제목", example = "스프링 공부하기")
    private String title;

    @Schema(description = "내용", example = "JPA 개념 학습")
    private String content;

    @Schema(description = "우선순위", example = "HIGH")
    private Priority priority;

    @Schema(description = "시작기한", example = "2024-04-01T12:00:00")
    private LocalDateTime startDate;

    @Schema(description = "마감기한", example = "2024-02-01T12:00:00")
    private LocalDateTime deadline;

    @Schema(description = "ML 이 카테고리 넣는곳", example = "아무 카테고리")
    private String category;
}