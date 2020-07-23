package ch.swisssmp.zvieriplausch;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataContainer {

    private final ZvieriArena arena;
    private final int LEVELS = 5;

    private File dataFile;
    private YamlConfiguration yamlConfiguration;
    private ConfigurationSection arenaSection;

    private List<List<String>> unlockedPlayers;
    private List<List<String>> highscorePlayers;
    private int[] highscoreScores;
    private HashMap<UUID,int[]> personalHighscores;

    private PlayerDataContainer(ZvieriArena arena){
        this.arena = arena;

        File pluginDirectory = new File(arena.getWorld().getWorldFolder(), "plugindata/ZvieriPlausch");
        dataFile = new File(pluginDirectory, "playerData.yml");

        if(!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }
        if(!dataFile.exists()){
            try {
                dataFile.createNewFile();
            } catch (IOException e){
                Bukkit.getLogger().info(ZvieriPlauschPlugin.getPrefix() + " " + e.getMessage());
                e.printStackTrace();
            }
        }
        yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        if(yamlConfiguration == null) yamlConfiguration = new YamlConfiguration();

        arenaSection = getArenaSection();
        if(arenaSection == null) {
            Bukkit.getLogger().info(ZvieriPlauschPlugin.getPrefix() + " Konnte Spielerdaten nicht laden.");
            return;
        }
        unlockedPlayers = new ArrayList<>();
        highscorePlayers = new ArrayList<>();
        highscoreScores = new int[LEVELS];
        personalHighscores = new HashMap<>();
        readUnlockedPlayers();
        readHighscorePlayers();
        readHighscoreScores();
        readPersonalHighscores();
    }

    public static PlayerDataContainer initialize(ZvieriArena arena){
        return new PlayerDataContainer(arena);
    }

    public List<String> getUnlockedPlayers(int level){
        try {
            return unlockedPlayers.get(level - 2);
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
            int highscore = highscoreScores[level - 1];
            return highscore;
        } catch (IndexOutOfBoundsException e){
            return 0;
        }
    }

    public int getPersonalHighscore(UUID playerId, int level){
        try{
            int[] scores = personalHighscores.get(playerId);
            return scores[level - 1];
        } catch (Exception e){
            return 0;
        }
    }

    private ConfigurationSection getArenaSection(){
        ConfigurationSection arenaSection;
        if (!yamlConfiguration.getKeys(false).contains(arena.getId().toString())) {
            arenaSection = yamlConfiguration.createSection(arena.getId().toString());
        } else{
            arenaSection = yamlConfiguration.getConfigurationSection(arena.getId().toString());
            }
        return arenaSection;
    }

    private void readUnlockedPlayers(){
        ConfigurationSection unlockedLevelsSection = arenaSection.getConfigurationSection("unlockedLevels");
        if (unlockedLevelsSection == null) return;
        for(int i = 1; i < LEVELS; i++) {
            if (unlockedLevelsSection.getStringList("level_" + (i+1)) == null){
                List<String> puffer = new ArrayList<>();
                puffer.add("");
                unlockedPlayers.add(puffer);
                continue;
            }
            List<String> playersList = unlockedLevelsSection.getStringList("level_" + (i+1));
            unlockedPlayers.add(playersList);
        }
    }

    private void readHighscoreScores(){
        for(int i = 1; i <= LEVELS; i++) {
            int highscore;
            try {
                highscore = arenaSection.getConfigurationSection("highscores").getConfigurationSection("level_" + i).getInt("score");
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
                players = arenaSection.getConfigurationSection("highscores").getConfigurationSection("level_" + (i)).getStringList("players");
            } catch (NullPointerException e) {
                players.add("");
            }
            highscorePlayers.add(players);
        }
    }

    private void readPersonalHighscores(){
        ConfigurationSection personalHighscoresSection;
        try {
            personalHighscoresSection = arenaSection.getConfigurationSection("highscores").getConfigurationSection("personalHighscores");
        } catch (NullPointerException e){
            return;
        }
        if(personalHighscoresSection == null) return;
        for(String key : personalHighscoresSection.getKeys(false)){
            ConfigurationSection playerSection = personalHighscoresSection.getConfigurationSection(key);
            int[] scores = new int[LEVELS];
            for(int i = 1; i <= LEVELS; i++){
                try {
                    scores[i - 1] = playerSection.getInt("level_" + i);
                } catch (NullPointerException e){
                    continue;
                }
            }
            personalHighscores.put(UUID.fromString(key), scores);
        }
    }

    public void updateLevelUnlocks(int level, List<String> playerIds){
        try {
            for (String id : playerIds) {
                if (!unlockedPlayers.get(level-1).contains(id)) unlockedPlayers.get(level-1).add(id);
            }
        } catch (IndexOutOfBoundsException e){
            unlockedPlayers.add(playerIds);
        }
    }

    public void updateHighscore(int level, int score, List<Player> participants) {
        List<String> participantsStrings = new ArrayList<>();
        for(Player player : participants){
            participantsStrings.add(player.getName());
        }
        try {
            highscorePlayers.remove(level - 1);
        } catch (IndexOutOfBoundsException e){}
        try {
            highscorePlayers.add(level - 1, participantsStrings);
        } catch (IndexOutOfBoundsException e){
            highscorePlayers.add(participantsStrings);
        }
        highscoreScores[level-1] = score;
    }

    public void updatePersonalHighscore(int level, int score, UUID playerId){
        int[] highscores;
        if(!personalHighscores.containsKey(playerId)){
            highscores = new int[LEVELS];
            highscores[level - 1] = score;
            personalHighscores.put(playerId, highscores);
            return;
        }
        highscores = personalHighscores.get(playerId);
        if(score > highscores[level - 1]) highscores[level - 1] = score;
    }

    public void resetHighscores(){
        for(int i = 0; i < LEVELS; i++){
            updateHighscore((i+1), 0, new ArrayList<Player>());
        }
        save();
    }

    protected void save(){
        ConfigurationSection unlockedLevelsSection = arenaSection.getConfigurationSection("unlockedLevels");
        if(unlockedLevelsSection == null){
            unlockedLevelsSection = arenaSection.createSection("unlockedLevels");
        }
        for(int i = 0; i < unlockedPlayers.size(); i++) {
            unlockedLevelsSection.set("level_" + (i+2), unlockedPlayers.get(i));
        }

        ConfigurationSection highscoreSection = arenaSection.getConfigurationSection("highscores");
        if(highscoreSection == null){
            highscoreSection = arenaSection.createSection("highscores");
        }
        for(int i = 0; i < highscorePlayers.size(); i++){
            ConfigurationSection levelSection = highscoreSection.getConfigurationSection("level_" + (i+1));
            if(levelSection == null) {
                levelSection = highscoreSection.createSection("level_" + (i + 1));
            }
            try {
                levelSection.set("score", highscoreScores[i]);
                levelSection.set("players", highscorePlayers.get(i));
            } catch (IndexOutOfBoundsException e){
                continue;
            }
        }
        ConfigurationSection personalHighscoresSection = highscoreSection.getConfigurationSection("personalHighscores");
        if(personalHighscoresSection == null) {
            personalHighscoresSection = highscoreSection.createSection("personalHighscores");
        }
        for(UUID key : personalHighscores.keySet()){
//                ConfigurationSection playerSection;
//                playerSection = personalHighscoresSection.getConfigurationSection(key.toString());
//                if(playerSection == null)
            ConfigurationSection playerSection = personalHighscoresSection.createSection(key.toString());
            int[] scores = personalHighscores.get(key);
            for(int i = 0; i < scores.length; i++){
                playerSection.set("level_" + (i + 1), scores[i]);
            }
        }
        yamlConfiguration.save(dataFile);
    }
}
