package com.eduinsight.ui;

import com.eduinsight.auth.SchoolAccount;
import com.eduinsight.service.StudentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@PageTitle("School Info | EduInsight Analytics")
@Route(value = "school-info", layout = MainLayout.class)
public class SchoolInfoView extends VerticalLayout {

    private record SchoolProfile(String name, String campus, String type, String district, String address,
                                  String phone, String principal, String established, Long fixedEnrollment) {
    }

    private static final List<SchoolProfile> SCHOOLS = List.of(
            new SchoolProfile("Harmony Discovery", "Harmony Discovery", "Public Charter School", "Harmony Public Schools",
                    "4200 Innovation Way, Houston, TX 77036", "(713) 555-0142", "Dr. Amina Cole", "2014", null),
            new SchoolProfile("Harmony Science", "Harmony Science", "Public Charter School", "Harmony Public Schools",
                    "8630 STEM Parkway, Austin, TX 78745", "(512) 555-0198", "Mr. Diego Fuentes", "2011", null),
            new SchoolProfile("Harmony Innovation", "Harmony Innovation", "Public Charter School", "Harmony Public Schools",
                    "1520 Discovery Blvd, San Antonio, TX 78223", "(210) 555-0176", "Ms. Priya Natarajan", "2016", null),
            // No seeded student data exists for this campus, so its enrollment is a fixed demo figure
            // rather than a live lookup (which would incorrectly show 0).
            new SchoolProfile("Horizon Leadership Academy", "Horizon Leadership Academy", "Private School", "Horizon Leadership Academy",
                    "300 Leadership Circle, Dallas, TX 75201", "(214) 555-0110", "Dr. Marcus Whitfield", "2009", 480L)
    );

    public SchoolInfoView(StudentService studentService) {
        addClassNames(LumoUtility.Padding.LARGE);
        setWidthFull();

        SchoolAccount account = SchoolAccount.current();
        add(pageHeader(account));
        add(buildSchoolGrid(account, studentService));
    }

    private Component pageHeader(SchoolAccount account) {
        boolean isDistrictWide = account != null && account.campus() == null;
        var title = UiUtils.pageTitle("School Information");
        var subtitle = new Paragraph(isDistrictWide
                ? "Directory of every school participating in the EduInsight pilot."
                : "Profile and contact details for your school, plus other pilot participants.");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE);
        var header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Component buildSchoolGrid(SchoolAccount account, StudentService studentService) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.getStyle().set("flex-wrap", "wrap");

        for (SchoolProfile school : SCHOOLS) {
            boolean isCurrent = account != null && school.name().equals(account.schoolName());
            long enrollment = school.fixedEnrollment() != null
                    ? school.fixedEnrollment()
                    : studentService.findByCampus(school.campus()).size();
            row.add(schoolCard(school, enrollment, isCurrent));
        }

        return row;
    }

    private Div schoolCard(SchoolProfile school, long enrollment, boolean isCurrent) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", isCurrent ? "2px solid var(--lumo-primary-color)" : "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "20px 24px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("flex", "1")
                .set("min-width", "280px");

        var titleRow = new HorizontalLayout();
        titleRow.setWidthFull();
        titleRow.setAlignItems(Alignment.CENTER);
        var nameEl = new H3(school.name());
        nameEl.getStyle().set("margin", "0");
        titleRow.add(nameEl);
        if (isCurrent) {
            var badge = new Span("Your School");
            badge.getStyle()
                    .set("background", "var(--lumo-primary-color)")
                    .set("color", "white")
                    .set("border-radius", "12px")
                    .set("padding", "2px 10px")
                    .set("font-size", "11px")
                    .set("font-weight", "bold")
                    .set("margin-left", "auto");
            titleRow.add(badge);
        }

        var tags = new Span(school.type() + " · " + school.district());
        tags.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)")
                .set("display", "block").set("margin", "4px 0 16px");

        var info = new VerticalLayout(
                infoRow(VaadinIcon.USERS, enrollment + " students enrolled"),
                infoRow(VaadinIcon.CALENDAR, "Established " + school.established()),
                infoRow(VaadinIcon.MAP_MARKER, school.address()),
                infoRow(VaadinIcon.PHONE, school.phone()),
                infoRow(VaadinIcon.USER, "Principal: " + school.principal())
        );
        info.setPadding(false);
        info.setSpacing(false);

        card.add(titleRow, tags, info);
        return card;
    }

    private Component infoRow(VaadinIcon icon, String text) {
        var iconComponent = icon.create();
        iconComponent.getStyle().set("width", "14px").set("height", "14px").set("color", "var(--lumo-tertiary-text-color)");

        var textEl = new Span(text);
        textEl.getStyle().set("font-size", "13px").set("color", "var(--lumo-body-text-color)");

        var row = new HorizontalLayout(iconComponent, textEl);
        row.setAlignItems(Alignment.CENTER);
        row.setSpacing(false);
        row.getStyle().set("gap", "8px").set("padding", "4px 0");
        return row;
    }
}
