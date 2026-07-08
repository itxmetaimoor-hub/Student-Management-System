package com.student.studentmanagement.service;

import com.student.studentmanagement.dto.ResultDTO;
import com.student.studentmanagement.entity.Course;
import com.student.studentmanagement.entity.Result;
import com.student.studentmanagement.entity.Student;
import com.student.studentmanagement.repository.CourseRepository;
import com.student.studentmanagement.repository.ResultRepository;
import com.student.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ResultService {
    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // Calculate Grade
    private String calculateGrade(int marks) {

        if (marks >= 90) {
            return "A";
        } else if (marks >= 80) {
            return "B";
        } else if (marks >= 70) {
            return "C";
        } else if (marks >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    // Add Result
    public ResultDTO addResult(ResultDTO dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student Not Found With ID: "
                                        + dto.getStudentId()));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Course Not Found With ID: "
                                        + dto.getCourseId()));

        Result result = new Result();

        result.setStudent(student);
        result.setCourse(course);
        result.setMarks(dto.getMarks());

        // Auto Calculate Grade
        result.setGrade(calculateGrade(dto.getMarks()));

        Result savedResult =
                resultRepository.save(result);

        return new ResultDTO(
                savedResult.getId(),
                student.getId(),
                course.getId(),
                savedResult.getMarks(),
                savedResult.getGrade()
        );
    }

    // Get All Results
    public List<ResultDTO> getAllResults() {

        return resultRepository.findAll()
                .stream()
                .map(result -> new ResultDTO(
                        result.getId(),
                        result.getStudent().getId(),
                        result.getCourse().getId(),
                        result.getMarks(),
                        result.getGrade()
                ))
                .collect(Collectors.toList());
    }
}
