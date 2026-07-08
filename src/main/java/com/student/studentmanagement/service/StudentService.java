package com.student.studentmanagement.service;

import com.student.studentmanagement.entity.Student;
import com.student.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import com.student.studentmanagement.exception.StudentNotFoundException;
import com.student.studentmanagement.dto.StudentDTO;
import java.util.stream.Collectors;


import java.util.List;

@Service
public class StudentService {
    // Convert Entity to DTO
    private StudentDTO convertToDTO(Student student) {

        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getCourse(),
                student.getAge()
        );
    }

    // Convert DTO to Entity
    private Student convertToEntity(StudentDTO dto) {

        Student student = new Student();

        student.setId(dto.getId());
        student.setName(dto.getName());
        student.setCourse(dto.getCourse());
        student.setAge(dto.getAge());

        return student;
    }
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // Add Student
    public StudentDTO addStudent(StudentDTO dto) {

        Student student = convertToEntity(dto);

        Student savedStudent = repository.save(student);

        return convertToDTO(savedStudent);
    }

    // Get All Students
    public List<StudentDTO> getAllStudents() {

        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // Update Student
    public StudentDTO updateStudent(Long id, StudentDTO dto) {

        Student student = repository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student Not Found With ID: " + id));

        student.setName(dto.getName());
        student.setCourse(dto.getCourse());
        student.setAge(dto.getAge());

        Student updatedStudent = repository.save(student);

        return convertToDTO(updatedStudent);
    }
    // Get Student By ID
    public StudentDTO getStudentById(Long id) {

        Student student = repository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student Not Found With ID: " + id));

        return convertToDTO(student);
    }


    // Delete Student
    public String deleteStudent(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student Not Found With ID: " + id));
        repository.deleteById(id);
        return "Student Deleted Successfully";
    }

}
