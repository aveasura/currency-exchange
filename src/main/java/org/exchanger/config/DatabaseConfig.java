package org.exchanger.config;

import java.nio.file.Path;

public record DatabaseConfig(
        Path databasePath,
        int maxPoolSize,
        String poolName) {
}
