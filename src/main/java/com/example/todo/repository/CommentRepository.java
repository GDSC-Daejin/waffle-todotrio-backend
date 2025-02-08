package com.example.todo.repository;

import com.example.todo.entity.TodoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<TodoComment, Long> {
    List<TodoComment> findByTodoIdOrderByCreatedDateDesc(Long todoId);
}
