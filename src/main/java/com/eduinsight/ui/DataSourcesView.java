package com.eduinsight.ui;

import com.eduinsight.integration.DatabricksAnalyticsService;
import com.eduinsight.integration.IntegrationStatus;
import com.eduinsight.integration.SnowflakeWarehouseService;
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

    /**
     * LIVE: real DB-backed data, wired up and running in this demo.
     * NOT_CONNECTED: real integration code exists (JDBC client, config keys) but
     * isn't pointed at a live account/workspace in this demo.
     * ROADMAP: purely illustrative — no client, dependency, or credential exists for it at all.
     */
    private enum IntegrationState { LIVE, NOT_CONNECTED, ROADMAP }

    private final IntegrationStatus snowflakeStatus;
    private final IntegrationStatus databricksStatus;

    public DataSourcesView(GradeRecordRepository gradeRepo,
                           AttendanceRecordRepository attendanceRepo,
                           CodingProgressRepository codingRepo,
                           SnowflakeWarehouseService snowflakeService,
                           DatabricksAnalyticsService databricksService) {
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        this.snowflakeStatus = snowflakeService.checkStatus();
        this.databricksStatus = databricksService.checkStatus();

        add(pageHeader());
        add(buildPlatformCards(gradeRepo.count(), attendanceRepo.count(), codingRepo.count()));
        add(buildAnalyticsLayerSection());
        add(buildArchitectureSection());
        add(buildStatusTable());
    }

    private Component pageHeader() {
        var title = UiUtils.pageTitle("Connected Data Sources");
        var subtitle = new Paragraph(
                "EduInsight acts as the FERPA-compliant interoperability layer. Schoology, Skyward, CodeHS, and " +
                "GMETRIX are live in this demo, ingested via REST APIs from seeded district data. Every other card " +
                "below is either a real-but-disabled integration or an illustrative roadmap item — see each card's status."
        );
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildPlatformCards(long grades, long attendance, long coding) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.getStyle().set("flex-wrap", "wrap");

        row.add(platformCard("Schoology", "Learning Management System",
                "Grades, assignments, course enrollment, submission timestamps",
                grades + " records ingested", "#1565c0", "LMS", "Today 08:32 AM", IntegrationState.LIVE));
        row.add(platformCard("Skyward", "Student Information System",
                "Attendance, enrollment, demographics, scheduling",
                attendance + " records ingested", "#6a1b9a", "SIS", "Today 08:30 AM", IntegrationState.LIVE));
        row.add(platformCard("CodeHS", "Coding Platform",
                "Python, Java, Web Dev modules, point progress, completion rates",
                (coding / 2) + " records ingested", "#00695c", "EdTech", "Today 07:55 AM", IntegrationState.LIVE));
        row.add(platformCard("GMETRIX", "IBC Certification Platform",
                "IC3, Microsoft, Adobe certifications — Industry-Based Cert tracking",
                (coding / 2) + " records ingested", "#e65100", "IBC", "Today 07:55 AM", IntegrationState.LIVE));
        row.add(platformCard("Google Classroom", "Learning Management System",
                "Assignments, announcements, class rosters, submission status",
                "No live connection — illustrative only", "#00897b", "LMS", null, IntegrationState.ROADMAP));
        row.add(platformCard("Codeium", "AI Coding Assistant",
                "IDE activity, AI-assisted completions, coding session duration",
                "No live connection — illustrative only", "#3f51b5", "EdTech", null, IntegrationState.ROADMAP));
        row.add(platformCard("Harmony ClassLink", "SSO / Identity Portal",
                "Single sign-on identity, roster provisioning — powers EduInsight login",
                "No live connection — illustrative only", "#5e35b1", "SSO", null, IntegrationState.ROADMAP));
        row.add(platformCard("Edres", "Online Education Platform",
                "Virtual coursework, digital credit recovery, online course completions",
                "No live connection — illustrative only", "#c2185b", "EdTech", null, IntegrationState.ROADMAP));

        return row;
    }

    private Div platformCard(String name, String type, String description, String metric, String color, String tag,
                              String lastSync, IntegrationState state) {
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

        var status = statusBadge(state);

        var metricEl = new Span(metric);
        metricEl.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)");
        if (state == IntegrationState.LIVE) {
            metricEl.getStyle().set("margin-left", "12px");
        } else {
            metricEl.getStyle().set("display", "block").set("margin-top", "4px");
        }

        card.add(tagSpan, nameEl, typeEl, descEl, status, metricEl);

        if (lastSync != null) {
            var sync = new Span("Last sync: " + lastSync);
            sync.getStyle().set("font-size", "11px").set("color", "var(--lumo-tertiary-text-color)").set("display", "block").set("margin-top", "6px");
            card.add(sync);
        }
        return card;
    }

    private Span statusBadge(IntegrationState state) {
        String label = switch (state) {
            case LIVE -> "● Active";
            case NOT_CONNECTED -> "○ Not Connected";
            case ROADMAP -> "○ Roadmap";
        };
        String color = switch (state) {
            case LIVE -> "#2e7d32";
            case NOT_CONNECTED -> "#e65100";
            case ROADMAP -> "var(--lumo-tertiary-text-color)";
        };
        var status = new Span(label);
        status.getStyle().set("color", color).set("font-size", "13px").set("font-weight", "bold");
        return status;
    }

    private Component buildAnalyticsLayerSection() {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(UiUtils.sectionTitle("AI & Data Analytics Layer"));

        var subtitle = new Paragraph(
                "Downstream of ingestion, EduInsight can warehouse and model the unified dataset via real (optional) " +
                "JDBC clients to Snowflake and Databricks — disabled in this demo since no live warehouse or workspace " +
                "is provisioned. Configure eduinsight.integrations.snowflake.* / .databricks.* to turn them on.");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.getStyle().set("flex-wrap", "wrap");

        row.add(platformCard("Snowflake", "Cloud Data Warehouse",
                "Centralized, historical warehouse for multi-year trend analysis and district reporting",
                snowflakeStatus.detail(), "#29b5e8", "Warehouse", null,
                snowflakeStatus.connected() ? IntegrationState.LIVE : IntegrationState.NOT_CONNECTED));
        row.add(platformCard("Databricks", "AI / ML Analytics Platform",
                "Trains and serves the predictive at-risk model and AI-generated insight summaries",
                databricksStatus.detail(), "#ff3621", "AI/ML", null,
                databricksStatus.connected() ? IntegrationState.LIVE : IntegrationState.NOT_CONNECTED));

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
                [CodeHS / GMETRIX]   ──┤──► [Spring Boot REST Layer] ──► [Data Ingestion]
                                                                                 │
                                                                                 ▼
                                                                   [H2 (operational store)]
                                                                                 │
                                                                                 ▼
                                                       [Snowflake Data Warehouse]  (optional, disabled)
                                                                                 │
                                                                                 ▼
                                                    [Databricks AI / ML Analytics]  (optional, disabled)
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
                {"CodeHS", "REST API", "Bearer Token", "Active", "Module progress, Points"},
                {"GMETRIX", "REST API", "API Key", "Active", "IBC scores, Certification status"},
                {"Google Classroom", "REST API v1", "OAuth 2.0", "Roadmap", "Assignments, Announcements, Rosters"},
                {"Codeium", "REST API", "API Key", "Roadmap", "IDE activity, AI completion metrics"},
                {"Harmony ClassLink", "REST API", "SSO/SAML", "Roadmap", "SSO identity layer, roster provisioning"},
                {"Edres", "REST API", "API Key", "Roadmap", "Virtual coursework, credit recovery completions"},
                {"Snowflake", "JDBC / Snowpipe", "Key Pair Auth", snowflakeStatus.connected() ? "Active" : "Not Connected", "Warehoused historical data, district reporting"},
                {"Databricks", "REST API / MLflow", "OAuth 2.0", databricksStatus.connected() ? "Active" : "Not Connected", "At-risk model training, AI insight generation"}
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
                String colorForStatus = switch (cols[3]) {
                    case "Active" -> "#2e7d32";
                    case "Not Connected" -> "#e65100";
                    default -> "var(--lumo-tertiary-text-color)";
                };
                cell.getStyle().set("color", colorForStatus).set("font-weight", "bold");
            }
            cell.add(new Span(cols[i]));
            row.add(cell);
        }
        return row;
    }
}
