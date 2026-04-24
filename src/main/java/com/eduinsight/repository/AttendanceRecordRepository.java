package com.eduinsight.repository;

import com.eduinsight.model.AttendanceRecord;
import com.eduinsight.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByStudent(Student student);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student = :student AND a.status = 'PRESENT'")
    long countPresentByStudent(@Param("student") Student student);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student = :student")
    long countTotalByStudent(@Param("student") Student student);
}
