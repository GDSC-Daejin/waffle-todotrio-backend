package com.example.todo.repository;

import com.example.todo.entity.TodoGroup;
import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoGroupRepository extends JpaRepository<TodoGroup, Long> {
    List<TodoGroup> findByOwner(User user);
}
