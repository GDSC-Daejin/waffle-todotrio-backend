package com.example.todo.entity;

import com.example.todo.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * User Entity - 사용자 정보
 * username: 로그인 ID
 * password: 암호화된 비밀번호
 * email: 사용자 이메일
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}
