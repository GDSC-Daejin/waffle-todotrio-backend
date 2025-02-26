package com.example.todo.repository;

import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoShare;
import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoShareRepository extends JpaRepository<TodoShare, Long> {
    List<TodoShare> findByUser(User user);
    Optional<TodoShare> findByTodoAndUser(Todo todo, User user);

    // Todo가 삭제될 때 관련된 TodoShare 항목을 삭제하기 위한 메서드
    void deleteByTodo(Todo todo);
}