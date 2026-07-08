package com.student.studentmanagement.controller;

import com.student.studentmanagement.dto.TeacherDTO;
import com.student.studentmanagement.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;

    // Add Teacher
    @PostMapping
    public TeacherDTO addTeacher(
            @Valid @RequestBody TeacherDTO dto) {

        return service.addTeacher(dto);
    }

    // Get All Teachers
    @GetMapping
    public List<TeacherDTO> getAllTeachers() {

        return service.getAllTeachers();
    }

    // Get Teacher By ID
    @GetMapping("/{id}")
    public TeacherDTO getTeacherById(@PathVariable Long id) {

        return service.getTeacherById(id);
    }

    // Update Teacher
    @PutMapping("/{id}")
    public TeacherDTO updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherDTO dto) {

        return service.updateTeacher(id, dto);
    }

    // Delete Teacher
    @DeleteMapping("/{id}")
    public String deleteTeacher(@PathVariable Long id) {

        return service.deleteTeacher(id);
    }


}
