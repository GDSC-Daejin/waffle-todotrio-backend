package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.TodoGroupDto;
import com.example.todo.dto.TodoGroupRequestDto;
import com.example.todo.entity.TodoGroup;
import com.example.todo.entity.User;
import com.example.todo.service.TodoGroupService;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 할 일 그룹 관리를 위한 컨트롤러 클래스.
 * 그룹 조회, 생성, 수정, 삭제에 관한 API를 제공
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Group", description = "그룹 관리 API")
public class TodoGroupController {

    private final TodoGroupService groupService;
    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "그룹 목록 조회",
            description = "사용자가 소유한 모든 그룹 목록을 조회합니다"
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseDto<List<TodoGroupDto>> getGroups(@AuthenticationPrincipal UserDetails userDetails) {
        List<TodoGroup> groups = groupService.getGroupsByUser(getUser(userDetails));
        return ResponseDto.success(groups.stream()
                .map(TodoGroupDto::from)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(
            summary = "그룹 생성",
            description = "새로운 그룹을 생성합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseDto<TodoGroupDto> createGroup(
            @RequestBody @Valid TodoGroupRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        TodoGroup group = groupService.createGroup(requestDto, getUser(userDetails));
        return ResponseDto.success(TodoGroupDto.from(group));
    }

    @PutMapping("/{groupId}")
    @Operation(
            summary = "그룹 수정",
            description = "기존 그룹의 정보를 수정합니다"
    )
    public ResponseDto<TodoGroupDto> updateGroup(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId,
            @RequestBody @Valid TodoGroupRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        TodoGroup group = groupService.updateGroup(groupId, requestDto, getUser(userDetails));
        return ResponseDto.success(TodoGroupDto.from(group));
    }

    @DeleteMapping("/{groupId}")
    @Operation(
            summary = "그룹 삭제",
            description = "그룹을 삭제합니다"
    )
    public ResponseDto<String> deleteGroup(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.deleteGroup(groupId, getUser(userDetails));
        return ResponseDto.success("그룹이 삭제되었습니다");
    }

    private User getUser(UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }
}