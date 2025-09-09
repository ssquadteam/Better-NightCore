package su.nightexpress.nightcore.db.connection.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.db.config.DatabaseConfig;
import su.nightexpress.nightcore.db.connection.AbstractConnector;

public class MySQLConnector extends AbstractConnector {

    public MySQLConnector(@NotNull NightPlugin plugin, @NotNull DatabaseConfig config) {
        super(plugin, config);
    }

    @Override
    protected String getURL(@NotNull DatabaseConfig databaseConfig) {
        String host = databaseConfig.getHost();
        String database = databaseConfig.getDatabase();
        String options = databaseConfig.getUrlOptions();

        return "jdbc:mysql://" + host + "/" + database + options;
    }

    @Override
    protected void setupConfig(@NotNull DatabaseConfig databaseConfig) {
        this.config.setUsername(databaseConfig.getUsername());
        this.config.setPassword(databaseConfig.getPassword());
        this.config.setMaxLifetime(databaseConfig.getMaxLifetime());

        this.config.setMaximumPoolSize(10);
        this.config.setMinimumIdle(2);
        this.config.setConnectionTimeout(10000);
        this.config.setIdleTimeout(300000);
        this.config.setLeakDetectionThreshold(60000);

        this.config.setConnectionTestQuery("SELECT 1");
        this.config.setValidationTimeout(3000);

        this.config.addDataSourceProperty("useServerPrepStmts", "true");
        this.config.addDataSourceProperty("rewriteBatchedStatements", "true");
        this.config.addDataSourceProperty("maintainTimeStats", "false");
        this.config.addDataSourceProperty("useLocalSessionState", "true");
        this.config.addDataSourceProperty("useLocalTransactionState", "true");
        this.config.addDataSourceProperty("cacheResultSetMetadata", "true");
        this.config.addDataSourceProperty("cacheServerConfiguration", "true");
        this.config.addDataSourceProperty("elideSetAutoCommits", "true");
    }
}
