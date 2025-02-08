package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoRequestDto;
import com.example.todo.dto.UserDto;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.todo.enums.Role;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController {

    private final UserService userService;
    private final TodoService todoService;

    @GetMapping("/users")
    @Operation(summary = "모든 사용자 조회")
    public ResponseDto<List<UserDto>> getAllUsers() {
        return ResponseDto.success(userService.getAllUsers());
    }

    @PutMapping("/users/{userId}/role")
    @Operation(summary = "사용자 역할 변경")
    public ResponseDto<String> changeUserRole(
            @PathVariable Long userId,
            @RequestBody Role role) {
        userService.changeUserRole(userId, role);
        return ResponseDto.success("역할이 변경되었습니다.");
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "사용자 계정 삭제")
    public ResponseDto<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseDto.success("사용자가 삭제되었습니다.");
    }

    @GetMapping("/todos")
    @Operation(summary = "모든 Todo 조회")
    public ResponseDto<List<TodoDto>> getAllTodos() {
        return ResponseDto.success(todoService.getAllTodos());
    }

    @PutMapping("/todos/{todoId}")
    @Operation(summary = "특정 Todo 수정")
    public ResponseDto<TodoDto> updateAnyTodo(
            @PathVariable Long todoId,
            @RequestBody TodoRequestDto request) {
        return ResponseDto.success(TodoDto.from(
                todoService.updateAnyTodo(todoId, request)));
    }
}
