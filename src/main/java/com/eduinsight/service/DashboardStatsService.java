package com.eduinsight.service;

import com.eduinsight.model.AssessmentScore;
import com.eduinsight.repository.*;
import com.eduinsight.service.AtRiskAnalysisService.RiskLevel;
import com.eduinsight.service.AtRiskAnalysisService.StudentRiskProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardStatsService {

    private final StudentRepository studentRepo;
    private final GradeRecordRepository gradeRepo;
    private final AttendanceRecordRepository attendanceRepo;
    private final CodingProgressRepository codingRepo;
    private final AssessmentScoreRepository assessmentRepo;
    private final AtRiskAnalysisService riskService;

    public record DashboardSummary(
            long totalStudents,
            long atRiskCount,
            double avgApPassRate,
            long ibcPassedCount,
            long totalGradeRecords,
            long totalAttendanceRecords,
            long totalCodingRecords,
            long totalAssessmentScores,
            Map<RiskLevel, Long> riskDistribution,
            Map<String, Long> atRiskByCampus
    ) {}

    public record ApPassRateStats(
            String examName,
            long totalTakers,
            long passCount,
            double passRate,
            Map<String, Double> passByCampus
    ) {}

    public DashboardSummary buildSummary() {
        var students = studentRepo.findAll();
        var profiles = riskService.analyzeAll(students);

        long atRisk = profiles.stream().filter(p -> p.riskLevel() != RiskLevel.OK).count();
        double avgApPassRate = computeOverallApPassRate();
        long ibcPassed = codingRepo.countIbcPassed();

        Map<RiskLevel, Long> riskDist = profiles.stream()
                .collect(Collectors.groupingBy(StudentRiskProfile::riskLevel, Collectors.counting()));

        Map<String, Long> atRiskByCampus = profiles.stream()
                .filter(p -> p.riskLevel() != RiskLevel.OK)
                .collect(Collectors.groupingBy(p -> p.student().getCampus(), Collectors.counting()));

        return new DashboardSummary(
                students.size(), atRisk, avgApPassRate, ibcPassed,
                gradeRepo.count(), attendanceRepo.count(), codingRepo.count(), assessmentRepo.count(),
                riskDist, atRiskByCampus
        );
    }

    public List<ApPassRateStats> buildApPassRates() {
        return assessmentRepo.findDistinctExamNames().stream().map(examName -> {
            List<AssessmentScore> scores = assessmentRepo.findByExamName(examName);
            long total = scores.size();
            long passing = scores.stream().filter(AssessmentScore::isPassing).count();
            double rate = total > 0 ? (passing * 100.0) / total : 0.0;

            Map<String, Double> byCampus = new LinkedHashMap<>();
            scores.stream()
                    .collect(Collectors.groupingBy(s -> s.getStudent().getCampus()))
                    .forEach((campus, campusScores) -> {
                        long campusPassing = campusScores.stream().filter(AssessmentScore::isPassing).count();
                        byCampus.put(campus, campusScores.isEmpty() ? 0.0 : (campusPassing * 100.0) / campusScores.size());
                    });

            return new ApPassRateStats(examName, total, passing, rate, byCampus);
        }).toList();
    }

    private double computeOverallApPassRate() {
        var all = assessmentRepo.findAll();
        if (all.isEmpty()) return 0.0;
        long passing = all.stream().filter(AssessmentScore::isPassing).count();
        return (passing * 100.0) / all.size();
    }
}
