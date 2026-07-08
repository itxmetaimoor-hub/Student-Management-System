package com.student.studentmanagement.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course title cannot be empty")
    private String title;

    @NotBlank(message = "Instructor name cannot be empty")
    private String instructor;
}

