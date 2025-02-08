package com.example.todo.dto;

import com.example.todo.enums.Priority;
import com.example.todo.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "할일 검색 요청 DTO")
public class TodoSearchRequestDto {
    @Schema(description = "검색어", example = "스프링")
    private String keyword;

    @Schema(description = "상태", example = "IN_PROGRESS")
    private TodoStatus status;

    @Schema(description = "우선순위", example = "HIGH")
    private Priority priority;

    @Schema(description = "시작일", example = "2024-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "종료일", example = "2024-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "정렬 기준", example = "deadline")
    private String sortBy = "deadline";

    @Schema(description = "정렬 방향", example = "asc")
    private String sortDirection = "asc";
}
