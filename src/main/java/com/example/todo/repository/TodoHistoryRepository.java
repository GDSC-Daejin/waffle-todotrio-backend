package com.example.todo.repository;

import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoHistoryRepository extends JpaRepository<TodoHistory, Long> {
    List<TodoHistory> findByTodoId(Long todoId);
    List<TodoHistory> findByTodoIdOrderByCreatedDateDesc(Long todoId);
    void deleteByTodo(Todo todo);
}
