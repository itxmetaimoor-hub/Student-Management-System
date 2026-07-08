package com.student.studentmanagement.repository;

import com.student.studentmanagement.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ResultRepository extends JpaRepository<Result, Long> {
}
