package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredictSuccessRequestDto {
    private String priority;
    private String createdDate;
    private String deadline;
    private String category;
    private String status;
}