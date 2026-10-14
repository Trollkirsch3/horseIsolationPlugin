package org.plugin2.mcplugin2.databases;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.plugin2.mcplugin2.GameData;
import org.plugin2.mcplugin2.PlayerData;
import org.plugin2.mcplugin2.leaderboard.GameHistory;
import org.plugin2.mcplugin2.leaderboard.Leaderboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameDatabase {

    private final DatabaseManager database;

    public GameDatabase(DatabaseManager database) {
        this.database = database;
    }

    public int saveGame(@NotNull GameData data) {
        String sql = """
                INSERT INTO games (
                            player1,
                            player2,
                            winner,
                            timeSpendP1,
                            timeSpendP2,
                            duration
                            )
                    VALUES (?, ?, ?, ?, ?, ?)
                            """;
        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, data.player1.toString());
            statement.setString(2, data.player2.toString());
            statement.setString(3, data.winner.toString());

            statement.setInt(4, data.timeSpendP1);
            statement.setInt(5, data.timeSpendP2);
            statement.setInt(6, data.duration);
            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    public List<GameData> getHistory(UUID player, int page){
        String sql = """
                SELECT *
                FROM games
                WHERE player1 = ? OR player2 = ?
                ORDER BY id DESC
                LIMIT ?
                OFFSET ?;
                """;
        List<GameData> games = new ArrayList<>();
        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(sql)) {

            statement.setString(1, player.toString());
            statement.setString(2, player.toString());

            statement.setInt(3, GameHistory.gamesPerPage);
            statement.setInt(4, GameHistory.gamesPerPage*(page-1));

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    UUID player1 = UUID.fromString(result.getString("player1"));
                    UUID player2 = UUID.fromString(result.getString("player2"));
                    UUID winner = UUID.fromString(result.getString("winner"));
                    int timeSpendP1 = result.getInt("timeSpendP1");
                    int timeSpendP2 = result.getInt("timeSpendP2");
                    int duration = result.getInt("duration");
                    int id = result.getInt("id");
                    games.add(new GameData(id, player1, player2, winner, timeSpendP1, timeSpendP2, duration));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }
}
