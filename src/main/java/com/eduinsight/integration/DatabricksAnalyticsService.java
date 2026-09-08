package com.eduinsight.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Real (optional) JDBC client for the Databricks AI/ML analytics layer
 * described on the Data Sources page. Disabled by default: this demo has no
 * live Databricks workspace, so {@link #checkStatus()} truthfully reports
 * "not connected" instead of the app pretending a model is in production.
 * Enable by setting eduinsight.integrations.databricks.* in application.properties.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabricksAnalyticsService {

    private static final int LOGIN_TIMEOUT_SECONDS = 5;

    private final IntegrationProperties properties;

    public IntegrationStatus checkStatus() {
        IntegrationProperties.Databricks config = properties.getDatabricks();
        if (!config.isEnabled()) {
            return new IntegrationStatus("Databricks", false,
                    "Not connected — set eduinsight.integrations.databricks.enabled=true with a workspace host, HTTP path, and token to enable");
        }

        String url = "jdbc:databricks://" + config.getHost() + ":443/default;"
                + "transportMode=http;ssl=1;httpPath=" + config.getHttpPath() + ";AuthMech=3;UID=token";

        try {
            DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
            try (Connection ignored = DriverManager.getConnection(url, "token", config.getToken())) {
                return new IntegrationStatus("Databricks", true, "Connected to " + config.getHost());
            }
        } catch (SQLException e) {
            log.warn("Databricks connection check failed: {}", e.getMessage());
            return new IntegrationStatus("Databricks", false, "Connection failed: " + e.getMessage());
        }
    }
}
