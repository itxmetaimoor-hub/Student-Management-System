package com.student.studentmanagement.controller;

import com.student.studentmanagement.dto.EnrollmentDTO;
import com.student.studentmanagement.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor

public class EnrollmentController {
    private final EnrollmentService service;

    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        return service.getAllEnrollments();
    }

    // Enroll Student
    @PostMapping
    public EnrollmentDTO enrollStudent(@RequestBody EnrollmentDTO dto) {
        return service.enrollStudent(dto);
    }
}
