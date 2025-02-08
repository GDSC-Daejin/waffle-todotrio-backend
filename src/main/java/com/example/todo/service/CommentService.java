package com.example.todo.service;

import com.example.todo.dto.CommentDto;
import com.example.todo.dto.CommentRequestDto;
import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoComment;
import com.example.todo.entity.User;
import com.example.todo.repository.CommentRepository;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto createComment(Long todoId, CommentRequestDto request, String username) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TodoComment comment = new TodoComment();
        comment.setContent(request.getContent());
        comment.setTodo(todo);
        comment.setUser(user);
        comment.setCreatedDate(LocalDateTime.now());

        return CommentDto.from(commentRepository.save(comment));
    }

    public List<CommentDto> getCommentsByTodoId(Long todoId) {
        return commentRepository.findByTodoIdOrderByCreatedDateDesc(todoId)
                .stream()
                .map(CommentDto::from)
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        TodoComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다");
        }

        commentRepository.delete(comment);
    }
}
