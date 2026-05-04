# EduInsight Analytics — MVP Demo

FERPA-compliant student data middleware that unifies Schoology, Skyward, CodeHS, and GMETRIX into a single teacher dashboard. Built for the Harmony Public Schools 2026–27 pilot.

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 25, Spring Boot 4.0.6 |
| Frontend | Vaadin 25.1.1 |
| Database | H2 in-memory (demo) → Snowflake (production) |
| Build | Gradle 9.4.1 |

## Running Locally

```bash
./gradlew bootRun
```

Then open: **<http://localhost:8080>**

H2 Console (inspect seeded data): **<http://localhost:8080/h2-console>**

- JDBC URL: `jdbc:h2:mem:eduinsight`
- Username: `sa` / Password: *(empty)*

## Dashboard Views

| View | Route | Description |
|------|-------|-------------|
| Dashboard | `/` | Overview: student counts, risk distribution, AP rates, ingestion stats |
| At-Risk Students | `/at-risk` | Filterable grid with early-warning flags from all 3 platforms |
| AP Pass Rates | `/ap-rates` | Exam pass rates per course and campus |
| Data Sources | `/data-sources` | Platform integration cards, architecture diagram, status table |
| Admin Burden | `/admin-burden` | Time-saved metrics: 42 min → 3 min per planning period |

## Screenshots

### District Dashboard

![District Dashboard](docs/screenshots/dashboard.png)

### At-Risk Student Identification

![At-Risk Students](docs/screenshots/at-risk-students.png)

### AP Exam Pass Rate Tracker

![AP Pass Rates](docs/screenshots/ap-pass-rates.png)

### Connected Data Sources

![Data Sources Overview](docs/screenshots/data-sources-overview.png)

### Data Integration Status Table

![Data Sources Integration Table](docs/screenshots/data-sources-integration-table.png)

### Administrative Burden Reduction

![Administrative Burden Hero](docs/screenshots/admin-burden-hero.png)

### Savings Projection and Outcomes

![Administrative Burden Savings](docs/screenshots/admin-burden-savings.png)

## Demo Data

- **60 students** across 3 Harmony campuses (Discovery, Science, Innovation)
- **Grade records** from Schoology (4 AP courses, 10 records/student)
- **Attendance records** from Skyward (50+ records/student, realistic present/absent/tardy)
- **Coding progress** from CodeHS and GMETRIX (IBC certification tracking)
- **AP assessment scores** for 11th/12th graders (realistic 1–5 distribution)

## At-Risk Scoring

A student is flagged based on data from three platforms:

| Dimension | Platform | Threshold | Weight |
|-----------|----------|-----------|--------|
| Grade Average | Schoology | < 70% | 1 flag |
| Attendance Rate | Skyward | < 90% | 1 flag |
| Coding Completion | CodeHS/GMETRIX | < 60% | 1 flag |

**Risk Level:** HIGH (3 flags) · MEDIUM (2) · LOW (1) · OK (0)

## Business Context

EduInsight addresses a market gap: enterprise data warehouse alternatives cost $250,000+ per deployment, while this platform targets Title I districts. The Harmony pilot (documented by Principal Ali Sarioglu and Coach Yilmaz Kahraman) will validate the 42-minute-per-planning-period reduction confirmed by district audit.

---

*MVP Demo — dummy data only. Not for production use. FERPA compliance documentation in progress.*
