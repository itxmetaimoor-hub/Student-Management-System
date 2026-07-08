package com.student.studentmanagement.service;
import com.student.studentmanagement.dto.AttendanceDTO;
import com.student.studentmanagement.entity.Attendance;
import com.student.studentmanagement.entity.Course;
import com.student.studentmanagement.entity.Student;
import com.student.studentmanagement.exception.StudentNotFoundException;
import com.student.studentmanagement.repository.AttendanceRepository;
import com.student.studentmanagement.repository.CourseRepository;
import com.student.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // Add this method inside the class
    public List<AttendanceDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(a -> new AttendanceDTO(
                        a.getId(),
                        a.getStudent().getId(),
                        a.getCourse().getId(),
                        a.getStatus(),
                        a.getAttendanceDate()
                ))
                .collect(Collectors.toList());
    }

    // Mark Attendance
    public AttendanceDTO markAttendance(AttendanceDTO dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student Not Found With ID: " + dto.getStudentId()));


        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Course Not Found With ID: " + dto.getCourseId()));

        Attendance attendance = new Attendance();

        attendance.setStudent(student);
        attendance.setCourse(course);
        attendance.setStatus(dto.getStatus());
        attendance.setAttendanceDate(dto.getAttendanceDate());

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        return new AttendanceDTO(
                savedAttendance.getId(),
                student.getId(),
                course.getId(),
                savedAttendance.getStatus(),
                savedAttendance.getAttendanceDate()


        );
    }
}
