package com.example.todo.dto;

import com.example.todo.entity.TodoComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "댓글 정보 DTO")
public class CommentDto {
    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "이 부분 확인해주세요!")
    private String content;

    @Schema(description = "작성자", example = "user123")
    private String username;

    @Schema(description = "작성일시")
    private LocalDateTime createdDate;

    public static CommentDto from(TodoComment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUsername(comment.getUser().getUsername());
        dto.setCreatedDate(comment.getCreatedDate());
        return dto;
    }
}