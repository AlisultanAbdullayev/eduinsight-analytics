package com.eduinsight.ui;

import com.eduinsight.auth.SchoolAccount;
import com.eduinsight.auth.SchoolDirectory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Sign In | EduInsight Analytics")
@Route(value = "login", autoLayout = false)
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.setI18n(buildI18n());
        loginForm.addLoginListener(event ->
                SchoolDirectory.authenticate(event.getUsername(), event.getPassword())
                        .ifPresentOrElse(this::onSuccess, () -> event.getSource().setError(true)));

        var card = new VerticalLayout(brand(), loginForm, buildDemoCredentialsCard());
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setWidth("420px");
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("box-shadow", "var(--lumo-box-shadow-m)")
                .set("padding", "var(--lumo-space-l)");

        add(card);
    }

    private Div brand() {
        var brand = new Div();
        brand.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.AlignItems.CENTER, LumoUtility.Margin.Bottom.MEDIUM);

        var mark = new Span("E");
        mark.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("width", "36px")
                .set("height", "36px")
                .set("border-radius", "10px")
                .set("background", "#131b2c")
                .set("color", "white")
                .set("font-weight", "bold")
                .set("margin-bottom", "10px");

        var name = new Span("EduInsight Analytics");
        name.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD);
        var tagline = new Span("Multi-District Pilot Program");
        tagline.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        brand.add(mark, name, tagline);
        return brand;
    }

    private LoginI18n buildI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Sign in to your school");
        i18n.getForm().setUsername("School Username");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Sign In");
        i18n.getErrorMessage().setTitle("Incorrect username or password");
        i18n.getErrorMessage().setMessage("Check the demo credentials below and try again.");
        i18n.setAdditionalInformation("Demo build — sign in with your school's Harmony ClassLink credentials.");
        return i18n;
    }

    private void onSuccess(SchoolAccount account) {
        VaadinSession.getCurrent().setAttribute(SchoolAccount.class, account);
        UI.getCurrent().navigate("dashboard");
    }

    private Div buildDemoCredentialsCard() {
        var box = new Div();
        box.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-m)")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        var title = new H4("Demo credentials");
        title.getStyle().set("margin", "0 0 8px");
        box.add(title);

        for (SchoolAccount account : SchoolDirectory.ACCOUNTS) {
            var row = new Paragraph(account.schoolName() + " — " + account.username() + " / " + account.password());
            row.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);
            box.add(row);
        }
        return box;
    }
}
