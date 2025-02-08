package com.example.todo.entity;

import com.example.todo.enums.Priority;
import com.example.todo.enums.TodoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Todo Entity - 할일 정보
 * title: 제목
 * content: 내용
 * status: 상태(진행중/완료/지연)
 * priority: 우선순위(상/중/하)
 * deadline: 마감기한
 * createdDate: 생성일
 * completedDate: 완료일
 * owner: 작성자
 * group: 소속 그룹
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Todo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private TodoStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDateTime deadline;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private TodoGroup group;
}
