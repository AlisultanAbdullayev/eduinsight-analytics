package com.eduinsight.ui;

import com.eduinsight.service.AtRiskAnalysisService.RiskLevel;
import com.eduinsight.service.DashboardStatsService;
import com.eduinsight.service.DashboardStatsService.DashboardSummary;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Dashboard | EduInsight Analytics")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView(DashboardStatsService statsService) {
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        DashboardSummary summary = statsService.buildSummary();

        add(pageHeader());
        add(buildStatCards(summary));
        add(buildRiskBreakdown(summary));
        add(buildPlatformIngestionBar(summary));
    }

    private Component pageHeader() {
        var title = new H2("District Overview");
        title.addClassNames(LumoUtility.Margin.Bottom.XSMALL);
        var subtitle = new Paragraph("Real-time unified view across Schoology, Skyward, CodeHS, and GMETRIX — Harmony Public Schools Pilot 2026–27");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildStatCards(DashboardSummary s) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.add(
                statCard("Total Students", String.valueOf(s.totalStudents()), "3 Harmony Campuses", "#1565c0"),
                statCard("At-Risk Students", String.valueOf(s.atRiskCount()),
                        Math.round((s.atRiskCount() * 100.0) / s.totalStudents()) + "% of enrollment", "#b71c1c"),
                statCard("AP Pass Rate", String.format("%.1f%%", s.avgApPassRate()), "Avg across all exams", "#1b5e20"),
                statCard("IBC Certifications", String.valueOf(s.ibcPassedCount()), "Industry-Based Certs passed", "#4a148c")
        );
        return row;
    }

    private Div statCard(String title, String value, String subtitle, String accentColor) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-left", "4px solid " + accentColor)
                .set("border-radius", "8px")
                .set("padding", "20px 24px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("flex", "1")
                .set("min-width", "180px");

        var titleEl = new Span(title);
        titleEl.getStyle().set("font-size", "12px").set("color", "#666").set("text-transform", "uppercase").set("letter-spacing", "0.5px");

        var valueEl = new H3(value);
        valueEl.getStyle().set("margin", "6px 0 4px").set("font-size", "28px").set("color", accentColor);

        var subtitleEl = new Span(subtitle);
        subtitleEl.getStyle().set("font-size", "12px").set("color", "#999");

        card.add(new Div(titleEl), valueEl, new Div(subtitleEl));
        return card;
    }

    private Component buildRiskBreakdown(DashboardSummary s) {
        var section = new VerticalLayout();
        section.setPadding(false);

        var heading = new H3("Student Risk Distribution");
        heading.addClassNames(LumoUtility.Margin.Bottom.SMALL);

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        RiskLevel[] levels = {RiskLevel.HIGH, RiskLevel.MEDIUM, RiskLevel.LOW, RiskLevel.OK};
        String[] colors = {"#c62828", "#e65100", "#f9a825", "#2e7d32"};
        String[] icons = {"⚠", "!", "~", "✓"};

        long total = s.totalStudents();
        for (int i = 0; i < levels.length; i++) {
            long count = s.riskDistribution().getOrDefault(levels[i], 0L);
            double pct = total > 0 ? (count * 100.0) / total : 0;
            row.add(riskCard(levels[i].name(), count, pct, colors[i], icons[i]));
        }

        var campusRow = new HorizontalLayout();
        campusRow.setWidthFull();
        campusRow.setSpacing(true);
        s.atRiskByCampus().forEach((campus, count) ->
                campusRow.add(campusAtRiskBadge(campus, count)));

        section.add(heading, row, new H4("At-Risk by Campus"), campusRow);
        return section;
    }

    private Div riskCard(String level, long count, double pct, String color, String icon) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-top", "4px solid " + color)
                .set("border-radius", "8px")
                .set("padding", "16px 20px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)")
                .set("flex", "1")
                .set("text-align", "center");

        var iconEl = new Span(icon);
        iconEl.getStyle().set("font-size", "24px");
        var levelEl = new Div(new Span(level));
        levelEl.getStyle().set("font-weight", "bold").set("color", color).set("margin", "4px 0");
        var countEl = new Div(new Span(String.valueOf(count)));
        countEl.getStyle().set("font-size", "22px").set("font-weight", "bold");
        var pctEl = new Div(new Span(String.format("%.1f%%", pct)));
        pctEl.getStyle().set("font-size", "12px").set("color", "#888");

        card.add(iconEl, levelEl, countEl, pctEl);
        return card;
    }

    private Div campusAtRiskBadge(String campus, long count) {
        var badge = new Div();
        badge.getStyle()
                .set("background", "#fff3e0")
                .set("border", "1px solid #ff9800")
                .set("border-radius", "8px")
                .set("padding", "12px 20px")
                .set("flex", "1");
        var name = new Span(campus);
        name.getStyle().set("font-weight", "600").set("display", "block");
        var cnt = new Span(count + " at-risk students");
        cnt.getStyle().set("color", "#e65100").set("font-size", "13px");
        badge.add(name, cnt);
        return badge;
    }

    private Component buildPlatformIngestionBar(DashboardSummary s) {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(new H3("Platform Data Ingested (This Semester)"));

        String[][] platforms = {
                {"Schoology (Grades)", String.valueOf(s.totalGradeRecords()), "#1565c0"},
                {"Skyward (Attendance)", String.valueOf(s.totalAttendanceRecords()), "#6a1b9a"},
                {"CodeHS / GMETRIX", String.valueOf(s.totalCodingRecords()), "#00695c"},
                {"AP Assessment Portal", String.valueOf(s.totalAssessmentScores()), "#e65100"}
        };

        for (String[] p : platforms) {
            var row = new HorizontalLayout();
            row.setAlignItems(Alignment.CENTER);
            row.setWidthFull();

            var label = new Span(p[0]);
            label.getStyle().set("width", "220px").set("font-size", "13px");

            var bar = new Div();
            bar.getStyle()
                    .set("height", "18px")
                    .set("background", p[2])
                    .set("border-radius", "4px")
                    .set("min-width", "40px")
                    .set("width", (Math.min(Long.parseLong(p[1]) / 30, 500)) + "px");

            var count = new Span(p[1] + " records");
            count.getStyle().set("margin-left", "10px").set("font-size", "12px").set("color", "#555");

            row.add(label, bar, count);
            section.add(row);
        }
        return section;
    }
}
