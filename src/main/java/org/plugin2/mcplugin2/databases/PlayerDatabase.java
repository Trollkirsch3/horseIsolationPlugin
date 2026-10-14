package org.plugin2.mcplugin2.databases;

import org.jetbrains.annotations.NotNull;
import org.plugin2.mcplugin2.GameData;
import org.plugin2.mcplugin2.PlayerData;
import org.plugin2.mcplugin2.leaderboard.Leaderboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDatabase {

    private final DatabaseManager database;

    public PlayerDatabase(DatabaseManager database) {
        this.database = database;
    }

    public PlayerData createPlayer(@NotNull UUID player) {

        String sql = """
                INSERT OR IGNORE INTO players (uuid)
                VALUES (?)
                """;

        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(sql)) {

            statement.setString(1, player.toString());
            statement.executeUpdate();
            return  loadPlayer(player);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    ArrayList<GameData> gameHistory;
    double accuracy;
    public void savePlayer(@NotNull PlayerData data) {

        String sql = """
            UPDATE players
            SET wins = ?,
                losses = ?,
                winStreak = ?,
                allTimeWinStreak = ?,
                accuracy = ?
            WHERE uuid = ?
            """;

        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(sql)) {

            statement.setInt(1, data.getWins());
            statement.setInt(2, data.getLosses());
            statement.setInt(3, data.getWinStreak());
            statement.setInt(4, data.getAllTimeWinStreak());
            statement.setDouble(5, data.getAccuracy());

            statement.setString(
                    6,
                    data.getUUID().toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData loadPlayer(@NotNull UUID player) {

        String sql = """
                SELECT *
                FROM players
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(sql)) {

            statement.setString(1, player.toString());

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    int wins = result.getInt("wins");
                    int losses = result.getInt("losses");
                    int winStreak = result.getInt("winStreak");
                    int allTimeWinStreak = result.getInt("allTimeWinStreak");
                    double accuracy = result.getDouble("accuracy");

                    return new PlayerData(
                            player,
                            wins,
                            losses,
                            winStreak,
                            allTimeWinStreak,
                            accuracy,
                            getPlayerRank(player)
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public List<PlayerData> applySQL(String sql, boolean calcRank){
        List<PlayerData> filteredPlayers = new ArrayList<>();
        try (PreparedStatement statement = database.getConnection().prepareStatement(sql)) {

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    int wins = result.getInt("wins");
                    int losses = result.getInt("losses");
                    int winStreak = result.getInt("winStreak");
                    int allTimeWinStreak = result.getInt("allTimeWinStreak");
                    double accuracy = result.getDouble("accuracy");
                    UUID player = UUID.fromString(result.getString("uuid"));
                    int rank = -1;
                    if (calcRank){
                        rank = getPlayerRank(player);
                    }
                    filteredPlayers.add(new PlayerData(
                            player,
                            wins,
                            losses,
                            winStreak,
                            allTimeWinStreak,
                            accuracy,
                            rank
                            ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredPlayers;
    }
    public List<PlayerData> getLeaderboardPlayers(int page) {

        List<PlayerData> players = new ArrayList<>();

        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * Leaderboard.playersPerPage;

        String sql = """
            SELECT
                uuid,
                wins,
                losses,
                winStreak,
                allTimeWinStreak,
                accuracy,
                RANK() OVER (ORDER BY wins DESC) AS rank
            FROM players
            ORDER BY wins DESC, uuid ASC
            LIMIT ?
            OFFSET ?
            """;

        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(sql)) {

            statement.setInt(1, Leaderboard.playersPerPage);
            statement.setInt(2, offset);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    players.add(new PlayerData(
                            UUID.fromString(result.getString("uuid")),
                            result.getInt("wins"),
                            result.getInt("losses"),
                            result.getInt("winStreak"),
                            result.getInt("allTimeWinStreak"),
                            result.getDouble("accuracy"),
                            result.getInt("rank")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }
    public int getPlayerRank(UUID uuid) {

        String sql = """
            SELECT COUNT(*) + 1 AS position
            FROM players
            WHERE wins > (
                SELECT wins
                FROM players
                WHERE uuid = ?
            )
            """;

        try (PreparedStatement statement =
                     database.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("position");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
