package com.example.todo.dto;

import com.example.todo.enums.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "할일 공유 요청 DTO")
public class ShareRequestDto {
    @Schema(description = "공유 대상 사용자명", example = "shareuser")
    @NotBlank(message = "사용자 이름은 필수입니다")
    private String username;

    @Schema(description = "공유 권한 (READ/WRITE)", example = "READ")
    @NotNull(message = "권한은 필수입니다")
    private PermissionType permission;
}