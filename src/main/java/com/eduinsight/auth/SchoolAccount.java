package com.eduinsight.auth;

import com.vaadin.flow.server.VaadinSession;

import java.util.List;

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

    /**
     * The campuses this account may see student data for: just its own campus
     * for a single-campus login, or every campus in its district for a
     * district-wide login (campus == null). Keeps every tenant-facing view
     * scoped to the signed-in school instead of showing the whole database.
     */
    public List<String> campusScope() {
        return campus != null ? List.of(campus) : SchoolDirectory.campusesForDistrict(district);
    }
}
