package com.student.studentmanagement.service;
import com.student.studentmanagement.dto.CourseDTO;
import com.student.studentmanagement.dto.StudentDTO;
import com.student.studentmanagement.entity.Course;
import com.student.studentmanagement.entity.Student;
import com.student.studentmanagement.exception.StudentNotFoundException;
import com.student.studentmanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CourseService {
    private final CourseRepository repository;

    // Convert Entity → DTO
    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getInstructor()
        );
    }

    // Convert DTO → Entity
    private Course convertToEntity(CourseDTO dto) {
        return new Course(
                dto.getId(),
                dto.getTitle(),
                dto.getInstructor()
        );
    }

    // Add Course
    public CourseDTO addCourse(CourseDTO dto) {

        Course savedCourse = repository.save(convertToEntity(dto));

        return convertToDTO(savedCourse);
    }

    // Get All Courses
    public List<CourseDTO> getAllCourses() {

        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // Update Student
    public CourseDTO updateCourse(Long id, CourseDTO dto) {

        Course course = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course Not Found With ID: " + id));

        course.setTitle(dto.getTitle());
        course.setInstructor(dto.getInstructor());

        Course updatedCourse = repository.save(course);

        return convertToDTO(updatedCourse);
    }
    // Get Student By ID
    public CourseDTO getCourseById(Long id) {

        Course course = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course Not Found With ID: " + id));

        return convertToDTO(course);
    }

    // Delete Student
    public String deleteCourse(Long id) {
        Course course = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course Not Found With ID: " + id));
        repository.deleteById(id);
        return "Course Deleted Successfully";
    }

}
