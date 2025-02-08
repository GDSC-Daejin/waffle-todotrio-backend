package com.example.todo.controller;

import com.example.todo.dto.LoginDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.UserDto;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final UserService userService;


    @Operation(
            summary = "회원 가입",
            description = "새로운 사용자를 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/signup")
    public ResponseDto<String> signup(@RequestBody UserDto userDto) {
        try {
            userService.signup(userDto);
            return ResponseDto.success("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    @Operation(
            summary = "로그인",
            description = "사용자 인증 후 JWT 토큰을 발급합니다."
    )
    @PostMapping("/login")
    public ResponseDto<String> login(@RequestBody LoginDto loginDto) {
        try {
            String token = userService.login(loginDto);
            return ResponseDto.success(token);
        } catch (RuntimeException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    @Operation(summary = "로그 아웃")
    @PostMapping("/logout")
    public ResponseDto<String> logout(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                return ResponseDto.success("로그아웃 성공");
            }
            return ResponseDto.fail("토큰이 없습니다");
        } catch (Exception e) {
            return ResponseDto.fail("로그아웃 실패: " + e.getMessage());
        }
    }
}