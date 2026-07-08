package com.student.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many attendance records belong to one student
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Many attendance records belong to one course
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String status;

    private LocalDate attendanceDate;
}
