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

    @Query("SELECT a FROM AssessmentScore a WHERE a.student.campus IN :campuses")
    List<AssessmentScore> findByCampuses(@Param("campuses") List<String> campuses);

    @Query("SELECT DISTINCT a.examName FROM AssessmentScore a WHERE a.student.campus IN :campuses")
    List<String> findDistinctExamNamesByCampuses(@Param("campuses") List<String> campuses);

    @Query("SELECT a FROM AssessmentScore a WHERE a.examName = :examName AND a.student.campus IN :campuses")
    List<AssessmentScore> findByExamNameAndCampuses(@Param("examName") String examName, @Param("campuses") List<String> campuses);
}
