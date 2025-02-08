package com.example.todo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TodoGroup Entity - 할일 그룹
 * name: 그룹명
 * description: 그룹 설명
 * owner: 그룹 생성자
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TodoGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
}
