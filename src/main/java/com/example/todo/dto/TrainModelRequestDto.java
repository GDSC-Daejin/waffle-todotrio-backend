package com.example.todo.dto;

import lombok.Data;

@Data
public class TrainModelRequestDto {
    private String priority;
    private String createdDate;
    private String deadline;
    private String category;
    private String status;
}