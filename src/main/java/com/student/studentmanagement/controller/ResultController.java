package com.student.studentmanagement.controller;

import com.student.studentmanagement.dto.ResultDTO;
import com.student.studentmanagement.service.ResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor

public class ResultController {
    private final ResultService service;

    // Add Result
    @PostMapping
    public ResultDTO addResult(
            @Valid @RequestBody ResultDTO dto) {

        return service.addResult(dto);
    }

    // Get All Results
    @GetMapping
    public List<ResultDTO> getAllResults() {

        return service.getAllResults();
    }
}
