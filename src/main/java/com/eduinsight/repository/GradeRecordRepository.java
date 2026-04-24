package com.eduinsight.repository;

import com.eduinsight.model.GradeRecord;
import com.eduinsight.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRecordRepository extends JpaRepository<GradeRecord, Long> {
    List<GradeRecord> findByStudent(Student student);

    @Query("SELECT AVG(g.score / g.maxScore * 100) FROM GradeRecord g WHERE g.student = :student")
    Double findAverageGradeByStudent(@Param("student") Student student);

    @Query("SELECT DISTINCT g.course FROM GradeRecord g")
    List<String> findDistinctCourses();

    @Query("SELECT g FROM GradeRecord g WHERE g.course = :course")
    List<GradeRecord> findByCourse(@Param("course") String course);
}
