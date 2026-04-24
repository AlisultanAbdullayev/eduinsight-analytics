package com.eduinsight.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simulates data ingested from the district AP assessment portal.
 */
@Entity
@Table(name = "assessment_scores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private String examName;
    private int score;
    private int examYear;
    private String subject;

    public boolean isPassing() {
        return score >= 3;
    }
}
