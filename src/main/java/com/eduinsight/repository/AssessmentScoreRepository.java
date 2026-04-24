package com.eduinsight.repository;

import com.eduinsight.model.AssessmentScore;
import com.eduinsight.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentScoreRepository extends JpaRepository<AssessmentScore, Long> {
    List<AssessmentScore> findByStudent(Student student);

    @Query("SELECT DISTINCT a.examName FROM AssessmentScore a")
    List<String> findDistinctExamNames();

    @Query("SELECT a FROM AssessmentScore a WHERE a.examName = :examName")
    List<AssessmentScore> findByExamName(@Param("examName") String examName);

    @Query("SELECT a FROM AssessmentScore a WHERE a.student.campus = :campus")
    List<AssessmentScore> findByCampus(@Param("campus") String campus);
}
