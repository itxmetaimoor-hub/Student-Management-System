package com.student.studentmanagement.controller;

import com.student.studentmanagement.entity.Student;
import org.springframework.web.bind.annotation.*;
import com.student.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import com.student.studentmanagement.dto.StudentDTO;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // Add Student
    @PostMapping
    public StudentDTO addStudent(@Valid @RequestBody StudentDTO studentDTO) {
        return service.addStudent(studentDTO);
    }

    // Get All Students
    @GetMapping
    public List<StudentDTO> getAllStudents() {
        return service.getAllStudents();
    }
    // Update Student

    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Long id,@Valid
                                 @RequestBody StudentDTO studentDTO) {

        return service.updateStudent(id, studentDTO);
    }

    // Get Student By ID
    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return service.getStudentById(id);
    }

    // Delete Student
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        return service.deleteStudent(id);
    }


}
