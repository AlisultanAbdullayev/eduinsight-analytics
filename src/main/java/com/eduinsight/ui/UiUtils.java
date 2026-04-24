package com.eduinsight.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;

class UiUtils {

    static H2 pageTitle(String text) {
        H2 h = new H2(text);
        h.getStyle()
                .set("margin", "0 0 6px 0")
                .set("padding-left", "14px")
                .set("border-left", "4px solid #1565c0")
                .set("text-decoration", "none")
                .set("font-size", "22px")
                .set("color", "#1a1a2e");
        return h;
    }

    static H3 sectionTitle(String text) {
        H3 h = new H3(text);
        h.getStyle()
                .set("margin", "24px 0 12px 0")
                .set("padding-bottom", "6px")
                .set("border-bottom", "2px solid #e3eaf5")
                .set("text-decoration", "none")
                .set("font-size", "16px")
                .set("color", "#1a1a2e")
                .set("letter-spacing", "0.2px");
        return h;
    }

    static H4 subTitle(String text) {
        H4 h = new H4(text);
        h.getStyle()
                .set("margin", "16px 0 8px 0")
                .set("text-decoration", "none")
                .set("font-size", "14px")
                .set("color", "#444")
                .set("font-weight", "600");
        return h;
    }
}
