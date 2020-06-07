package ch.swisssmp.zvierigame;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataContainer {

    private final ZvieriArena arena;
    private final int LEVELS = 5;
    private ConfigurationSection arenaSection;

    private List<List<String>> unlockedPlayers;
    private List<List<String>> highscorePlayers;
    private int[] highscoreScores;

    private PlayerDataContainer(ZvieriArena arena){
        this.arena = arena;
        this.arenaSection = getArenaSection();
        unlockedPlayers = new ArrayList<>();
        highscorePlayers = new ArrayList<>();
        highscoreScores = new int[LEVELS];
        readUnlockedPlayers();
        readHighscorePlayers();
        readHighscoreScores();
    }

    public static PlayerDataContainer initialize(ZvieriArena arena){
        return new PlayerDataContainer(arena);
    }

    public List<String> getUnlockedPlayers(int level){
        try {
            return unlockedPlayers.get(level - 1);
        } catch (IndexOutOfBoundsException e){
            return new ArrayList<String>();
        }
    }

    public List<String> getHighscorePlayers(int level){
        try {
            return highscorePlayers.get(level - 1);
        } catch (IndexOutOfBoundsException e){
            return new ArrayList<String>();
        }
    }

    public int getHighscoreScore(int level){
        try {
            return highscoreScores[level - 1];
        } catch (IndexOutOfBoundsException e){
            return 0;
        }
    }

    public void reloadHighscores(int level){
        readHighscorePlayers(level);
        readHighscoreScore(level);
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

    public void reloadUnlockedPlayers(int level){
        ConfigurationSection unlockedLevelsSection = arenaSection.getConfigurationSection("unlockedLevels");
        if (unlockedLevelsSection == null) return;
        if (unlockedLevelsSection.get("level_" + level) == null) {
            Bukkit.getLogger().info(ZvieriGamePlugin.getInstance() + " Konnte levelunlocks nicht updaten");
            return;
        }
        List<String> playersList = unlockedLevelsSection.getStringList("level_" + level);
        unlockedPlayers.set(level-1, playersList);
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

    private void readHighscoreScore(int level){
        try{
            highscoreScores[level-1] = arenaSection.getConfigurationSection("highscores").getConfigurationSection("level_" + level).getInt("highscore");
        } catch (NullPointerException e){
            highscoreScores[level-1] = 0;
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

    private void readHighscorePlayers(int level){
        List<String> players = new ArrayList<String>();
        try {
            ConfigurationSection playersSection = arenaSection.getConfigurationSection("highscores")
                    .getConfigurationSection("level_" + (level)).getConfigurationSection("players");
            int j = 1;
            while (playersSection.get("player_" + j) != null) {
                players.add(playersSection.getString("player_" + j));
                j++;
            }
        } catch (NullPointerException e) {
            players.add("");
        }
        highscorePlayers.set(level-1, players);
    }
}
