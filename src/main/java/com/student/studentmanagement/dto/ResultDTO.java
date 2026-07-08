package com.student.studentmanagement.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ResultDTO {
    private Long id;

    private Long studentId;

    private Long courseId;

    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private int marks;

    private String grade;
}
