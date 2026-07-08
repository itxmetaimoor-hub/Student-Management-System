package com.student.studentmanagement.controller;
import com.student.studentmanagement.dto.CourseDTO;
import com.student.studentmanagement.dto.StudentDTO;
import com.student.studentmanagement.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor

public class CourseController {
    private final CourseService service;

    // Add Course
    @PostMapping
    public CourseDTO addCourse(@Valid @RequestBody CourseDTO dto) {
        return service.addCourse(dto);
    }

    // Get All Courses
    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return service.getAllCourses();
    }

    // Update Student

    @PutMapping("/{id}")
    public CourseDTO updateCourse(@PathVariable Long id, @Valid
    @RequestBody CourseDTO courseDTO) {

        return service.updateCourse(id, courseDTO);
    }

    // Get Student By ID
    @GetMapping("/{id}")
    public CourseDTO getcourseById(@PathVariable Long id) {
        return service.getCourseById(id);
    }

    // Delete Student
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        return service.deleteCourse(id);
    }
}
