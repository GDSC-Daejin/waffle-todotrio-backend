package com.example.todo.entity;

import com.example.todo.enums.PermissionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
* TodoShare Entity - 할일 공유 정보
* todo: 공유할 할일
* user: 공유받는 사용자
* permissionType: 권한 유형(읽기/쓰기)
*/
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TodoShare {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Todo todo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;
}
