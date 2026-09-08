package com.eduinsight.service;

import com.eduinsight.auth.SchoolAccount;
import com.eduinsight.model.AssessmentScore;
import com.eduinsight.model.Student;
import com.eduinsight.repository.*;
import com.eduinsight.service.AtRiskAnalysisService.RiskLevel;
import com.eduinsight.service.AtRiskAnalysisService.StudentRiskProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
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
            Map<String, Long> atRiskByCampus,
            int campusCount
    ) {}

    public record ApPassRateStats(
            String examName,
            long totalTakers,
            long passCount,
            double passRate,
            Map<String, Double> passByCampus
    ) {}

    /**
     * Scoped to the signed-in school: a single-campus login only ever sees its
     * own campus, a district-wide login sees every campus in its district.
     * Tenants with no seeded data (e.g. Horizon Leadership Academy) correctly
     * get an empty summary instead of another school's numbers.
     */
    public DashboardSummary buildSummary(SchoolAccount account) {
        List<String> campuses = account != null ? account.campusScope() : List.of();
        List<Student> students = campuses.isEmpty() ? List.of() : studentRepo.findByCampusIn(campuses);
        var profiles = riskService.analyzeAll(students);

        long atRisk = profiles.stream().filter(p -> p.riskLevel() != RiskLevel.OK).count();
        double avgApPassRate = computeOverallApPassRate(campuses);
        long ibcPassed = campuses.isEmpty() ? 0 : codingRepo.countIbcPassedByCampuses(campuses);

        Map<RiskLevel, Long> riskDist = profiles.stream()
                .collect(Collectors.groupingBy(StudentRiskProfile::riskLevel, Collectors.counting()));

        Map<String, Long> atRiskByCampus = profiles.stream()
                .filter(p -> p.riskLevel() != RiskLevel.OK)
                .collect(Collectors.groupingBy(p -> p.student().getCampus(), Collectors.counting()));

        long gradeCount = campuses.isEmpty() ? 0 : gradeRepo.countByCampuses(campuses);
        long attendanceCount = campuses.isEmpty() ? 0 : attendanceRepo.countByCampuses(campuses);
        long codingCount = campuses.isEmpty() ? 0 : codingRepo.countByCampuses(campuses);
        long assessmentCount = campuses.isEmpty() ? 0 : assessmentRepo.findByCampuses(campuses).size();

        return new DashboardSummary(
                students.size(), atRisk, avgApPassRate, ibcPassed,
                gradeCount, attendanceCount, codingCount, assessmentCount,
                riskDist, atRiskByCampus, campuses.size()
        );
    }

    public List<ApPassRateStats> buildApPassRates(List<String> campuses) {
        if (campuses.isEmpty()) return List.of();
        return assessmentRepo.findDistinctExamNamesByCampuses(campuses).stream().map(examName -> {
            List<AssessmentScore> scores = assessmentRepo.findByExamNameAndCampuses(examName, campuses);
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

    private double computeOverallApPassRate(List<String> campuses) {
        if (campuses.isEmpty()) return 0.0;
        var scoped = assessmentRepo.findByCampuses(campuses);
        if (scoped.isEmpty()) return 0.0;
        long passing = scoped.stream().filter(AssessmentScore::isPassing).count();
        return (passing * 100.0) / scoped.size();
    }
}
