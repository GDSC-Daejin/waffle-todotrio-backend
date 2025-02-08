package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.UserDto;
import com.example.todo.jwt.TokenProvider;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "OAuth2", description = "구글 OAuth2 로그인 API")
public class OAuth2LoginController {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    @Operation(
            summary = "구글 로그인 성공",
            description = "구글 OAuth2 로그인 성공 시 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 - JWT 토큰 반환",
                    content = @Content(schema = @Schema(
                            example = "{\"success\": true, \"data\": \"eyJhbGciOiJIUzI1...\"}"
                    ))
            )
    })
    @GetMapping("/login-success")
    public ResponseDto<String> loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String token = tokenProvider.createToken(email);
        return ResponseDto.success(token);
    }

    @Operation(
            summary = "구글 로그인 실패",
            description = "구글 OAuth2 로그인 실패 시 에러 메시지를 반환합니다."
    )
    @ApiResponse(
            responseCode = "400",
            description = "로그인 실패",
            content = @Content(schema = @Schema(
                    example = "{\"success\": false, \"message\": \"Google 로그인 실패\"}"
            ))
    )
    @GetMapping("/login-failure")
    public ResponseDto<String> loginFailure() {
        return ResponseDto.fail("Google 로그인 실패");
    }
}