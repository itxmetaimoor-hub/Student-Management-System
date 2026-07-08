package com.student.studentmanagement.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many results belong to one student
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Many results belong to one course
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private int marks;

    private String grade;

}
