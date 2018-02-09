package ch.swisssmp.elytrarace;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PlayerRace {
	protected final UUID player_uuid;
	protected final int course_id;
	protected final int contest_id;
	protected int checkpointCount;
	protected long startTime = 0;
	protected long finishTime = 0;
	protected long actionBarDisabledUntil = 0;
	protected HashMap<String, Long> bestTimes = null;
	protected final HashMap<String, Long> passedCheckpoints = new HashMap<String, Long>();
	protected boolean running = false;
	protected final Random random = new Random();
	
	private String currentSoundtrack = null;
	
	protected PlayerRace(int course_id, int contest_id, Player player){
		this.player_uuid = player.getUniqueId();
		this.course_id = course_id;
		this.contest_id = contest_id;
		this.loadBestTimes();
		ElytraRace.races.put(player.getUniqueId(), this);
	}
	private void loadBestTimes(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("elytra_race/best_times.php", new String[]{
			"player="+player_uuid.toString(),
			"contest="+contest_id,
			"course="+course_id
		});
		bestTimes = new HashMap<String,Long>();
		if(yamlConfiguration==null || !yamlConfiguration.contains("times")) return;
		else{
			ConfigurationSection timesSection = yamlConfiguration.getConfigurationSection("times");
			for(String key : timesSection.getKeys(false)){
				bestTimes.put(key, timesSection.getLong(key));
			}
		}
	}
	protected void start(){
		this.checkpointCount = RaceCourse.get(this.course_id).getCheckpointCount();
		this.running = true;
		this.startTime = System.currentTimeMillis();
		this.passedCheckpoints.clear();
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null) return;
		SwissSMPler.get(player).sendActionBar(ElytraRace.getContestName());
		actionBarDisabledUntil=System.currentTimeMillis()+3000;
		if(currentSoundtrack!=null){
			player.stopSound(currentSoundtrack);
		}
		RaceCourse raceCourse = RaceCourse.get(this.course_id);
		if(raceCourse==null)return;
		player.stopSound(raceCourse.getSoundtrack());
		this.currentSoundtrack = raceCourse.getSoundtrack();
		player.playSound(player.getLocation(), raceCourse.getSoundtrack(), SoundCategory.RECORDS, 2000f, 1f);
	}
	protected void cancel(){
		this.running = false;
		this.passedCheckpoints.clear();
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player!=null){
			if(currentSoundtrack!=null){
				player.stopSound(this.currentSoundtrack);
				this.currentSoundtrack = null;
			}
			player.setVelocity(new Vector(0, 0, 0));
			player.setFallDistance(0);
			player.teleport(player.getWorld().getSpawnLocation().add(0,0.5,0));
		}
	}
	protected void finish(){
		if(!this.running) return;
		this.running = false;
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null) return;
		if(currentSoundtrack!=null){
			player.stopSound(this.currentSoundtrack);
			this.currentSoundtrack = null;
		}
		player.setVelocity(new Vector(0, 0, 0));
		this.finishTime = System.currentTimeMillis();
		long totalTime = this.finishTime-this.startTime;
		String[] checkpointStrings = new String[passedCheckpoints.size()];
		String[] checkpointKeys = new String[passedCheckpoints.size()];
		passedCheckpoints.keySet().toArray(checkpointKeys);
		boolean validRace = this.passedCheckpoints.size()>=this.checkpointCount;
		for(int i = 0; i < passedCheckpoints.size(); i++){
			checkpointStrings[i] = "checkpoints["+checkpointKeys[i]+"]="+passedCheckpoints.get(checkpointKeys[i]);
		}
		String checkpointsData = String.join("&", checkpointStrings);
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("elytra_race/record.php", new String[]{
				"player="+player_uuid.toString(),
				"contest="+this.contest_id,
				"course="+this.course_id,
				"time="+totalTime,
				"valid="+(validRace ? "true" : "false"),
				checkpointsData
			});
		if(!validRace){
			SwissSMPler.get(player).sendTitle(ChatColor.WHITE+"UNGÜLTIG!",ChatColor.RED+"Nicht alle Checkpoints passiert!");
			return;
		}
		if(yamlConfiguration==null) return;
		boolean isContestHighscore = yamlConfiguration.getBoolean("is_contest_highscore");
		boolean isCourseHighscore = yamlConfiguration.getBoolean("is_course_highscore");
		boolean isPersonalHighscore = yamlConfiguration.getBoolean("is_personal_highscore");
		if(yamlConfiguration.contains("sound")){
			player.playSound(player.getLocation(), yamlConfiguration.getString("sound"), SoundCategory.RECORDS, 500f,1f);
		}
		String bigText;
		String smallText;
		if(isCourseHighscore || isContestHighscore || isPersonalHighscore){
			firework(player, 4);
			bigText = "GLÜCKWUNSCH!";
			smallText = ChatColor.GREEN+formatTime(totalTime);
			player.playEffect(EntityEffect.FIREWORK_EXPLODE);
			if(isCourseHighscore){
				player.sendMessage("[ElytraRace] "+ChatColor.LIGHT_PURPLE+"Neuer Streckenrekord! "+formatTime(totalTime));
				for(Player other : player.getWorld().getPlayers()){
					if(other==player) continue;
					other.sendMessage("[ElytraRace] "+player.getDisplayName()+ChatColor.RESET+ChatColor.LIGHT_PURPLE+" hat mit "+ChatColor.YELLOW+formatTime(totalTime)+ChatColor.LIGHT_PURPLE+" den Streckenrekord geschlagen!");
				}
			}
			else if(isContestHighscore){
				player.sendMessage("[ElytraRace] "+ChatColor.GREEN+"Neue Bestzeit! "+formatTime(totalTime));
				for(Player other : player.getWorld().getPlayers()){
					if(other==player) continue;
					other.sendMessage("[ElytraRace] "+player.getDisplayName()+ChatColor.RESET+ChatColor.YELLOW+" hat mit "+formatTime(totalTime)+" die neue Bestzeit im Wettkampf!");
				}
			}
			if(isPersonalHighscore)
				player.sendMessage(ChatColor.GREEN+"Neue persönliche Bestzeit! "+formatTime(totalTime));
		}
		else{
			bigText = "ZIEL!";
			smallText = ChatColor.WHITE+formatTime(totalTime);
			player.playEffect(EntityEffect.VILLAGER_ANGRY);
		}
		SwissSMPler.get(player).sendTitle(bigText, smallText);
		this.loadBestTimes();
	}
	protected void passCheckpoint(String checkpoint){
		if(this.passedCheckpoints.containsKey(checkpoint)) return;
		long currentTime = System.currentTimeMillis()-this.startTime;
		this.passedCheckpoints.put(checkpoint, currentTime);
		boolean newRecord = true;
		boolean newTime = true;
		String messageStart;
		long displayTime = currentTime;
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(bestTimes!=null){
			if(bestTimes.containsKey(checkpoint)){
				newTime = false;
				displayTime = currentTime - bestTimes.get(checkpoint);
				if(displayTime>0){
					newRecord = false;
				}
			}
		}
		if(newRecord){
			if(!newTime){
				messageStart = ChatColor.GREEN+"-";
				if(player!=null)
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 100f, random.nextFloat()*0.1f+1.1f);
			}
			else{
				messageStart = ChatColor.GRAY+"";
				if(player!=null)
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 100f, random.nextFloat()*0.1f+0.95f);
			}
		}
		else{
			if(player!=null)
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 100f, random.nextFloat()*0.1f+0.8f);
			messageStart = ChatColor.RED+"+";
		}
		if(player!=null && actionBarDisabledUntil<System.currentTimeMillis()){
			SwissSMPler.get(player).sendActionBar(messageStart+formatTime(displayTime));
		}
	}
	private void firework(Player player, int recursion){
		World world = player.getWorld();
		Color[] colors = new Color[]{
				Color.RED,
				Color.WHITE,
				Color.AQUA,
				Color.SILVER
		};
		Type[] types = new Type[]{
				Type.BALL,
				Type.BALL_LARGE,
				Type.BURST,
				Type.CREEPER,
				Type.STAR
		};
		Random random = new Random();
		for(int i = 0; i < 2; i++){
			Firework firework = (Firework)world.spawnEntity(player.getLocation(), EntityType.FIREWORK);
			FireworkMeta fireworkMeta = firework.getFireworkMeta();
			Builder builder = FireworkEffect.builder();
			builder.trail(random.nextBoolean());
			builder.withFade(colors[random.nextInt(colors.length)]);
			builder.with(types[random.nextInt(colors.length)]);
			builder.withColor(colors[random.nextInt(colors.length)]);
			fireworkMeta.addEffect(builder.build());
			firework.setFireworkMeta(fireworkMeta);
		}
		if(recursion-1>0){
			Runnable runnable = new Runnable(){
				public void run(){
					firework(player, recursion-1);
				}
			};
			Bukkit.getScheduler().runTaskLater(ElytraRace.plugin, runnable, 5L);
		}
	}
	protected static String formatTime(long time){
		time = Math.abs(time);
		int totalHundreds = (int) Math.floor(time/10);
	    int totalSeconds = (int) Math.floor(totalHundreds/100);
	    int totalMinutes = (int) Math.floor(totalSeconds/60);
	    int minutes = totalMinutes;
	    int seconds = totalSeconds-minutes*60;
	    int hundreds = (int)totalHundreds-totalSeconds*100;
	    return String.format("%02d", minutes)+":"+String.format("%02d", seconds)+":"+String.format("%02d", hundreds);
	}
}
