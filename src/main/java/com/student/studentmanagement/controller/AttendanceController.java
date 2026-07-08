package com.student.studentmanagement.controller;
import com.student.studentmanagement.dto.AttendanceDTO;
import com.student.studentmanagement.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor

public class AttendanceController {

    private final AttendanceService service;
    @GetMapping
    public List<AttendanceDTO> getAllAttendance() {
        return service.getAllAttendance();
    }

    // Mark Attendance
    @PostMapping
    public AttendanceDTO markAttendance(@RequestBody AttendanceDTO dto) {

        return service.markAttendance(dto);


    }
}