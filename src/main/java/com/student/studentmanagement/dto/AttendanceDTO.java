package com.student.studentmanagement.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AttendanceDTO {
    private Long id;

    private Long studentId;

    private Long courseId;

    private String status;

    private LocalDate attendanceDate;
}
