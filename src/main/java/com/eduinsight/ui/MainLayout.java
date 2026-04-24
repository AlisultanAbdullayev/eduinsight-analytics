package com.eduinsight.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
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
                .set("background", "#2e7d32")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "2px 10px")
                .set("font-size", "11px")
                .set("font-weight", "bold")
                .set("margin-left", "12px");

        var header = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(viewTitle, badge);
        header.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        addToNavbar(true, toggle, header);
    }

    private void addDrawerContent() {
        var appName = new Span("EduInsight");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        var tagline = new Span("Unified Student Intelligence");
        tagline.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

        var header = new Header();
        header.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.LARGE, LumoUtility.Margin.Bottom.SMALL);
        header.add(appName, tagline);

        Scroller scroller = new Scroller(createNavigation());
        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("At-Risk Students", AtRiskView.class, VaadinIcon.WARNING.create()));
        nav.addItem(new SideNavItem("AP Pass Rates", ApPassRateView.class, VaadinIcon.CHART_LINE.create()));
        nav.addItem(new SideNavItem("Data Sources", DataSourcesView.class, VaadinIcon.CONNECT.create()));
        nav.addItem(new SideNavItem("Admin Burden", AdminBurdenView.class, VaadinIcon.CLOCK.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM, LumoUtility.Padding.Vertical.SMALL);

        Span info = new Span("Harmony Public Schools Pilot • 2026–27");
        info.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.TextColor.SECONDARY);
        layout.add(info);
        return layout;
    }
}
