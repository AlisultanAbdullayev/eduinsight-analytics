package com.eduinsight.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Simulates data ingested from Schoology (LMS).
 */
@Entity
@Table(name = "grade_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private String course;
    private String assignmentName;
    private double score;
    private double maxScore;
    private LocalDate submittedDate;

    public double getPercentage() {
        return maxScore > 0 ? (score / maxScore) * 100.0 : 0.0;
    }
}
