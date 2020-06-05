package ch.swisssmp.zvierigame;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final ZvieriArena arena;
    private final int LEVELS = 5;
    private ConfigurationSection arenaSection;

    private List<List<String>> unlockedPlayers;
    private List<List<String>> highscorePlayers;
    private int[] highscoreScores;

    private PlayerData(ZvieriArena arena){
        this.arena = arena;
        this.arenaSection = getArenaSection();
        readUnlockedPlayers();
        readHighscorePlayers();
        readHighscoreScores();
    }

    public static PlayerData initialize(ZvieriArena arena){
        return new PlayerData(arena);
    }

    public List<String> getUnlockedPlayers(int level){
        return unlockedPlayers.get(level-1);
    }

    public List<String> getHighscorePlayers(int level){
        return highscorePlayers.get(level-1);
    }

    public int getHighscoreScore(int level){
        return highscoreScores[level-1];
    }

    private ConfigurationSection getArenaSection(){
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(arena.getWorld().getWorldFolder(), "plugindata/ZvieriGame/arenen.yml"));
        ConfigurationSection arenaSection = null;
        if (yamlConfiguration.contains("arenen")) {
            ConfigurationSection arenenSection = yamlConfiguration.getConfigurationSection("arenen");
            for (String key : arenenSection.getKeys(false)) {
                arenaSection = arenenSection.getConfigurationSection(key);
                if (UUID.fromString(arenaSection.getString("id")).equals(arena.getId())) {
                    return arenaSection;
                }
            }
        }
        return null;
    }

    private void readUnlockedPlayers(){
        ConfigurationSection unlockedLevelsSection = arenaSection.getConfigurationSection("unlockedLevels");
        if (unlockedLevelsSection == null) return;
        for(int i = 1; i < LEVELS; i++) {
            if (unlockedLevelsSection.get("level_" + i) == null) continue;
            List<String> playersList = unlockedLevelsSection.getStringList("level_" + i);
            unlockedPlayers.add(playersList);
        }
    }

    private void readHighscoreScores(){
        for(int i = 1; i <= LEVELS; i++) {
            int highscore = 0;
            try {
                highscore = arenaSection.getConfigurationSection("highscores").getConfigurationSection("level_" + i).getInt("highscore");
            } catch (NullPointerException e) {
                highscore = 0;
            }
            highscoreScores[i-1] = highscore;
        }
    }

    private void readHighscorePlayers() {
        for(int i = 1; i <= LEVELS; i++) {
            List<String> players = new ArrayList<String>();
            try {
                ConfigurationSection playersSection = arenaSection.getConfigurationSection("highscores")
                        .getConfigurationSection("level_" + (i)).getConfigurationSection("players");
                int j = 1;
                while (playersSection.get("player_" + j) != null) {
                    players.add(playersSection.getString("player_" + j));
                    j++;
                }
            } catch (NullPointerException e) {
                players.add("");
            }
            highscorePlayers.add(players);
        }
    }
}
