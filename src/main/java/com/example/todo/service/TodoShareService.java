package com.example.todo.service;

import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoShare;
import com.example.todo.entity.User;
import com.example.todo.enums.PermissionType;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.TodoShareRepository;
import com.example.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//Todo 공유 관련 서비스
@Service
@RequiredArgsConstructor
public class TodoShareService {
    private final TodoShareRepository shareRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    /**
     * Todo 공유하기
     * @param todoId 공유할 Todo ID
     * @param username 공유받을 사용자 이름
     * @param permission 권한 유형
     */
    @Transactional
    public void shareTodo(Long todoId, String username, PermissionType permission) {
        if (permission == null) {
            permission = PermissionType.READ; // 기본값을 READ로 설정
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TodoShare share = new TodoShare();
        share.setTodo(todo);
        share.setUser(user);
        share.setPermissionType(permission);
        shareRepository.save(share);
    }

    /**
     * 공유받은 Todo 목록 조회
     */

    @Transactional
    public List<Todo> getSharedTodos(User user) {
        return shareRepository.findByUser(user).stream()
                .map(TodoShare::getTodo)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelShare(Long todoId, String username) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TodoShare share = shareRepository.findByTodoAndUser(todo, user)
                .orElseThrow(() -> new RuntimeException("Share not found"));

        shareRepository.delete(share);
    }

    @Transactional
    public void updatePermission(Long todoId, String username, PermissionType newPermission) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TodoShare share = shareRepository.findByTodoAndUser(todo, user)
                .orElseThrow(() -> new RuntimeException("Share not found"));

        share.setPermissionType(newPermission);
    }


    /**
     * 공유해준 사람의 유저네임 조회
     * @param todoId 공유된 Todo ID
     * @param username 공유받은 사용자 이름
     * @return 공유해준 사람의 유저네임
     */
    @Transactional
    public String getSharerUsername(Long todoId, String username) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TodoShare share = shareRepository.findByTodoAndUser(todo, user)
                .orElseThrow(() -> new RuntimeException("Share not found"));

        return todo.getOwner().getUsername(); // owner를 사용하도록 수정
    }



}