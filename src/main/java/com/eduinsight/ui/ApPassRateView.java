package com.eduinsight.ui;

import com.eduinsight.service.DashboardStatsService;
import com.eduinsight.service.DashboardStatsService.ApPassRateStats;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@PageTitle("AP Pass Rates | EduInsight Analytics")
@Route(value = "ap-rates", layout = MainLayout.class)
public class ApPassRateView extends VerticalLayout {

    public ApPassRateView(DashboardStatsService statsService) {
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        List<ApPassRateStats> stats = statsService.buildApPassRates();

        add(pageHeader());
        add(buildSummaryCards(stats));
        add(buildRateGrid(stats));
        add(buildCampusBreakdown(stats));
    }

    private Component pageHeader() {
        var title = UiUtils.pageTitle("AP Exam Pass Rate Tracker");
        var subtitle = new Paragraph("Pass threshold: score ≥ 3. Data sourced from district AP Assessment Portal. Exam year: 2025.");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildSummaryCards(List<ApPassRateStats> stats) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        for (ApPassRateStats s : stats) {
            row.add(examCard(s));
        }
        return row;
    }

    private Div examCard(ApPassRateStats s) {
        String color = s.passRate() >= 60 ? "#2e7d32" : s.passRate() >= 40 ? "#e65100" : "#c62828";
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-top", "4px solid " + color)
                .set("border-radius", "8px")
                .set("padding", "16px 20px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.07)")
                .set("flex", "1")
                .set("min-width", "160px");

        var name = new Span(s.examName());
        name.getStyle().set("font-size", "12px").set("color", "#666").set("display", "block").set("text-transform", "uppercase");
        var rate = new H3(String.format("%.1f%%", s.passRate()));
        rate.getStyle().set("margin", "6px 0 4px").set("color", color);
        var detail = new Span(s.passCount() + "/" + s.totalTakers() + " students passed");
        detail.getStyle().set("font-size", "12px").set("color", "#888");

        card.add(name, rate, detail);
        return card;
    }

    private Component buildRateGrid(List<ApPassRateStats> stats) {
        var heading = UiUtils.sectionTitle("Detailed Pass Rate Table");
        Grid<ApPassRateStats> grid = new Grid<>();
        grid.setWidthFull();
        grid.setAllRowsVisible(true);

        grid.addColumn(ApPassRateStats::examName).setHeader("AP Exam").setAutoWidth(true);
        grid.addColumn(s -> s.totalTakers()).setHeader("Total Students").setWidth("150px");
        grid.addColumn(s -> s.passCount()).setHeader("Passed (≥3)").setWidth("140px");
        grid.addColumn(s -> s.totalTakers() - s.passCount()).setHeader("Did Not Pass").setWidth("150px");
        grid.addColumn(new ComponentRenderer<>(s -> {
            var bar = buildRateBar(s.passRate());
            return bar;
        })).setHeader("Pass Rate").setWidth("300px");

        grid.setItems(stats);
        var section = new VerticalLayout(heading, grid);
        section.setPadding(false);
        return section;
    }

    private Div buildRateBar(double rate) {
        String color = rate >= 60 ? "#2e7d32" : rate >= 40 ? "#e65100" : "#c62828";
        var container = new Div();
        container.getStyle().set("display", "flex").set("align-items", "center").set("gap", "8px");

        var track = new Div();
        track.getStyle().set("background", "#f0f0f0").set("border-radius", "4px")
                .set("height", "14px").set("width", "200px").set("overflow", "hidden");

        var fill = new Div();
        fill.getStyle().set("background", color).set("height", "100%")
                .set("width", String.format("%.0f%%", Math.min(rate, 100)));
        track.add(fill);

        var label = new Span(String.format("%.1f%%", rate));
        label.getStyle().set("font-size", "12px").set("font-weight", "bold").set("color", color);

        container.add(track, label);
        return container;
    }

    private Component buildCampusBreakdown(List<ApPassRateStats> stats) {
        var heading = UiUtils.sectionTitle("Pass Rate by Campus");
        var grid = new Grid<CampusRow>();
        grid.setWidthFull();
        grid.setAllRowsVisible(true);

        grid.addColumn(r -> r.campus()).setHeader("Campus").setAutoWidth(true);
        for (ApPassRateStats s : stats) {
            grid.addColumn(new ComponentRenderer<>(r -> {
                double rate = s.passByCampus().getOrDefault(r.campus(), 0.0);
                return buildRateBar(rate);
            })).setHeader(s.examName()).setWidth("220px");
        }

        List<String> campuses = List.of("Harmony Discovery", "Harmony Science", "Harmony Innovation");
        grid.setItems(campuses.stream().map(CampusRow::new).toList());

        var section = new VerticalLayout(heading, grid);
        section.setPadding(false);
        return section;
    }

    private record CampusRow(String campus) {}
}
