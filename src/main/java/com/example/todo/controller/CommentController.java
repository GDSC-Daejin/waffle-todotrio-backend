package com.example.todo.controller;

import com.example.todo.dto.CommentDto;
import com.example.todo.dto.CommentRequestDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos/{todoId}/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 작성")
    public ResponseDto<CommentDto> createComment(
            @PathVariable Long todoId,
            @RequestBody @Valid CommentRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(commentService.createComment(todoId, request, userDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "댓글 목록 조회")
    public ResponseDto<List<CommentDto>> getComments(@PathVariable Long todoId) {
        try {
            log.info("Fetching comments for todo id: {}", todoId);
            List<CommentDto> comments = commentService.getCommentsByTodoId(todoId);
            log.info("Found {} comments", comments.size());
            return ResponseDto.success(comments);
        } catch (Exception e) {
            log.error("Error fetching comments: ", e);
            return ResponseDto.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseDto<String> deleteComment(
            @PathVariable Long todoId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseDto.success("댓글이 삭제되었습니다");
    }
}
