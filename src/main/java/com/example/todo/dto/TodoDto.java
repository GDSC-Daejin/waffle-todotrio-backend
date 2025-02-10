package com.example.todo.dto;

import com.example.todo.entity.Todo;
import com.example.todo.enums.Priority;
import com.example.todo.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Todo DTO - 할일 데이터 전송 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    @Schema(description = "할일 ID", example = "1")
    private Long id;                    // Todo 고유 ID
    @Schema(description = "제목", example = "스프링 공부하기")
    private String title;               // 제목
    @Schema(description = "내용", example = "JPA 학습하기")
    private String content;             // 내용
    @Schema(description = "우선순위", example = "HIGH")
    private Priority priority;          // 우선순위 (HIGH, MEDIUM, LOW)

    @Schema(description = "시작기한", example = "2026-03-01T12:00:00")
    private LocalDateTime startDate;
    @Schema(description = "마감기한", example = "2026-02-01T12:00:00")
    private LocalDateTime deadline;     // 마감기한
    @Schema(description = "상태", example = "IN_PROGRESS")
    private TodoStatus status;          // 상태 (IN_PROGRESS, COMPLETED, DELAYED)
    private LocalDateTime createdDate;  // 생성일시
    private LocalDateTime completedDate; // 완료일시

    /**
     * Todo 엔티티를 DTO로 변환
     * @param todo Todo 엔티티
     * @return TodoDto 객체
     */
    public static TodoDto from(Todo todo) {
        return new TodoDto(
                todo.getId(),
                todo.getTitle(),
                todo.getContent(),
                todo.getPriority(),
                todo.getStartDate(),
                todo.getDeadline(),
                todo.getStatus(),
                todo.getCreatedDate(),
                todo.getCompletedDate()
        );
    }
}