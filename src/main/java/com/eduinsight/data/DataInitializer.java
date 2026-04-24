package com.eduinsight.data;

import com.eduinsight.model.*;
import com.eduinsight.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepo;
    private final GradeRecordRepository gradeRepo;
    private final AttendanceRecordRepository attendanceRepo;
    private final CodingProgressRepository codingRepo;
    private final AssessmentScoreRepository assessmentRepo;

    private static final String[] CAMPUSES = {
            "Harmony Discovery", "Harmony Science", "Harmony Innovation"
    };

    private static final String[][] FIRST_NAMES = {
            {"Emma", "Liam", "Sophia", "Noah", "Olivia", "Ethan", "Ava", "Mason", "Isabella", "Logan",
             "Mia", "Lucas", "Charlotte", "Aiden", "Amelia", "Jackson", "Harper", "Elijah", "Evelyn", "James"},
            {"Aaliyah", "Carlos", "Priya", "Javier", "Mei", "Tariq", "Fatima", "Diego", "Aisha", "Marco",
             "Leila", "Ravi", "Camila", "Yusuf", "Zara", "Andre", "Sana", "Miguel", "Nadia", "Omar"},
            {"Riley", "Hunter", "Skylar", "Jordan", "Taylor", "Morgan", "Casey", "Avery", "Quinn", "Blake",
             "Peyton", "Reese", "Sage", "Rowan", "Finley", "Hayden", "Cameron", "Alexis", "Drew", "Dakota"}
    };

    private static final String[] LAST_NAMES = {
            "Johnson", "Williams", "Brown", "Jones", "Garcia", "Martinez", "Davis", "Miller",
            "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris",
            "Martin", "Thompson", "Robinson", "Clark", "Lewis", "Lee", "Walker", "Hall",
            "Allen", "Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scott",
            "Green", "Adams", "Baker", "Gonzalez", "Nelson", "Carter", "Mitchell", "Perez",
            "Roberts", "Turner", "Phillips", "Campbell", "Parker", "Evans", "Edwards", "Collins",
            "Stewart", "Sanchez", "Morris", "Rogers", "Reed", "Cook", "Morgan", "Bell",
            "Murphy", "Bailey", "Rivera", "Cooper", "Richardson", "Cox", "Howard", "Ward"
    };

    private static final String[] AP_COURSES = {
            "AP Computer Science A", "AP Statistics", "AP Biology", "AP English Literature"
    };

    private static final String[] AP_EXAMS = {
            "AP CS A", "AP Statistics", "AP Biology", "AP English Lit"
    };

    private static final String[] CODEHS_MODULES = {
            "Intro to Python", "Data Structures", "Web Development Basics",
            "Java Fundamentals", "Algorithms & Problem Solving"
    };

    private static final String[] GMETRIX_MODULES = {
            "IC3 Digital Literacy", "Microsoft Office Specialist", "Adobe Certified Professional"
    };

    private final Random rand = new Random(42);

    @Override
    public void run(String... args) {
        log.info("Seeding EduInsight demo data...");
        List<Student> allStudents = seedStudents();
        seedGradeRecords(allStudents);
        seedAttendanceRecords(allStudents);
        seedCodingProgress(allStudents);
        seedAssessmentScores(allStudents);
        log.info("Seeded {} students with full platform data.", allStudents.size());
    }

    private List<Student> seedStudents() {
        List<Student> students = new ArrayList<>();
        int lastNameIdx = 0;
        for (int campusIdx = 0; campusIdx < CAMPUSES.length; campusIdx++) {
            String campus = CAMPUSES[campusIdx];
            String[] firstNames = FIRST_NAMES[campusIdx];
            for (int i = 0; i < 20; i++) {
                int gradeLevel = 9 + (i % 4);
                String firstName = firstNames[i];
                String lastName = LAST_NAMES[lastNameIdx % LAST_NAMES.length];
                lastNameIdx++;
                Student s = Student.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .gradeLevel(gradeLevel)
                        .campus(campus)
                        .studentId(String.format("HS%04d", campusIdx * 20 + i + 1001))
                        .email(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@harmonyschools.org")
                        .build();
                students.add(studentRepo.save(s));
            }
        }
        return students;
    }

    private void seedGradeRecords(List<Student> students) {
        LocalDate semesterStart = LocalDate.of(2026, 1, 15);
        for (Student student : students) {
            // Each student gets 10 grade records across 4 courses
            for (String course : AP_COURSES) {
                int numAssignments = 2 + rand.nextInt(2);
                for (int a = 0; a < numAssignments; a++) {
                    double maxScore = 100.0;
                    // Intentionally make ~25% of students struggle (low grades)
                    double score;
                    if (student.getId() % 4 == 0) {
                        score = 40 + rand.nextInt(25); // struggling student
                    } else {
                        score = 65 + rand.nextInt(35); // normal range
                    }
                    gradeRepo.save(GradeRecord.builder()
                            .student(student)
                            .course(course)
                            .assignmentName(course + " Assignment " + (a + 1))
                            .score(score)
                            .maxScore(maxScore)
                            .submittedDate(semesterStart.plusDays(rand.nextInt(90)))
                            .build());
                }
            }
        }
    }

    private void seedAttendanceRecords(List<Student> students) {
        LocalDate semesterStart = LocalDate.of(2026, 1, 15);
        for (Student student : students) {
            boolean poorAttendance = (student.getId() % 5 == 0);
            for (int day = 0; day < 75; day++) {
                LocalDate date = semesterStart.plusDays(day);
                if (date.getDayOfWeek().getValue() >= 6) continue; // skip weekends

                AttendanceRecord.Status status;
                int roll = rand.nextInt(100);
                if (poorAttendance) {
                    // ~75% present, ~15% absent, ~10% tardy
                    if (roll < 75) status = AttendanceRecord.Status.PRESENT;
                    else if (roll < 90) status = AttendanceRecord.Status.ABSENT;
                    else status = AttendanceRecord.Status.TARDY;
                } else {
                    // ~95% present, ~3% absent, ~2% tardy
                    if (roll < 95) status = AttendanceRecord.Status.PRESENT;
                    else if (roll < 98) status = AttendanceRecord.Status.ABSENT;
                    else status = AttendanceRecord.Status.TARDY;
                }
                attendanceRepo.save(AttendanceRecord.builder()
                        .student(student)
                        .date(date)
                        .status(status)
                        .build());
            }
        }
    }

    private void seedCodingProgress(List<Student> students) {
        for (Student student : students) {
            boolean lowCoder = (student.getId() % 3 == 0);

            // CodeHS module
            String codehsModule = CODEHS_MODULES[rand.nextInt(CODEHS_MODULES.length)];
            double codehsCompletion = lowCoder ? 20 + rand.nextInt(35) : 55 + rand.nextInt(45);
            int codehsPoints = (int) (codehsCompletion * 2);
            codingRepo.save(CodingProgress.builder()
                    .student(student)
                    .platform(CodingProgress.Platform.CODE_HS)
                    .moduleName(codehsModule)
                    .completionPercentage(codehsCompletion)
                    .ibcPassed(codehsCompletion >= 70 && rand.nextBoolean())
                    .pointsEarned(codehsPoints)
                    .totalPoints(200)
                    .build());

            // GMETRIX module
            String gmetrixModule = GMETRIX_MODULES[rand.nextInt(GMETRIX_MODULES.length)];
            double gmetrixCompletion = lowCoder ? 15 + rand.nextInt(40) : 60 + rand.nextInt(40);
            codingRepo.save(CodingProgress.builder()
                    .student(student)
                    .platform(CodingProgress.Platform.GMETRIX)
                    .moduleName(gmetrixModule)
                    .completionPercentage(gmetrixCompletion)
                    .ibcPassed(gmetrixCompletion >= 80)
                    .pointsEarned((int) (gmetrixCompletion * 1.5))
                    .totalPoints(150)
                    .build());
        }
    }

    private void seedAssessmentScores(List<Student> students) {
        for (int i = 0; i < AP_EXAMS.length; i++) {
            String exam = AP_EXAMS[i];
            String subject = AP_COURSES[i];
            for (Student student : students) {
                // Only students in relevant grade levels took this AP exam
                if (student.getGradeLevel() < 11) continue;
                int score;
                // Realistic distribution: ~55-65% pass rate
                int roll = rand.nextInt(100);
                if (roll < 10) score = 1;
                else if (roll < 25) score = 2;
                else if (roll < 55) score = 3;
                else if (roll < 80) score = 4;
                else score = 5;
                assessmentRepo.save(AssessmentScore.builder()
                        .student(student)
                        .examName(exam)
                        .subject(subject)
                        .score(score)
                        .examYear(2025)
                        .build());
            }
        }
    }
}
