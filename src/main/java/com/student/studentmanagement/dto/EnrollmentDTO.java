package com.student.studentmanagement.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EnrollmentDTO {
    private Long id;

    private Long studentId;

    private Long courseId;
}
