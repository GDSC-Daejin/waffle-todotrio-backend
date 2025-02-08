package com.example.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "할일 그룹 생성/수정 요청 DTO")
public class TodoGroupRequestDto {
    @Schema(description = "그룹명", example = "프로젝트")
    private String name;

    @Schema(description = "그룹 설명", example = "프로젝트 관련 할일들")
    private String description;
}