package org.plugin2.mcplugin2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    UUID uuid;
    int wins;
    int losses;
    int winStreak;
    int allTimeWinStreak;
    double accuracy;
    int rank;
    public PlayerData(UUID uuid, int wins, int losses, int winStreak, int allTimeWinStreak, double accuracy, int rank) {
        this.wins = wins;
        this.losses = losses;
        this.winStreak = winStreak;
        this.allTimeWinStreak = allTimeWinStreak;
        this.accuracy = accuracy;
        this.uuid = uuid;
        this.rank = rank;
    }
    public void addWin(){
        wins ++;
        winStreak ++;
        if (winStreak > allTimeWinStreak){
            allTimeWinStreak = winStreak;
        }
    }
    public void addLoss(){
        losses ++;
        winStreak = 0;
    }
    public UUID getUUID() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getAllTimeWinStreak() {
        return allTimeWinStreak;
    }

    public double getAccuracy() {
        return accuracy;
    }
    public int getRank() { return  rank; }
}
