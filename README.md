# EduInsight Analytics — MVP Demo

FERPA-compliant student data middleware that unifies Schoology, Skyward, Google Classroom, CodeHS, GMETRIX, Codeium, Edres, Harmony ClassLink, Snowflake, and Databricks into a single teacher dashboard. Built for the Harmony Public Schools 2026–27 pilot, and demoed as a multi-district/multi-school platform (Harmony Public Schools' three campuses plus the independent Horizon Leadership Academy).

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 25, Spring Boot 4.0.6 |
| Frontend | Vaadin 25.1.1 (custom Lumo theme, adaptive light/dark mode) |
| Database | H2 in-memory (demo) → Snowflake (production) |
| Build | Gradle 9.4.1 |

## Running Locally

```bash
./gradlew bootRun
```

Then open: **<http://localhost:8080>** — you'll land on the sign-in page first (see [Signing In](#signing-in) below).

H2 Console (inspect seeded data): **<http://localhost:8080/h2-console>**

- JDBC URL: `jdbc:h2:mem:eduinsight`
- Username: `sa` / Password: *(empty)*

## Signing In

Access is gated per school with a lightweight, session-based demo login (no real identity provider — this is a demo, not production auth). Signing in reveals that school's name throughout the app (sidebar, header, dashboard copy) instead of a generic label.

| School | Username | Password |
|--------|----------|----------|
| Harmony Discovery | `harmony.discovery` | `demo123` |
| Harmony Science | `harmony.science` | `demo123` |
| Harmony Innovation | `harmony.innovation` | `demo123` |
| Horizon Leadership Academy (private school) | `horizon.leadership` | `demo123` |
| Harmony Public Schools — District Office (district-wide view) | `district.admin` | `demo123` |

![Sign In](docs/screenshots/login.png)

## Dashboard Views

| View | Route | Description |
|------|-------|-------------|
| Dashboard | `/dashboard` (alias `/`) | Overview: student counts, risk distribution, AP rates, ingestion stats, KPI trend deltas |
| At-Risk Students | `/at-risk` | Filterable grid with early-warning flags from all connected platforms |
| AP Pass Rates | `/ap-rates` | Exam pass rates per course and campus |
| Data Sources | `/data-sources` | Platform integration cards, AI/data-analytics layer, architecture diagram, status table |
| School Info | `/school-info` | Directory of all pilot schools with contact details and enrollment |
| Admin Burden | `/admin-burden` | Time-saved metrics: 42 min → 3 min per planning period |

The app automatically follows your OS light/dark mode preference (a manual toggle in the header lets you override it for a demo).

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

### School Information Directory

![School Info](docs/screenshots/school-info.png)

### Administrative Burden Reduction

![Administrative Burden Reduction](docs/screenshots/admin-burden.png)

## Demo Data

- **60 students** across 3 Harmony campuses (Discovery, Science, Innovation). Horizon Leadership Academy has no seeded student rows — it demonstrates the login/branding flow with a fixed demo enrollment figure instead.
- **Grade records** from Schoology (4 AP courses, 10 records/student)
- **Attendance records** from Skyward (50+ records/student, realistic present/absent/tardy)
- **Coding progress** from CodeHS and GMETRIX (IBC certification tracking)
- **AP assessment scores** for 11th/12th graders (realistic 1–5 distribution)
- All other integrations shown on the Data Sources page (Google Classroom, Codeium, Harmony ClassLink, Edres, Snowflake, Databricks) are illustrative/mock — there are no real API credentials or live connections behind them.

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
