package com.student.studentmanagement.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CourseDTO {
    private Long id;

    @NotBlank(message = "Course title cannot be empty")
    private String title;

    @NotBlank(message = "Instructor name cannot be empty")
    private String instructor;
}
