package com.example.todo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TodoComment Entity - 할일 댓글
 * todo: 댓글이 달린 할일
 * user: 댓글 작성자
 * content: 댓글 내용
 * createdDate: 작성일
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TodoComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Todo todo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String content;

    private LocalDateTime createdDate;
}
