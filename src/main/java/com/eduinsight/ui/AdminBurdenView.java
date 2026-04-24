package com.eduinsight.ui;

import com.eduinsight.repository.StudentRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Admin Burden | EduInsight Analytics")
@Route(value = "admin-burden", layout = MainLayout.class)
public class AdminBurdenView extends VerticalLayout {

    private static final int TEACHERS_PER_CAMPUS = 30;
    private static final int CAMPUSES = 3;
    private static final int TOTAL_TEACHERS = TEACHERS_PER_CAMPUS * CAMPUSES;
    private static final double MANUAL_MINUTES_PER_PERIOD = 42.0;
    private static final double EDUINSIGHT_MINUTES_PER_PERIOD = 3.0;
    private static final int PERIODS_PER_WEEK = 5;
    private static final int WEEKS_PER_SEMESTER = 18;

    public AdminBurdenView(StudentRepository studentRepo) {
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        double weeklySavingsPerTeacher = (MANUAL_MINUTES_PER_PERIOD - EDUINSIGHT_MINUTES_PER_PERIOD) * PERIODS_PER_WEEK;
        double semesterSavingsPerTeacher = weeklySavingsPerTeacher * WEEKS_PER_SEMESTER;
        double districtWeeklySavings = weeklySavingsPerTeacher * TOTAL_TEACHERS;
        double districtSemesterHours = (districtWeeklySavings * WEEKS_PER_SEMESTER) / 60.0;

        add(pageHeader());
        add(buildKeyMetric());
        add(buildComparisonSection());
        add(buildSavingsProjection(weeklySavingsPerTeacher, semesterSavingsPerTeacher,
                districtWeeklySavings, districtSemesterHours));
        add(buildInstructionImpact(districtSemesterHours));
        add(buildMethodologyNote());
    }

    private Component pageHeader() {
        var title = UiUtils.pageTitle("Administrative Burden Reduction");
        var subtitle = new Paragraph(
                "Quantifying the time EduInsight returns to teachers by eliminating manual data reconciliation across " +
                "Schoology, Skyward, CodeHS, and GMETRIX. Source: Harmony district instructional audit."
        );
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildKeyMetric() {
        var card = new Div();
        card.getStyle()
                .set("background", "linear-gradient(135deg, #1565c0 0%, #0d47a1 100%)")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "32px 40px")
                .set("text-align", "center")
                .set("margin-bottom", "24px");

        var label = new Span("TIME SAVED PER TEACHER PER PLANNING PERIOD");
        label.getStyle().set("font-size", "12px").set("letter-spacing", "1px").set("opacity", "0.8").set("display", "block");
        var value = new H1("39 minutes");
        value.getStyle().set("margin", "12px 0 8px").set("font-size", "52px").set("color", "white");
        var sub = new Span("from 42 min manual → 3 min with EduInsight");
        sub.getStyle().set("opacity", "0.85").set("font-size", "16px");
        var source = new Span("Source: Harmony District Instructional Audit (Kahraman, 2026)");
        source.getStyle().set("font-size", "11px").set("opacity", "0.6").set("display", "block").set("margin-top", "12px");

        card.add(label, value, sub, source);
        return card;
    }

    private Component buildComparisonSection() {
        var heading = UiUtils.sectionTitle("Planning Period: Before vs. After EduInsight");

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        row.add(comparisonCard("WITHOUT EduInsight",
                new String[][]{
                        {"Login to Schoology", "4 min"},
                        {"Export grades to spreadsheet", "6 min"},
                        {"Login to Skyward", "4 min"},
                        {"Check attendance records", "8 min"},
                        {"Open CodeHS dashboard", "3 min"},
                        {"Cross-reference GMETRIX", "7 min"},
                        {"Manually merge into notes", "10 min"}
                }, "42 min total", "#c62828", false));

        row.add(comparisonCard("WITH EduInsight",
                new String[][]{
                        {"Open EduInsight dashboard", "30 sec"},
                        {"View unified student status", "1 min"},
                        {"Review flagged students", "1.5 min"}
                }, "3 min total", "#2e7d32", true));

        var section = new VerticalLayout(heading, row);
        section.setPadding(false);
        return section;
    }

    private Div comparisonCard(String title, String[][] steps, String total, String color, boolean isGood) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border", "2px solid " + (isGood ? "#2e7d32" : "#e0e0e0"))
                .set("border-radius", "10px")
                .set("padding", "20px 24px")
                .set("flex", "1");

        var titleEl = new H4(title);
        titleEl.getStyle().set("color", color).set("margin-top", "0");

        card.add(titleEl);
        for (String[] step : steps) {
            var stepRow = new Div();
            stepRow.getStyle().set("display", "flex").set("justify-content", "space-between")
                    .set("padding", "5px 0").set("border-bottom", "1px solid #f5f5f5").set("font-size", "13px");
            var taskEl = new Span(step[0]);
            var timeEl = new Span(step[1]);
            timeEl.getStyle().set("color", isGood ? "#2e7d32" : "#c62828").set("font-weight", "bold");
            stepRow.add(taskEl, timeEl);
            card.add(stepRow);
        }

        var totalDiv = new Div();
        totalDiv.getStyle().set("margin-top", "12px").set("padding-top", "10px").set("border-top", "2px solid " + color)
                .set("display", "flex").set("justify-content", "space-between").set("font-weight", "bold");
        totalDiv.add(new Span("TOTAL"), new Span(total));
        totalDiv.getStyle().set("color", color);
        card.add(totalDiv);

        return card;
    }

    private Component buildSavingsProjection(double weeklyPerTeacher, double semesterPerTeacher,
                                              double districtWeekly, double districtSemesterHours) {
        var heading = UiUtils.sectionTitle("Savings Projection (3 Campuses · " + TOTAL_TEACHERS + " Teachers)");

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.add(
                projectionCard("Per Teacher / Week", String.format("%.0f min", weeklyPerTeacher), "#1565c0"),
                projectionCard("Per Teacher / Semester", String.format("%.0f min (%.1f hrs)", semesterPerTeacher, semesterPerTeacher / 60), "#6a1b9a"),
                projectionCard("District / Week", String.format("%.0f min (%.1f hrs)", districtWeekly, districtWeekly / 60), "#00695c"),
                projectionCard("District / Semester", String.format("%.0f hours", districtSemesterHours), "#e65100")
        );

        var section = new VerticalLayout(heading, row);
        section.setPadding(false);
        return section;
    }

    private Div projectionCard(String label, String value, String color) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-left", "4px solid " + color)
                .set("border-radius", "8px")
                .set("padding", "16px 20px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.07)")
                .set("flex", "1");
        var labelEl = new Span(label);
        labelEl.getStyle().set("font-size", "11px").set("color", "#888").set("text-transform", "uppercase").set("letter-spacing", "0.5px");
        var valueEl = new H3(value);
        valueEl.getStyle().set("margin", "6px 0 0").set("color", color).set("font-size", "20px");
        card.add(new Div(labelEl), valueEl);
        return card;
    }

    private Component buildInstructionImpact(double districtSemesterHours) {
        var section = new VerticalLayout();
        section.setPadding(false);
        section.add(UiUtils.sectionTitle("What Those Hours Mean for Students"));

        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        long lessonPlanHours = Math.round(districtSemesterHours * 0.40);
        long studentInterventionHours = Math.round(districtSemesterHours * 0.35);
        long professionalDevelopmentHours = Math.round(districtSemesterHours * 0.25);

        row.add(impactCard("📚", "Lesson Planning", lessonPlanHours + " hours", "Richer curriculum development", "#1565c0"));
        row.add(impactCard("🎓", "Student Interventions", studentInterventionHours + " hours", "1:1 tutoring & targeted support", "#c62828"));
        row.add(impactCard("💡", "Professional Development", professionalDevelopmentHours + " hours", "Teacher growth & collaboration", "#6a1b9a"));

        section.add(row);
        return section;
    }

    private Div impactCard(String icon, String title, String hours, String description, String color) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "10px")
                .set("padding", "20px 24px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.07)")
                .set("flex", "1")
                .set("text-align", "center");
        var iconEl = new Span(icon);
        iconEl.getStyle().set("font-size", "32px");
        var titleEl = new H4(title);
        titleEl.getStyle().set("color", color).set("margin", "10px 0 4px");
        var hoursEl = new H3(hours);
        hoursEl.getStyle().set("margin", "0 0 8px").set("color", "#333");
        var desc = new Span(description);
        desc.getStyle().set("font-size", "13px").set("color", "#888");
        card.add(iconEl, titleEl, hoursEl, new Div(desc));
        return card;
    }

    private Component buildMethodologyNote() {
        var note = new Div();
        note.getStyle()
                .set("background", "#f8f9fa")
                .set("border-left", "4px solid #aaa")
                .set("border-radius", "6px")
                .set("padding", "14px 18px")
                .set("margin-top", "8px");
        var text = new Paragraph(
                "Methodology: Manual time (42 min) confirmed via Harmony district instructional audit conducted by " +
                "District STEM/GT Instructional Coach Yilmaz Kahraman (2026). EduInsight time (3 min) estimated from " +
                "teacher feedback during early prototype testing. Calculation assumes 5 planning periods/week, 18-week semester, " +
                "30 teachers/campus × 3 campuses = 90 teachers district-wide."
        );
        text.getStyle().set("margin", "0").set("font-size", "12px").set("color", "#666");
        note.add(text);
        return note;
    }
}
