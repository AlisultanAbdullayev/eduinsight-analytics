package com.eduinsight.auth;

import java.util.List;
import java.util.Optional;

public final class SchoolDirectory {

    private static final String HARMONY = "Harmony Public Schools";

    public static final List<SchoolAccount> ACCOUNTS = List.of(
            new SchoolAccount("harmony.discovery", "demo123", "Harmony Discovery", "Harmony Discovery", HARMONY),
            new SchoolAccount("harmony.science", "demo123", "Harmony Science", "Harmony Science", HARMONY),
            new SchoolAccount("harmony.innovation", "demo123", "Harmony Innovation", "Harmony Innovation", HARMONY),
            new SchoolAccount("horizon.leadership", "demo123", "Horizon Leadership Academy", "Horizon Leadership Academy", "Horizon Leadership Academy"),
            new SchoolAccount("district.admin", "demo123", "Harmony Public Schools — District Office", null, HARMONY)
    );

    private SchoolDirectory() {
    }

    public static Optional<SchoolAccount> authenticate(String username, String password) {
        return ACCOUNTS.stream()
                .filter(a -> a.username().equalsIgnoreCase(username) && a.password().equals(password))
                .findFirst();
    }
}
