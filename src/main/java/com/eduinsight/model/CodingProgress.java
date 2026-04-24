package com.eduinsight.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simulates data ingested from CodeHS and GMETRIX platforms.
 */
@Entity
@Table(name = "coding_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingProgress {

    public enum Platform {
        CODE_HS, GMETRIX, CODIO, GITHUB_CLASSROOM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String moduleName;
    private double completionPercentage;
    private boolean ibcPassed;
    private int pointsEarned;
    private int totalPoints;
}
