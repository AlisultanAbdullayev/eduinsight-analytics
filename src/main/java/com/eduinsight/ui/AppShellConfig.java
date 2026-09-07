package com.eduinsight.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme("eduinsight")
@ColorScheme(ColorScheme.Value.SYSTEM)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
public class AppShellConfig implements AppShellConfigurator {
}
