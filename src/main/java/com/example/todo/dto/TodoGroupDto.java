package com.example.todo.dto;

import com.example.todo.entity.TodoGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoGroupDto {
    @Schema(description = "그룹 ID", example = "1")
    private Long id;

    @Schema(description = "그룹명", example = "프로젝트")
    private String name;

    @Schema(description = "그룹 설명", example = "프로젝트 관련 할일들")
    private String description;

    public static TodoGroupDto from(TodoGroup group) {
        return new TodoGroupDto(
                group.getId(),
                group.getName(),
                group.getDescription()
        );
    }
}
