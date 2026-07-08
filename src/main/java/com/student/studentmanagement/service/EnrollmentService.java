package com.student.studentmanagement.service;
import com.student.studentmanagement.dto.EnrollmentDTO;
import com.student.studentmanagement.entity.Course;
import com.student.studentmanagement.entity.Enrollment;
import com.student.studentmanagement.entity.Student;
import com.student.studentmanagement.exception.StudentNotFoundException;
import com.student.studentmanagement.repository.CourseRepository;
import com.student.studentmanagement.repository.EnrollmentRepository;
import com.student.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class EnrollmentService {

    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(e -> new EnrollmentDTO(
                        e.getId(),
                        e.getStudent().getId(),
                        e.getCourse().getId()
                ))
                .collect(Collectors.toList());
    }
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // Enroll Student in Course
    public EnrollmentDTO enrollStudent(EnrollmentDTO dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student Not Found With ID: " + dto.getStudentId()));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Course Not Found With ID: " + dto.getCourseId()));

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return new EnrollmentDTO(
                savedEnrollment.getId(),
                student.getId(),
                course.getId()
        );
    }
}
