package com.example.todo.dto;

import com.example.todo.enums.Priority;
import com.example.todo.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Schema(description = "할일 통계 정보 DTO")
public class TodoStatisticsDto {
    @Schema(description = "완료율", example = "75.5")
    private double completionRate;

    @Schema(description = "우선순위별 분포")
    private Map<Priority, Integer> priorityDistribution;

    @Schema(description = "상태별 분포")
    private Map<TodoStatus, Integer> statusDistribution;
}
