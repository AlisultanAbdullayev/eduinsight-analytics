package com.eduinsight.integration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Backs the {@code eduinsight.integrations.*} keys in application.properties.
 * Both integrations are disabled by default — this demo has no live Snowflake
 * account or Databricks workspace to point at.
 */
@Component
@ConfigurationProperties(prefix = "eduinsight.integrations")
@Data
public class IntegrationProperties {

    private final Snowflake snowflake = new Snowflake();
    private final Databricks databricks = new Databricks();

    @Data
    public static class Snowflake {
        private boolean enabled = false;
        private String account = "";
        private String user = "";
        private String password = "";
        private String warehouse = "";
        private String database = "";
        private String schema = "";
    }

    @Data
    public static class Databricks {
        private boolean enabled = false;
        private String host = "";
        private String httpPath = "";
        private String token = "";
    }
}
