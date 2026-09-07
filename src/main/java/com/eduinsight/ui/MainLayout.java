package com.eduinsight.ui;

import com.eduinsight.auth.SchoolAccount;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        H2 viewTitle = new H2("EduInsight Analytics");
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Span badge = new Span("FERPA Compliant");
        badge.getStyle()
                .set("background", "var(--lumo-success-color)")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "2px 10px")
                .set("font-size", "11px")
                .set("font-weight", "bold")
                .set("margin-left", "12px");

        var titleGroup = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(viewTitle, badge);
        titleGroup.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        SchoolAccount account = SchoolAccount.current();
        if (account != null) {
            Span school = new Span(account.schoolName());
            school.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.Margin.Left.MEDIUM);
            titleGroup.add(school);
        }

        Button themeToggle = new Button(VaadinIcon.MOON.create());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
        themeToggle.setAriaLabel("Toggle dark mode");
        themeToggle.addClickListener(e -> {
            var page = UI.getCurrent().getPage();
            boolean goingDark = page.getColorScheme() != ColorScheme.Value.DARK;
            page.setColorScheme(goingDark ? ColorScheme.Value.DARK : ColorScheme.Value.LIGHT);
        });

        Button logout = new Button("Log out", e -> logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var actions = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(themeToggle, logout);
        actions.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        var header = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(titleGroup, actions);
        header.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();

        addToNavbar(true, toggle, header);
    }

    private void logout() {
        VaadinSession.getCurrent().setAttribute(SchoolAccount.class, null);
        UI.getCurrent().getPage().setLocation("login");
    }

    private void addDrawerContent() {
        var appName = new Span("EduInsight");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        appName.getStyle().set("color", "white");

        var tagline = new Span("Unified Student Intelligence");
        tagline.addClassNames(LumoUtility.FontSize.XSMALL);
        tagline.getStyle().set("color", "rgba(255, 255, 255, 0.6)");

        var header = new Header();
        header.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.LARGE, LumoUtility.Margin.Bottom.SMALL);
        header.add(appName, tagline);

        Scroller scroller = new Scroller(createNavigation());
        scroller.getStyle().set("flex", "1");

        var drawerContent = new Div(header, scroller, createFooter());
        drawerContent.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100%")
                .set("background", "#131b2c")
                .set("color-scheme", "dark");

        addToDrawer(drawerContent);
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("At-Risk Students", AtRiskView.class, VaadinIcon.WARNING.create()));
        nav.addItem(new SideNavItem("AP Pass Rates", ApPassRateView.class, VaadinIcon.CHART_LINE.create()));
        nav.addItem(new SideNavItem("Data Sources", DataSourcesView.class, VaadinIcon.CONNECT.create()));
        nav.addItem(new SideNavItem("School Info", SchoolInfoView.class, VaadinIcon.INSTITUTION.create()));
        nav.addItem(new SideNavItem("Admin Burden", AdminBurdenView.class, VaadinIcon.CLOCK.create()));

        return nav;
    }

    private Div createFooter() {
        SchoolAccount account = SchoolAccount.current();
        String schoolName = account != null ? account.schoolName() : "Guest";
        String district = account != null ? account.district() : null;
        String captionText = district == null ? "EduInsight Pilot • 2026–27"
                : district.equals(schoolName) ? "EduInsight Pilot • 2026–27"
                : district + " Pilot • 2026–27";

        var avatar = new Span(initials(schoolName));
        avatar.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("width", "32px")
                .set("height", "32px")
                .set("border-radius", "50%")
                .set("background", "var(--lumo-primary-color)")
                .set("color", "white")
                .set("font-size", "12px")
                .set("font-weight", "bold")
                .set("flex", "0 0 auto");

        var schoolNameEl = new Span(schoolName);
        schoolNameEl.getStyle()
                .set("color", "white")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("display", "block")
                .set("white-space", "nowrap")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis");

        var caption = new Span(captionText);
        caption.getStyle().set("color", "rgba(255, 255, 255, 0.5)").set("font-size", "11px");

        var textColumn = new Div(schoolNameEl, caption);
        textColumn.getStyle().set("display", "flex").set("flex-direction", "column").set("min-width", "0").set("overflow", "hidden");

        var row = new Div(avatar, textColumn);
        row.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "10px")
                .set("padding", "14px 16px")
                .set("border-top", "1px solid rgba(255, 255, 255, 0.08)");

        return row;
    }

    private String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String word : name.replace("—", " ").trim().split("\\s+")) {
            if (!word.isEmpty() && Character.isLetter(word.charAt(0))) {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }
            if (sb.length() == 2) break;
        }
        return sb.isEmpty() ? "?" : sb.toString();
    }
}
