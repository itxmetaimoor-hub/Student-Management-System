package com.student.studentmanagement.service;
import com.student.studentmanagement.dto.TeacherDTO;
import com.student.studentmanagement.entity.Teacher;
import com.student.studentmanagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor


public class TeacherService {
    private final TeacherRepository repository;

    // Convert Entity → DTO
    private TeacherDTO convertToDTO(Teacher teacher) {

        return new TeacherDTO(
                teacher.getId(),
                teacher.getName(),
                teacher.getSubject(),
                teacher.getEmail()
        );
    }

    // Convert DTO → Entity
    private Teacher convertToEntity(TeacherDTO dto) {

        return new Teacher(
                dto.getId(),
                dto.getName(),
                dto.getSubject(),
                dto.getEmail()
        );
    }

    // Add Teacher
    public TeacherDTO addTeacher(TeacherDTO dto) {

        Teacher savedTeacher =
                repository.save(convertToEntity(dto));

        return convertToDTO(savedTeacher);
    }

    // Get All Teachers
    public List<TeacherDTO> getAllTeachers() {

        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get Teacher By ID
    public TeacherDTO getTeacherById(Long id) {

        Teacher teacher = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Teacher Not Found With ID: " + id));

        return convertToDTO(teacher);
    }

    // Update Teacher
    public TeacherDTO updateTeacher(Long id, TeacherDTO dto) {

        Teacher teacher = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Teacher Not Found With ID: " + id));

        teacher.setName(dto.getName());
        teacher.setSubject(dto.getSubject());
        teacher.setEmail(dto.getEmail());

        Teacher updatedTeacher = repository.save(teacher);

        return convertToDTO(updatedTeacher);
    }

    // Delete Teacher
    public String deleteTeacher(Long id) {

        Teacher teacher = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Teacher Not Found With ID: " + id));

        repository.delete(teacher);

        return "Teacher Deleted Successfully";
    }
}
