package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.UserDto;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    @Operation(summary = "회원 정보 보여주기")
    public ResponseDto<String> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseDto.success("Hello " + userDetails.getUsername());
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping("/update")
    public ResponseDto<String> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDto userDto) {
        try {
            userService.updateUser(userDetails.getUsername(), userDto);
            return ResponseDto.success("회원정보 수정 성공");
        } catch (RuntimeException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴")
    public ResponseDto<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.deleteUser(userDetails.getUsername());
            return ResponseDto.success("회원 탈퇴 성공");
        } catch (RuntimeException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }
}