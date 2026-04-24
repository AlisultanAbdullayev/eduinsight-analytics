package com.eduinsight.service;

import com.eduinsight.model.Student;
import com.eduinsight.repository.AttendanceRecordRepository;
import com.eduinsight.repository.CodingProgressRepository;
import com.eduinsight.repository.GradeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AtRiskAnalysisService {

    private static final double GRADE_THRESHOLD = 70.0;
    private static final double ATTENDANCE_THRESHOLD = 90.0;
    private static final double CODING_THRESHOLD = 60.0;

    private final GradeRecordRepository gradeRepo;
    private final AttendanceRecordRepository attendanceRepo;
    private final CodingProgressRepository codingRepo;

    public enum RiskLevel {
        OK, LOW, MEDIUM, HIGH
    }

    public record StudentRiskProfile(
            Student student,
            double gradeAverage,
            double attendanceRate,
            double codingCompletion,
            boolean gradeFlagged,
            boolean attendanceFlagged,
            boolean codingFlagged,
            RiskLevel riskLevel
    ) {}

    public StudentRiskProfile analyze(Student student) {
        double gradeAvg = safeAvg(gradeRepo.findAverageGradeByStudent(student));
        double attendanceRate = computeAttendanceRate(student);
        double codingAvg = safeAvg(codingRepo.findAvgCompletionByStudent(student));

        boolean gradeFlagged = gradeAvg < GRADE_THRESHOLD;
        boolean attendanceFlagged = attendanceRate < ATTENDANCE_THRESHOLD;
        boolean codingFlagged = codingAvg < CODING_THRESHOLD;

        int flagCount = (gradeFlagged ? 1 : 0) + (attendanceFlagged ? 1 : 0) + (codingFlagged ? 1 : 0);
        RiskLevel risk = switch (flagCount) {
            case 3 -> RiskLevel.HIGH;
            case 2 -> RiskLevel.MEDIUM;
            case 1 -> RiskLevel.LOW;
            default -> RiskLevel.OK;
        };

        return new StudentRiskProfile(student, gradeAvg, attendanceRate, codingAvg,
                gradeFlagged, attendanceFlagged, codingFlagged, risk);
    }

    public List<StudentRiskProfile> analyzeAll(List<Student> students) {
        return students.stream().map(this::analyze).toList();
    }

    private double computeAttendanceRate(Student student) {
        long total = attendanceRepo.countTotalByStudent(student);
        if (total == 0) return 100.0;
        long present = attendanceRepo.countPresentByStudent(student);
        return (present * 100.0) / total;
    }

    private double safeAvg(Double val) {
        return val != null ? val : 100.0;
    }
}
