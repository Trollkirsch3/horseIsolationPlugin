package org.plugin2.mcplugin2.databases;

//package org.plugin2.beeSwarm.database;


import org.plugin2.mcplugin2.GameData;
import org.plugin2.mcplugin2.McPlugin2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseManager {

    public final McPlugin2 plugin;
    private Connection connection;

    public DatabaseManager(McPlugin2 plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException, ClassNotFoundException {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File databaseFile =
                new File(plugin.getDataFolder(), "beeswarm.db");

        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + databaseFile.getAbsolutePath()
        );
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        plugin.getLogger().info("Connected to SQLite database.");
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTables() throws SQLException {

        String playersTable = """
        CREATE TABLE IF NOT EXISTS players (
            uuid TEXT PRIMARY KEY NOT NULL,

            wins INTEGER NOT NULL DEFAULT 0,
            losses INTEGER NOT NULL DEFAULT 0,
            winStreak INTEGER NOT NULL DEFAULT 0,
            allTimeWinStreak INTEGER NOT NULL DEFAULT 0,
            accuracy DOUBLE NOT NULL DEFAULT 0
            )
        """;
        String gamesTable = """
        CREATE TABLE IF NOT EXISTS games (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            player1 TEXT NOT NULL,
            player2 TEXT NOT NULL,
            winner TEXT NOT NULL,
            timeSpendP1 INTEGER NOT NULL,
            timeSpendP2 INTEGER NOT NULL,
            duration INTEGER NOT NULL
            )
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(playersTable);
            statement.execute(gamesTable);
        }
    }

    public void disconnect() {

        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
