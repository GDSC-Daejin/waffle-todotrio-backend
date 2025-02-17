package com.example.todo.controller;

import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Todo", description = "할일 관리 API")
public class TodoController {
    private final TodoService todoService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "할일 목록 조회")
    public ResponseDto<List<TodoDto>> getTodos(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(todoService.getTodosByUser(getUser(userDetails)).stream()
                .map(TodoDto::from)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(
            summary = "할일 생성",
            description = "새로운 할일을 생성합니다."
    )
    public ResponseDto<TodoDto> createTodo(
            @RequestBody @Valid TodoRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(TodoDto.from(
                todoService.createTodo(requestDto, getUser(userDetails))));
    }

    @PutMapping("/{todoId}")
    @Operation(summary = "할일 수정")
    public ResponseDto<TodoDto> updateTodo(
            @PathVariable Long todoId,
            @RequestBody @Valid TodoRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(TodoDto.from(
                todoService.updateTodo(todoId, requestDto, getUser(userDetails))));
    }

    @DeleteMapping("/{todoId}")
    @Operation(summary = "할일 삭제")
    public ResponseDto<String> deleteTodo(
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        todoService.deleteTodo(todoId, getUser(userDetails));
        return ResponseDto.success("할일이 삭제되었습니다.");
    }

    @PutMapping("/{todoId}/complete")
    @Operation(summary = "할일 완료 처리")
    public ResponseDto<TodoDto> completeTodo(
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(TodoDto.from(
                todoService.completeTodo(todoId, getUser(userDetails))));
    }

    private User getUser(UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }
    // 지연 처리
    @PutMapping("/{todoId}/delay")
    @Operation(summary = "할일 지연 처리")
    public ResponseDto<TodoDto> delayTodo(
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(TodoDto.from(
                todoService.delayTodo(todoId, getUser(userDetails))));
    }

    @GetMapping("/{todoId}/success-probability")
    @Operation(summary = "성공률 계산")
    public ResponseDto<String> getPredictedSuccess(
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(todoService.getPredictedSuccess(todoId, getUser(userDetails)));
    }


    @GetMapping("/statistics")
    @Operation(summary = "Todo 통계 조회")
    public ResponseDto<TodoStatisticsDto> getStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success(todoService.getTodoStatistics(getUser(userDetails)));
    }

    @GetMapping("/search")
    @Operation(summary = "할일 검색/필터링", description = "키워드, 상태, 우선순위로 할일을 검색합니다")
    public ResponseDto<List<TodoDto>> searchTodos(
            @ModelAttribute TodoSearchRequestDto searchDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(todoService.searchTodos(searchDto, getUser(userDetails)));
    }

}