package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class TrainModelRequestDto {
    private String priority;
    private String createdDate;
    private String deadline;
    private String category;
    private String status;
}