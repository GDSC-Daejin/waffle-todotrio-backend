package com.example.todo.entity;

import com.example.todo.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TodoHistory Entity - 할일 변경 이력
 * todo: 변경된 할일
 * user: 변경한 사용자
 * actionType: 변경 유형(생성/수정/삭제/완료)
 * createdDate: 변경일
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TodoHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Todo todo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private LocalDateTime createdDate;
}
