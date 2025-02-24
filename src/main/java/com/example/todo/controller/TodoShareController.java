package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.ShareRequestDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.service.TodoShareService;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos/share")
@RequiredArgsConstructor
@Tag(name = "TodoShare", description = "할일 공유 API")
public class TodoShareController {
    private final TodoShareService shareService;
    private final UserService userService;

    @PostMapping("/{todoId}")
    @Operation(summary = "Todo 공유하기")
    public ResponseDto<String> shareTodo(
            @PathVariable Long todoId,
            @RequestBody ShareRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        shareService.shareTodo(todoId, requestDto.getUsername(), requestDto.getPermission());
        return ResponseDto.success("공유되었습니다");
    }


    @GetMapping("/shared")
    @Operation(summary = "공유받은 Todo 목록 조회")
    @Transactional(readOnly = true)
    public ResponseDto<List<TodoDto>> getSharedTodos(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Todo> todos = shareService.getSharedTodos(user);
        List<TodoDto> todoDtos = todos.stream()
                .map(TodoDto::from)
                .collect(Collectors.toList());

        return ResponseDto.success(todoDtos);
    }

    @DeleteMapping("/{todoId}/share/{username}")
    @Operation(summary = "Todo 공유 취소")
    public ResponseDto<String> cancelShare(
            @PathVariable Long todoId,
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        shareService.cancelShare(todoId, username);
        return ResponseDto.success("공유가 취소되었습니다");
    }

    @PutMapping("/{todoId}/share/{username}/permission")
    @Operation(summary = "공유 권한 수정")
    public ResponseDto<String> updateSharePermission(
            @PathVariable Long todoId,
            @PathVariable String username,
            @RequestBody ShareRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        shareService.updatePermission(todoId, username, requestDto.getPermission());
        return ResponseDto.success("권한이 수정되었습니다");
    }

    @GetMapping("/{todoId}/sharer")
    @Operation(summary = "Todo를 공유해준 사용자 조회")
    public ResponseDto<String> getSharerUsername(
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String sharerUsername = shareService.getSharerUsername(todoId, userDetails.getUsername());
        return ResponseDto.success(sharerUsername);
    }

}