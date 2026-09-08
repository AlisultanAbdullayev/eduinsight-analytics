package com.eduinsight.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Real (optional) JDBC client for the Snowflake data warehouse layer described
 * on the Data Sources page. Disabled by default: this demo has no live
 * Snowflake account, so {@link #checkStatus()} truthfully reports "not
 * connected" instead of the app pretending the warehouse is Active.
 * Enable by setting eduinsight.integrations.snowflake.* in application.properties.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnowflakeWarehouseService {

    private static final int LOGIN_TIMEOUT_SECONDS = 5;

    private final IntegrationProperties properties;

    public IntegrationStatus checkStatus() {
        IntegrationProperties.Snowflake config = properties.getSnowflake();
        if (!config.isEnabled()) {
            return new IntegrationStatus("Snowflake", false,
                    "Not connected — set eduinsight.integrations.snowflake.enabled=true with account credentials to enable");
        }

        String url = "jdbc:snowflake://" + config.getAccount() + ".snowflakecomputing.com/"
                + "?warehouse=" + config.getWarehouse()
                + "&db=" + config.getDatabase()
                + "&schema=" + config.getSchema();

        try {
            DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
            try (Connection ignored = DriverManager.getConnection(url, config.getUser(), config.getPassword())) {
                return new IntegrationStatus("Snowflake", true, "Connected to " + config.getAccount() + ".snowflakecomputing.com");
            }
        } catch (SQLException e) {
            log.warn("Snowflake connection check failed: {}", e.getMessage());
            return new IntegrationStatus("Snowflake", false, "Connection failed: " + e.getMessage());
        }
    }
}
