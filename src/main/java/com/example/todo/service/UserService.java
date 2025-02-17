package com.example.todo.service;

import com.example.todo.dto.LoginDto;
import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.enums.Role;
import com.example.todo.jwt.TokenProvider;
import com.example.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    // 사용자 데이터를 저장/조회하기 위한 UserRepository
    private final UserRepository userRepository;

    // 비밀번호 암호화를 위한 PasswordEncoder
    private final PasswordEncoder passwordEncoder;

    // JWT 토큰 생성을 담당하는 TokenProvider
    private final TokenProvider tokenProvider;
    @Transactional
    public void signup(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다");
        }

        // 새 사용자 객체 생성 및 설정
        User user = new User();
        user.setUsername(userDto.getUsername());// 사용자명 설정
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 패스워드 암호화
        user.setEmail(userDto.getEmail());// 이메일 설정

        // 이메일이 admin@admin.com인 경우 ADMIN 권한 부여
        if ("admin@admin.com".equals(userDto.getEmail())) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        // 데이터베이스에 사용자 저장
        userRepository.save(user);
    }

    @Transactional
    public UserDto updateUser(String currentUsername, UserDto userDto) {
        // 현재 사용자 검색
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 새로운 username으로 변경하려는 경우
        if (userDto.getUsername() != null && !userDto.getUsername().equals(currentUsername)) {
            // 새로운 username이 이미 존재하는지 확인
            if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
                throw new RuntimeException("이미 존재하는 사용자명입니다");
            }
            user.setUsername(userDto.getUsername());
        }

        // 이메일 업데이트
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        // 비밀번호 업데이트
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // 변경된 사용자 저장
        userRepository.save(user);

        // 업데이트된 사용자 정보를 DTO로 변환
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUsername(user.getUsername()); // 변경된 username
        updatedUserDto.setEmail(user.getEmail());
        updatedUserDto.setPassword(null); // 보안을 위해 비밀번호는 null로 설정

        return updatedUserDto;
    }

    public String login(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return tokenProvider.createToken(user.getUsername());
        }
        throw new RuntimeException("Invalid password");
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        userRepository.delete(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    // 어드민 관련 코드
    // 전체 사용자 목록을 UserDto 리스트로 변환 후 반환
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public void changeUserRole(Long userId, Role role) {
        // ID로 사용자 검색 (없으면 예외 발생)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // 역할(Role) 변경
        user.setRole(role);
        userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto getUserInfo(String username) {
        User user = getUserByUsername(username);
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        // 비밀번호는 보안상의 이유로 제외
        return userDto;
    }

}