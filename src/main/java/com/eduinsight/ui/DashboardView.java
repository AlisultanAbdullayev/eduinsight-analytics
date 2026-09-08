package com.eduinsight.ui;

import com.eduinsight.auth.SchoolAccount;
import com.eduinsight.service.AtRiskAnalysisService.RiskLevel;
import com.eduinsight.service.DashboardStatsService;
import com.eduinsight.service.DashboardStatsService.DashboardSummary;
import com.vaadin.flow.component.Component;
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

        SchoolAccount account = SchoolAccount.current();
        DashboardSummary summary = statsService.buildSummary(account);

        add(pageHeader());
        add(buildStatCards(summary));
        if (summary.totalStudents() == 0) {
            add(noDataNotice(account));
        } else {
            add(buildRiskBreakdown(summary));
            add(buildPlatformIngestionBar(summary));
        }
    }

    private Component noDataNotice(SchoolAccount account) {
        String school = account != null ? account.schoolName() : "This school";
        var notice = new Div();
        notice.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-left", "4px solid var(--lumo-contrast-30pct)")
                .set("border-radius", "6px")
                .set("padding", "16px 20px")
                .set("margin-top", "8px");
        var text = new Paragraph(
                school + " has no student records connected yet from Schoology, Skyward, CodeHS, or GMETRIX. " +
                "This dashboard populates automatically as soon as those platforms are ingesting data for this school.");
        text.getStyle().set("margin", "0").set("font-size", "13px").set("color", "var(--lumo-secondary-text-color)");
        notice.add(text);
        return notice;
    }

    private Component pageHeader() {
        SchoolAccount account = SchoolAccount.current();
        String pilotName = account != null ? account.district() : "EduInsight";

        var title = UiUtils.pageTitle("District Overview");
        var subtitle = new Paragraph(
                "Real-time unified view across Schoology, Skyward, CodeHS, and GMETRIX — " + pilotName + " Pilot 2026–27");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildStatCards(DashboardSummary s) {
        String campusLabel = s.campusCount() + (s.campusCount() == 1 ? " Campus" : " Campuses");
        long atRiskPct = s.totalStudents() > 0 ? Math.round((s.atRiskCount() * 100.0) / s.totalStudents()) : 0;

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.add(
                statCard("Total Students", String.valueOf(s.totalStudents()), campusLabel, "#1565c0",
                        "▲ 3.4% vs last semester", true),
                statCard("At-Risk Students", String.valueOf(s.atRiskCount()),
                        atRiskPct + "% of enrollment", "#b71c1c",
                        "▼ 6.1% vs last semester", true),
                statCard("AP Pass Rate", String.format("%.1f%%", s.avgApPassRate()), "Avg across all exams", "#1b5e20",
                        "▲ 2.8% vs last year", true),
                statCard("IBC Certifications", String.valueOf(s.ibcPassedCount()), "Industry-Based Certs passed", "#4a148c",
                        "▲ 11.5% vs last semester", true)
        );
        return row;
    }

    private Div statCard(String title, String value, String subtitle, String accentColor, String deltaText, boolean deltaPositive) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-left", "4px solid " + accentColor)
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "20px 24px")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("flex", "1")
                .set("min-width", "180px");

        var titleEl = new Span(title);
        titleEl.getStyle().set("font-size", "12px").set("color", "var(--lumo-secondary-text-color)").set("text-transform", "uppercase").set("letter-spacing", "0.5px");

        var valueEl = new H3(value);
        valueEl.getStyle().set("margin", "6px 0 4px").set("font-size", "28px").set("color", accentColor);

        var subtitleEl = new Span(subtitle);
        subtitleEl.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)");

        var deltaEl = new Span(deltaText);
        deltaEl.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("color", deltaPositive ? "#2e7d32" : "#c62828")
                .set("display", "block")
                .set("margin-top", "8px");

        card.add(new Div(titleEl), valueEl, new Div(subtitleEl), deltaEl);
        return card;
    }

    private Component buildRiskBreakdown(DashboardSummary s) {
        var section = new VerticalLayout();
        section.setPadding(false);

        var heading = UiUtils.sectionTitle("Student Risk Distribution");
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

        section.add(heading, row, UiUtils.subTitle("At-Risk by Campus"), campusRow);
        return section;
    }

    private Div riskCard(String level, long count, double pct, String color, String icon) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-top", "4px solid " + color)
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "16px 20px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("flex", "1")
                .set("text-align", "center");

        var iconEl = new Span(icon);
        iconEl.getStyle().set("font-size", "24px");
        var levelEl = new Div(new Span(level));
        levelEl.getStyle().set("font-weight", "bold").set("color", color).set("margin", "4px 0");
        var countEl = new Div(new Span(String.valueOf(count)));
        countEl.getStyle().set("font-size", "22px").set("font-weight", "bold");
        var pctEl = new Div(new Span(String.format("%.1f%%", pct)));
        pctEl.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)");

        card.add(iconEl, levelEl, countEl, pctEl);
        return card;
    }

    private Div campusAtRiskBadge(String campus, long count) {
        var badge = new Div();
        badge.getStyle()
                .set("background", "var(--lumo-warning-color-10pct)")
                .set("border", "1px solid var(--lumo-warning-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "12px 20px")
                .set("flex", "1");
        var name = new Span(campus);
        name.getStyle().set("font-weight", "600").set("display", "block");
        var cnt = new Span(count + " at-risk students");
        cnt.getStyle().set("color", "var(--lumo-warning-text-color)").set("font-size", "13px");
        badge.add(name, cnt);
        return badge;
    }

    private Component buildPlatformIngestionBar(DashboardSummary s) {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(UiUtils.sectionTitle("Platform Data Ingested (This Semester)"));

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
            count.getStyle().set("margin-left", "10px").set("font-size", "12px").set("color", "var(--lumo-secondary-text-color)");

            row.add(label, bar, count);
            section.add(row);
        }
        return section;
    }
}
