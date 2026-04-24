package com.eduinsight.ui;

import com.eduinsight.model.Student;
import com.eduinsight.service.AtRiskAnalysisService;
import com.eduinsight.service.AtRiskAnalysisService.RiskLevel;
import com.eduinsight.service.AtRiskAnalysisService.StudentRiskProfile;
import com.eduinsight.service.StudentService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@PageTitle("At-Risk Students | EduInsight Analytics")
@Route(value = "at-risk", layout = MainLayout.class)
public class AtRiskView extends VerticalLayout {

    private final StudentService studentService;
    private final AtRiskAnalysisService riskService;
    private final Grid<StudentRiskProfile> grid = new Grid<>();
    private List<StudentRiskProfile> allProfiles;

    public AtRiskView(StudentService studentService, AtRiskAnalysisService riskService) {
        this.studentService = studentService;
        this.riskService = riskService;
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        add(pageHeader());
        add(buildToolbar());
        add(buildGrid());

        loadData(null);
    }

    private Component pageHeader() {
        var title = new H2("At-Risk Student Identification");
        var subtitle = new Paragraph("Unified early-warning signals from Schoology (grades), Skyward (attendance), and CodeHS/GMETRIX (coding). Replaces 42+ min manual reconciliation.");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildToolbar() {
        ComboBox<String> campusFilter = new ComboBox<>("Filter by Campus");
        campusFilter.setItems(studentService.findDistinctCampuses());
        campusFilter.setClearButtonVisible(true);
        campusFilter.setWidth("250px");
        campusFilter.addValueChangeListener(e -> loadData(e.getValue()));

        ComboBox<String> riskFilter = new ComboBox<>("Filter by Risk");
        riskFilter.setItems("ALL", "HIGH", "MEDIUM", "LOW");
        riskFilter.setValue("ALL");
        riskFilter.setWidth("180px");
        riskFilter.addValueChangeListener(e -> filterByRisk(campusFilter.getValue(), e.getValue()));

        var row = new HorizontalLayout(campusFilter, riskFilter);
        row.setAlignItems(Alignment.END);
        return row;
    }

    private Grid<StudentRiskProfile> buildGrid() {
        grid.setWidthFull();
        grid.setHeight("500px");

        grid.addColumn(p -> p.student().getFullName())
                .setHeader("Student Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(p -> p.student().getCampus())
                .setHeader("Campus").setSortable(true).setAutoWidth(true);
        grid.addColumn(p -> p.student().getGradeLevel() + "th Grade")
                .setHeader("Grade").setSortable(true).setWidth("100px");
        grid.addColumn(p -> String.format("%.1f%%", p.gradeAverage()))
                .setHeader("Grade Avg").setSortable(true).setWidth("110px");
        grid.addColumn(p -> String.format("%.1f%%", p.attendanceRate()))
                .setHeader("Attendance").setSortable(true).setWidth("120px");
        grid.addColumn(p -> String.format("%.1f%%", p.codingCompletion()))
                .setHeader("Coding %").setSortable(true).setWidth("110px");
        grid.addColumn(new ComponentRenderer<>(p -> riskBadge(p.riskLevel())))
                .setHeader("Risk Level").setWidth("130px");
        grid.addColumn(new ComponentRenderer<>(this::flagsCell))
                .setHeader("Flags").setWidth("200px");

        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::buildDetailPanel));

        return grid;
    }

    private Span riskBadge(RiskLevel level) {
        String color = switch (level) {
            case HIGH -> "#c62828";
            case MEDIUM -> "#e65100";
            case LOW -> "#f9a825";
            case OK -> "#2e7d32";
        };
        var badge = new Span(level.name());
        badge.getStyle()
                .set("background", color)
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "2px 10px")
                .set("font-size", "12px")
                .set("font-weight", "bold");
        return badge;
    }

    private Component flagsCell(StudentRiskProfile p) {
        var row = new HorizontalLayout();
        row.setSpacing(false);
        if (p.gradeFlagged()) row.add(flagChip("Grade", "#1565c0"));
        if (p.attendanceFlagged()) row.add(flagChip("Attend", "#6a1b9a"));
        if (p.codingFlagged()) row.add(flagChip("Coding", "#00695c"));
        return row;
    }

    private Span flagChip(String label, String color) {
        var chip = new Span(label);
        chip.getStyle()
                .set("background", color + "22")
                .set("color", color)
                .set("border", "1px solid " + color)
                .set("border-radius", "8px")
                .set("padding", "1px 8px")
                .set("font-size", "11px")
                .set("margin-right", "4px");
        return chip;
    }

    private Component buildDetailPanel(StudentRiskProfile p) {
        var panel = new Div();
        panel.getStyle().set("padding", "16px 24px").set("background", "#f9f9f9");

        var title = new H4(p.student().getFullName() + " — Data Source Breakdown");
        title.getStyle().set("margin-top", "0");

        var row = new HorizontalLayout();
        row.add(
                sourceCard("Schoology (Grades)", String.format("%.1f%%", p.gradeAverage()),
                        p.gradeFlagged() ? "Below 70% threshold" : "On track", p.gradeFlagged()),
                sourceCard("Skyward (Attendance)", String.format("%.1f%%", p.attendanceRate()),
                        p.attendanceFlagged() ? "Below 90% threshold" : "On track", p.attendanceFlagged()),
                sourceCard("CodeHS / GMETRIX", String.format("%.1f%%", p.codingCompletion()),
                        p.codingFlagged() ? "Below 60% completion" : "On track", p.codingFlagged())
        );

        var note = new Paragraph("Student ID: " + p.student().getStudentId() +
                " | Email: " + p.student().getEmail() +
                " | Campus: " + p.student().getCampus());
        note.getStyle().set("font-size", "12px").set("color", "#888").set("margin-bottom", "0");

        panel.add(title, row, note);
        return panel;
    }

    private Div sourceCard(String source, String value, String status, boolean flagged) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-left", "4px solid " + (flagged ? "#c62828" : "#2e7d32"))
                .set("border-radius", "6px")
                .set("padding", "12px 16px")
                .set("margin-right", "12px")
                .set("min-width", "180px");
        var src = new Span(source);
        src.getStyle().set("font-size", "12px").set("color", "#666").set("display", "block");
        var val = new H4(value);
        val.getStyle().set("margin", "4px 0").set("color", flagged ? "#c62828" : "#2e7d32");
        var st = new Span(flagged ? "⚠ " + status : "✓ " + status);
        st.getStyle().set("font-size", "12px").set("color", flagged ? "#e65100" : "#2e7d32");
        card.add(src, val, st);
        return card;
    }

    private void loadData(String campus) {
        List<Student> students = (campus == null || campus.isEmpty())
                ? studentService.findAll()
                : studentService.findByCampus(campus);
        allProfiles = riskService.analyzeAll(students);
        grid.setItems(allProfiles);
    }

    private void filterByRisk(String campus, String riskValue) {
        loadData(campus);
        if (riskValue != null && !riskValue.equals("ALL")) {
            RiskLevel filter = RiskLevel.valueOf(riskValue);
            grid.setItems(allProfiles.stream().filter(p -> p.riskLevel() == filter).toList());
        }
    }
}
