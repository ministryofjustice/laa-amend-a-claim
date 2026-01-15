package uk.gov.justice.laa.amend.claim.utils;

public final class DbConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public DbConfig(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String host() { return host; }
    public int port() { return port; }
    public String database() { return database; }
    public String username() { return username; }
    public String password() { return password; }

    public static DbConfig fromEnvConfig() {
        String pass = EnvConfig.dbPassword();
        if (pass == null || pass.isBlank()) {
            throw new IllegalStateException("DB_PASSWORD is not set (env or .env).");
        }
        return new DbConfig(
                EnvConfig.dbHost(),
                EnvConfig.dbPort(),
                EnvConfig.dbName(),
                EnvConfig.dbUser(),
                pass
        );
    }

    public String jdbcUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + database;
    }
}