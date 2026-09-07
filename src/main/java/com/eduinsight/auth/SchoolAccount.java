package com.eduinsight.auth;

import com.vaadin.flow.server.VaadinSession;

/**
 * A dummy per-school login for demo purposes. {@code campus} matches the
 * campus names seeded by DataInitializer, or is {@code null} for the
 * district-wide account. {@code district} is the school network shown in
 * generic pilot copy — equal to {@code schoolName} for standalone schools.
 */
public record SchoolAccount(String username, String password, String schoolName, String campus, String district) {

    public static SchoolAccount current() {
        VaadinSession session = VaadinSession.getCurrent();
        return session == null ? null : session.getAttribute(SchoolAccount.class);
    }
}
