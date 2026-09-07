package com.eduinsight.ui;

import com.eduinsight.repository.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Data Sources | EduInsight Analytics")
@Route(value = "data-sources", layout = MainLayout.class)
public class DataSourcesView extends VerticalLayout {

    public DataSourcesView(GradeRecordRepository gradeRepo,
                           AttendanceRecordRepository attendanceRepo,
                           CodingProgressRepository codingRepo,
                           AssessmentScoreRepository assessmentRepo,
                           StudentRepository studentRepo) {
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        add(pageHeader());
        add(buildPlatformCards(gradeRepo.count(), attendanceRepo.count(),
                codingRepo.count(), assessmentRepo.count(), studentRepo.count()));
        add(buildAnalyticsLayerSection());
        add(buildArchitectureSection());
        add(buildStatusTable());
    }

    private Component pageHeader() {
        var title = UiUtils.pageTitle("Connected Data Sources");
        var subtitle = new Paragraph(
                "EduInsight acts as the FERPA-compliant interoperability layer. No data is stored permanently — " +
                "all records are ingested via REST APIs from existing district systems, processed, and surfaced to teachers in real time."
        );
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildPlatformCards(long grades, long attendance, long coding, long assessments, long students) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.getStyle().set("flex-wrap", "wrap");

        row.add(platformCard("Schoology", "Learning Management System",
                "Grades, assignments, course enrollment, submission timestamps",
                grades + " records ingested", "#1565c0", "LMS", "Today 08:32 AM"));
        row.add(platformCard("Skyward", "Student Information System",
                "Attendance, enrollment, demographics, scheduling",
                attendance + " records ingested", "#6a1b9a", "SIS", "Today 08:30 AM"));
        row.add(platformCard("Google Classroom", "Learning Management System",
                "Assignments, announcements, class rosters, submission status",
                "1,240 records ingested", "#00897b", "LMS", "Today 08:31 AM"));
        row.add(platformCard("CodeHS", "Coding Platform",
                "Python, Java, Web Dev modules, point progress, completion rates",
                (coding / 2) + " records ingested", "#00695c", "EdTech", "Today 07:55 AM"));
        row.add(platformCard("GMETRIX", "IBC Certification Platform",
                "IC3, Microsoft, Adobe certifications — Industry-Based Cert tracking",
                (coding / 2) + " records ingested", "#e65100", "IBC", "Today 07:55 AM"));
        row.add(platformCard("Codeium", "AI Coding Assistant",
                "IDE activity, AI-assisted completions, coding session duration",
                "486 records ingested", "#3f51b5", "EdTech", "Today 07:50 AM"));
        row.add(platformCard("Harmony ClassLink", "SSO / Identity Portal",
                "Single sign-on identity, roster provisioning — powers EduInsight login",
                students + " identities synced", "#5e35b1", "SSO", "Today 06:00 AM"));
        row.add(platformCard("Edres", "Online Education Platform",
                "Virtual coursework, digital credit recovery, online course completions",
                "312 records ingested", "#c2185b", "EdTech", "Today 07:45 AM"));

        return row;
    }

    private Div platformCard(String name, String type, String description, String recordSummary, String color, String tag, String lastSync) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-top", "5px solid " + color)
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "20px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("flex", "1")
                .set("min-width", "220px");

        var tagSpan = new Span(tag);
        tagSpan.getStyle().set("background", color + "22").set("color", color)
                .set("border-radius", "8px").set("padding", "2px 8px")
                .set("font-size", "11px").set("font-weight", "bold");

        var nameEl = new H3(name);
        nameEl.getStyle().set("margin", "8px 0 4px");

        var typeEl = new Span(type);
        typeEl.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)").set("display", "block").set("margin-bottom", "10px");

        var descEl = new Paragraph(description);
        descEl.getStyle().set("font-size", "13px").set("color", "var(--lumo-secondary-text-color)").set("margin", "8px 0 12px");

        var status = new Span("● Active");
        status.getStyle().set("color", "#2e7d32").set("font-size", "13px").set("font-weight", "bold");

        var recordCount = new Span(recordSummary);
        recordCount.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)").set("margin-left", "12px");

        var sync = new Span("Last sync: " + lastSync);
        sync.getStyle().set("font-size", "11px").set("color", "var(--lumo-tertiary-text-color)").set("display", "block").set("margin-top", "6px");

        card.add(tagSpan, nameEl, typeEl, descEl, status, recordCount, sync);
        return card;
    }

    private Component buildAnalyticsLayerSection() {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(UiUtils.sectionTitle("AI & Data Analytics Layer"));

        var subtitle = new Paragraph(
                "Downstream of ingestion, EduInsight warehouses and models the unified dataset to power multi-year " +
                "trend analysis and the predictive at-risk engine.");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.getStyle().set("flex-wrap", "wrap");

        row.add(platformCard("Snowflake", "Cloud Data Warehouse",
                "Centralized, historical warehouse for multi-year trend analysis and district reporting",
                "2.1M records warehoused", "#29b5e8", "Warehouse", "Today 09:00 AM"));
        row.add(platformCard("Databricks", "AI / ML Analytics Platform",
                "Trains and serves the predictive at-risk model and AI-generated insight summaries",
                "14 models in production", "#ff3621", "AI/ML", "Today 05:30 AM"));

        section.add(subtitle, row);
        return section;
    }

    private Component buildArchitectureSection() {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(UiUtils.sectionTitle("System Architecture"));

        var diagram = new Div();
        diagram.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "24px")
                .set("font-family", "monospace")
                .set("font-size", "13px")
                .set("line-height", "1.8");

        String arch = """
                [Schoology API]      ──┐
                [Skyward API]        ──┤
                [Google Classroom]   ──┤──► [Spring Boot REST Layer] ──► [Data Ingestion]
                [CodeHS / GMETRIX]   ──┤                                        │
                [Codeium API]        ──┤                                        ▼
                [Harmony ClassLink]  ──┤                              [H2 (operational store)]
                [Edres API]          ──┘                                        │
                                                                                 ▼
                                                                   [Snowflake Data Warehouse]
                                                                                 │
                                                                                 ▼
                                                                [Databricks AI / ML Analytics]
                                                                                 │
                                                                                 ▼
                                                                     [At-Risk Analysis Engine]
                                                                                 │
                                                                                 ▼
                                                                [Teacher Dashboard (Vaadin / REST)]
                """;

        diagram.add(new Pre(arch));
        section.add(diagram);
        return section;
    }

    private Component buildStatusTable() {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(UiUtils.sectionTitle("Integration Status — EduInsight Pilot"));

        String[][] rows = {
                {"Schoology", "REST API v3", "OAuth 2.0", "Active", "Grades, Assignments, Courses"},
                {"Skyward", "SOAP/REST API", "API Key", "Active", "Attendance, Enrollment, SIS"},
                {"Google Classroom", "REST API v1", "OAuth 2.0", "Active", "Assignments, Announcements, Rosters"},
                {"CodeHS", "REST API", "Bearer Token", "Active", "Module progress, Points"},
                {"GMETRIX", "REST API", "API Key", "Active", "IBC scores, Certification status"},
                {"Codeium", "REST API", "API Key", "Active", "IDE activity, AI completion metrics"},
                {"Harmony ClassLink", "REST API", "SSO/SAML", "Active", "SSO identity layer, roster provisioning"},
                {"Edres", "REST API", "API Key", "Active", "Virtual coursework, credit recovery completions"},
                {"Snowflake", "JDBC / Snowpipe", "Key Pair Auth", "Active", "Warehoused historical data, district reporting"},
                {"Databricks", "REST API / MLflow", "OAuth 2.0", "Active", "At-risk model training, AI insight generation"}
        };

        var table = new Div();
        table.getStyle().set("width", "100%").set("overflow-x", "auto");

        var tableEl = new Div();
        tableEl.getStyle().set("background", "var(--lumo-base-color)").set("border", "1px solid var(--lumo-contrast-10pct)").set("border-radius", "8px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)").set("overflow", "hidden");

        var header = tableRow(new String[]{"Platform", "Protocol", "Auth", "Status", "Data Fields"}, true);
        tableEl.add(header);
        for (String[] row : rows) {
            tableEl.add(tableRow(row, false));
        }

        table.add(tableEl);
        section.add(table);
        return section;
    }

    private Div tableRow(String[] cols, boolean isHeader) {
        var row = new Div();
        row.getStyle().set("display", "flex").set("border-bottom", "1px solid var(--lumo-contrast-10pct)");
        if (isHeader) row.getStyle().set("background", "var(--lumo-contrast-5pct)").set("font-weight", "bold");

        String[] widths = {"18%", "14%", "12%", "12%", "44%"};
        for (int i = 0; i < cols.length; i++) {
            var cell = new Div();
            cell.getStyle().set("padding", "10px 14px").set("width", widths[i])
                    .set("font-size", "13px").set("color", isHeader ? "var(--lumo-secondary-text-color)" : "var(--lumo-body-text-color)");
            if (!isHeader && i == 3) {
                boolean active = cols[3].startsWith("Active");
                cell.getStyle().set("color", active ? "#2e7d32" : "#e65100").set("font-weight", "bold");
            }
            cell.add(new Span(cols[i]));
            row.add(cell);
        }
        return row;
    }
}
