package com.eduinsight.integration;

/**
 * The truthful, live result of asking an external integration whether it is
 * actually reachable right now — as opposed to a hardcoded "Active" badge.
 */
public record IntegrationStatus(String platform, boolean connected, String detail) {
}
