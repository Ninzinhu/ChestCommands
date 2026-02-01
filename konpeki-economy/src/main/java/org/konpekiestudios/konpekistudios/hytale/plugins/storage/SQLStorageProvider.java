//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.storage;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.UserProfile;
import com.hypixel.hytale.logger.HytaleLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SQLStorageProvider implements StorageProvider {
    private HikariDataSource dataSource;
    private final Map<UUID, UserProfile> cache = new ConcurrentHashMap();
    private final String storageType;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public SQLStorageProvider(String storageType, String host, int port, String database, String username, String password) {
        this.storageType = storageType.toLowerCase();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void init() {
        HikariConfig config = new HikariConfig();
        switch (this.storageType) {
            case "sqlite":
                config.setJdbcUrl("jdbc:sqlite:KonpekiEconomy/economy.db");
                config.setDriverClassName("org.sqlite.JDBC");
                config.setMaximumPoolSize(1);
                break;
            case "mysql":
                config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=false&serverTimezone=UTC");
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setUsername(this.username);
                config.setPassword(this.password);
                config.setMaximumPoolSize(10);
                break;
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + this.storageType);
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setConnectionTimeout(10000L);
        this.dataSource = new HikariDataSource(config);
        this.createTable();
    }

    private void createTable() {
        String createTableSQL;
        if (this.storageType.equals("sqlite")) {
            createTableSQL = "CREATE TABLE IF NOT EXISTS economy_users (uuid TEXT PRIMARY KEY, balance REAL NOT NULL)";
        } else {
            createTableSQL = "CREATE TABLE IF NOT EXISTS economy_users (uuid VARCHAR(36) PRIMARY KEY, balance FLOAT NOT NULL)";
        }

        try (
                Connection conn = this.dataSource.getConnection();
                Statement stmt = conn.createStatement();
        ) {
            stmt.execute(createTableSQL);
            ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atInfo()).log("Economy table created or already exists");
        } catch (SQLException e) {
            ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atSevere()).log("Failed to create economy table", e);
        }

    }

    public void shutdown() {
        this.unloadAll();
        if (this.dataSource != null && !this.dataSource.isClosed()) {
            this.dataSource.close();
        }

    }

    public void loadPlayerData(UUID uuid) {
        String selectSQL = "SELECT uuid, balance FROM economy_users WHERE uuid = ?";

        try (
                Connection conn = this.dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(selectSQL);
        ) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserProfile profile = new UserProfile();
                profile.setUuid(rs.getString("uuid"));
                profile.setBalance(rs.getFloat("balance"));
                this.cache.put(uuid, profile);
            } else {
                UserProfile profile = new UserProfile();
                profile.setUuid(uuid.toString());
                profile.setBalance(KonpekiEconomy.getInstance().getConfigManager().getData().balance.starting);
                this.cache.put(uuid, profile);
                String insertSQL = "INSERT INTO economy_users (uuid, balance) VALUES (?, ?)";

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, uuid.toString());
                    insertStmt.setFloat(2, profile.balance);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atSevere()).log("Failed to load player data for " + String.valueOf(uuid), e);
        }

    }

    public UserProfile getProfile(UUID uuid) {
        return (UserProfile)this.cache.get(uuid);
    }

    public void updateProfile(UUID uuid, UserProfile profile) {
        this.cache.put(uuid, profile);
    }

    public void savePlayerData(UUID uuid) {
        UserProfile profile = (UserProfile)this.cache.get(uuid);
        if (profile != null) {
            String updateSQL = "UPDATE economy_users SET balance = ? WHERE uuid = ?";

            try (
                    Connection conn = this.dataSource.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(updateSQL);
            ) {
                stmt.setFloat(1, profile.balance);
                stmt.setString(2, uuid.toString());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    String insertSQL = "INSERT INTO economy_users (uuid, balance) VALUES (?, ?)";

                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, uuid.toString());
                        insertStmt.setFloat(2, profile.balance);
                        insertStmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atSevere()).log("Failed to save player data for " + String.valueOf(uuid), e);
            }

        }
    }

    public void unloadPlayer(UUID uuid) {
        this.savePlayerData(uuid);
        this.cache.remove(uuid);
    }

    public void unloadAll() {
        for(UUID uuid : this.cache.keySet()) {
            this.savePlayerData(uuid);
        }

        this.cache.clear();
    }

    public List<UserProfile> getTopBalances(int limit, int offset) {
        List<UserProfile> profiles = new ArrayList();
        String selectSQL = "SELECT uuid, balance FROM economy_users ORDER BY balance DESC LIMIT ? OFFSET ?";

        try (
                Connection conn = this.dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(selectSQL);
        ) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                UserProfile profile = new UserProfile();
                profile.setUuid(rs.getString("uuid"));
                profile.setBalance(rs.getFloat("balance"));
                profiles.add(profile);
            }
        } catch (SQLException e) {
            ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atSevere()).log("Failed to fetch top balances", e);
        }

        return profiles;
    }
}
