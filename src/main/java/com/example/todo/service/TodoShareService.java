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

}