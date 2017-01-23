package ch.swisssmp.elytrarace;

import java.util.HashMap;
import java.util.Map.Entry;
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
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class PlayerRace {
	protected final UUID player_uuid;
	protected long startTime = 0;
	protected long finishTime = 0;
	protected HashMap<String, Long> bestTimes = null;
	protected final HashMap<String, Long> passedCheckpoints = new HashMap<String, Long>();
	protected boolean running = false;
	protected final Random random = new Random();
	
	protected PlayerRace(Player player){
		this.player_uuid = player.getUniqueId();
		Main.races.put(player.getUniqueId(), this);
	}
	protected void start(){
		this.running = true;
		this.startTime = System.currentTimeMillis();
		this.passedCheckpoints.clear();
		Main.sendActionBar(Bukkit.getPlayer(player_uuid), "GO!");
	}
	protected void cancel(){
		this.running = false;
		this.passedCheckpoints.clear();
	}
	protected void finish(){
		if(!this.running) return;
		this.running = false;
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null) return;
		player.setVelocity(new Vector(0, 0, 0));
		if(this.passedCheckpoints.size()<18){
			Main.sendTitle(player, ChatColor.WHITE+"UNGÜLTIG!",ChatColor.RED+"Nicht alle Checkpoints passiert!", 1, 3, 1);
			return;
		}
		this.finishTime = System.currentTimeMillis();
		long totalTime = this.finishTime-this.startTime;
		String bigText;
		String smallText;
		boolean newHighscore = true;
		if(Main.highscores.containsKey(player_uuid)){
			if(totalTime>=Main.highscores.get(player_uuid)){
				newHighscore = false;
			}
		}
		if(newHighscore){
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 100f, 0.7f);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 100f, 0.9f);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 100f, 1.05f);
			firework(player, 4);
			Main.highscores.put(player.getUniqueId(), totalTime);
			this.bestTimes = new HashMap<String, Long>();
			for(Entry<String, Long> entry : passedCheckpoints.entrySet()){
				this.bestTimes.put(entry.getKey(), entry.getValue());
			}
			bigText = "REKORD!";
			smallText = ChatColor.GREEN+formatTime(totalTime);
			player.playEffect(EntityEffect.FIREWORK_EXPLODE);
			player.sendMessage(ChatColor.GREEN+"Neuer persönlicher Rekord! "+formatTime(totalTime));
		}
		else{
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 100f, 0.7f);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 100f, 0.85f);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 100f, 1.05f);
			bigText = "ZIEL!";
			smallText = ChatColor.WHITE+formatTime(totalTime);
			player.playEffect(EntityEffect.VILLAGER_ANGRY);
		}
		Main.sendTitle(player, bigText, smallText, 1, 5, 1);
	}
	protected void passCheckpoint(String checkpoint){
		if(this.passedCheckpoints.containsKey(checkpoint)) return;
		long currentTime = System.currentTimeMillis()-this.startTime;
		this.passedCheckpoints.put(checkpoint, currentTime);
		boolean newRecord = true;
		String messageStart;
		long displayTime = currentTime;
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(bestTimes!=null){
			if(bestTimes.containsKey(checkpoint)){
				displayTime = currentTime - bestTimes.get(checkpoint);
				if(displayTime>0){
					newRecord = false;
				}
			}
		}
		if(newRecord){
			if(bestTimes!=null){
				messageStart = ChatColor.GREEN+"-";
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100f, random.nextFloat()*0.1f+1.1f);
			}
			else{
				messageStart = ChatColor.GRAY+"";
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100f, random.nextFloat()*0.1f+0.95f);
			}
		}
		else{
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100f, random.nextFloat()*0.1f+0.8f);
			messageStart = ChatColor.RED+"+";
		}
		Main.sendActionBar(player, messageStart+formatTime(displayTime));
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
			Bukkit.getScheduler().runTaskLater(Main.plugin, runnable, 5L);
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
