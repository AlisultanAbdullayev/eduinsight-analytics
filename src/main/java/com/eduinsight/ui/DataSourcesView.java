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
        add(buildArchitectureSection());
        add(buildStatusTable());
    }

    private Component pageHeader() {
        var title = new H2("Connected Data Sources");
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

        row.add(platformCard("Schoology", "Learning Management System",
                "Grades, assignments, course enrollment, submission timestamps",
                grades, "#1565c0", "LMS"));
        row.add(platformCard("Skyward", "Student Information System",
                "Attendance, enrollment, demographics, scheduling",
                attendance, "#6a1b9a", "SIS"));
        row.add(platformCard("CodeHS", "Coding Platform",
                "Python, Java, Web Dev modules, point progress, completion rates",
                coding / 2, "#00695c", "EdTech"));
        row.add(platformCard("GMETRIX", "IBC Certification Platform",
                "IC3, Microsoft, Adobe certifications — Industry-Based Cert tracking",
                coding / 2, "#e65100", "IBC"));

        return row;
    }

    private Div platformCard(String name, String type, String description, long records, String color, String tag) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-top", "5px solid " + color)
                .set("border-radius", "10px")
                .set("padding", "20px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.08)")
                .set("flex", "1")
                .set("min-width", "200px");

        var tagSpan = new Span(tag);
        tagSpan.getStyle().set("background", color + "22").set("color", color)
                .set("border-radius", "8px").set("padding", "2px 8px")
                .set("font-size", "11px").set("font-weight", "bold");

        var nameEl = new H3(name);
        nameEl.getStyle().set("margin", "8px 0 4px");

        var typeEl = new Span(type);
        typeEl.getStyle().set("font-size", "12px").set("color", "#888").set("display", "block").set("margin-bottom", "10px");

        var descEl = new Paragraph(description);
        descEl.getStyle().set("font-size", "13px").set("color", "#555").set("margin", "8px 0 12px");

        var status = new Span("● Active");
        status.getStyle().set("color", "#2e7d32").set("font-size", "13px").set("font-weight", "bold");

        var recordCount = new Span(records + " records ingested");
        recordCount.getStyle().set("font-size", "12px").set("color", "#888").set("margin-left", "12px");

        var sync = new Span("Last sync: Today 08:32 AM");
        sync.getStyle().set("font-size", "11px").set("color", "#aaa").set("display", "block").set("margin-top", "6px");

        card.add(tagSpan, nameEl, typeEl, descEl, status, recordCount, sync);
        return card;
    }

    private Component buildArchitectureSection() {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(new H3("System Architecture"));

        var diagram = new Div();
        diagram.getStyle()
                .set("background", "#f8f9fa")
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "10px")
                .set("padding", "24px")
                .set("font-family", "monospace")
                .set("font-size", "13px")
                .set("line-height", "1.8");

        String arch = """
                [Schoology API] ──┐
                [Skyward API]  ───┤──► [Spring Boot REST Layer] ──► [Data Ingestion]
                [CodeHS API]   ───┤                                        │
                [GMETRIX API]  ──-┘                                        ▼
                                                                   [H2 / Snowflake DB]
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
        section.add(new H3("Integration Status — Harmony Pilot"));

        String[][] rows = {
                {"Schoology", "REST API v3", "OAuth 2.0", "Active", "Grades, Assignments, Courses"},
                {"Skyward", "SOAP/REST API", "API Key", "Active", "Attendance, Enrollment, SIS"},
                {"CodeHS", "REST API", "Bearer Token", "Active", "Module progress, Points"},
                {"GMETRIX", "REST API", "API Key", "Active", "IBC scores, Certification status"},
                {"ClassLink", "REST API", "SSO/SAML", "Planned Q2", "SSO identity layer"},
                {"Codio", "REST API", "API Key", "Planned Q2", "Coding assignments, IDE activity"}
        };

        var table = new Div();
        table.getStyle().set("width", "100%").set("overflow-x", "auto");

        var tableEl = new Div();
        tableEl.getStyle().set("background", "white").set("border-radius", "8px")
                .set("box-shadow", "0 1px 6px rgba(0,0,0,0.07)").set("overflow", "hidden");

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
        row.getStyle().set("display", "flex").set("border-bottom", "1px solid #f0f0f0");
        if (isHeader) row.getStyle().set("background", "#f8f9fa").set("font-weight", "bold");

        String[] widths = {"15%", "15%", "12%", "15%", "43%"};
        for (int i = 0; i < cols.length; i++) {
            var cell = new Div();
            cell.getStyle().set("padding", "10px 14px").set("width", widths[i])
                    .set("font-size", "13px").set("color", isHeader ? "#555" : "#333");
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
