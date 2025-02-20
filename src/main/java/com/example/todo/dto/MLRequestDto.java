package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MLRequestDto {
    private String content;
    private String title;
}