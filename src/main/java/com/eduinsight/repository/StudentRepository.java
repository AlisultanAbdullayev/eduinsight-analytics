package com.eduinsight.repository;

import com.eduinsight.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByCampus(String campus);
    List<Student> findByGradeLevel(int gradeLevel);
}
