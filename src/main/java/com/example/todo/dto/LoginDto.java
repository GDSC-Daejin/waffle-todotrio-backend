package com.example.todo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 요청 DTO")
public class LoginDto {
    @Schema(description = "사용자명", example = "user123")
    private String username;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}