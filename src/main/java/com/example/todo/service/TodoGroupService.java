package com.example.todo.service;

import com.example.todo.dto.TodoGroupRequestDto;
import com.example.todo.entity.TodoGroup;
import com.example.todo.repository.TodoGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.todo.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoGroupService {
    private final TodoGroupRepository groupRepository;

    @Transactional
    public TodoGroup createGroup(TodoGroupRequestDto requestDto, User user) {
        TodoGroup group = new TodoGroup();
        group.setName(requestDto.getName());
        group.setDescription(requestDto.getDescription());
        group.setOwner(user);
        return groupRepository.save(group);
    }

    @Transactional
    public TodoGroup updateGroup(Long groupId, TodoGroupRequestDto requestDto, User user) {
        TodoGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getOwner().equals(user)) {
            throw new RuntimeException("권한이 없습니다");
        }

        group.setName(requestDto.getName());
        group.setDescription(requestDto.getDescription());
        return group;
    }

    @Transactional
    public void deleteGroup(Long groupId, User user) {
        TodoGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getOwner().equals(user)) {
            throw new RuntimeException("권한이 없습니다");
        }

        groupRepository.delete(group);
    }

    public List<TodoGroup> getGroupsByUser(User user) {
        return groupRepository.findByOwner(user);
    }
}