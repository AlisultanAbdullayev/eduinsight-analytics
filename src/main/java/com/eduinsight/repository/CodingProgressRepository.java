package com.eduinsight.repository;

import com.eduinsight.model.CodingProgress;
import com.eduinsight.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingProgressRepository extends JpaRepository<CodingProgress, Long> {
    List<CodingProgress> findByStudent(Student student);

    @Query("SELECT AVG(c.completionPercentage) FROM CodingProgress c WHERE c.student = :student")
    Double findAvgCompletionByStudent(@Param("student") Student student);

    @Query("SELECT COUNT(c) FROM CodingProgress c WHERE c.ibcPassed = true")
    long countIbcPassed();
}
