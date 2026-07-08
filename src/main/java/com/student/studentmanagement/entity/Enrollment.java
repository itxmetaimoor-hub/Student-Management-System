package com.student.studentmanagement.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many enrollments belong to one student
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Many enrollments belong to one course
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}

