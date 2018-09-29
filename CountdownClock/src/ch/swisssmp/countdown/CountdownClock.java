package ch.swisssmp.countdown;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class CountdownClock implements Runnable {
	private static HashMap<String, CountdownClock> clocks = new HashMap<String, CountdownClock>();
	
	private final String name;
	private final Block position;
	private final BlockFace direction;
	private final Material numberMaterial;
	private final Material gapMaterial;
	private final long deadline;
	
	private boolean paused = false;
	
	private BukkitTask task;
	
	private CountdownClock(World world, ConfigurationSection dataSection){
		this.name = dataSection.getString("name");
		this.position = world.getBlockAt(dataSection.getInt("x"), dataSection.getInt("y"), dataSection.getInt("z"));
		this.direction = BlockFace.valueOf(dataSection.getString("direction"));
		this.numberMaterial = Material.valueOf(dataSection.getString("numbers"));
		this.gapMaterial = Material.valueOf(dataSection.getString("gaps"));
		this.deadline = dataSection.getLong("deadline");
	}
	
	private CountdownClock(String name, Block position, BlockFace direction, Material numberMaterial, Material gapMaterial, long deadline){
		this.name = name;
		this.position = position;
		this.direction = direction;
		this.numberMaterial = numberMaterial;
		this.gapMaterial = gapMaterial;
		this.deadline = deadline;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Block getPosition(){
		return this.position;
	}
	
	public long getDeadline(){
		return this.deadline;
	}
	
	@Override
	public void run() {
		if(this.paused) return;
		long remaining = deadline-System.currentTimeMillis();
		NumberDisplay.buildTime(this.position, this.direction, this.numberMaterial, this.gapMaterial, remaining);
		if(remaining<=0){
			this.finish();
		}
	}
	
	private void cancel(){
		this.task.cancel();
		clocks.remove(this.name.toLowerCase());
	}
	
	protected void pause(){
		this.paused = true;
	}
	
	protected void resume(){
		this.paused = false;
	}
	
	protected void stop(){
		this.cancel();
		CountdownClock.saveAll(this.position.getWorld());
	}
	
	private void finish(){
		this.cancel();
		Bukkit.getPluginManager().callEvent(new CountdownFinishEvent(this));
		CountdownClock.saveAll(this.position.getWorld());
	}
	
	private void save(ConfigurationSection dataSection){
		dataSection.set("name", this.name);
		dataSection.set("x", this.position.getX());
		dataSection.set("y", this.position.getY());
		dataSection.set("z", this.position.getZ());
		dataSection.set("direction", this.direction.toString());
		dataSection.set("numbers", this.numberMaterial.toString());
		dataSection.set("gaps", this.gapMaterial.toString());
		dataSection.set("deadline", this.deadline);
	}
	
	private static void saveAll(World world){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection clocksSection = yamlConfiguration.createSection("clocks");
		ConfigurationSection clockSection;
		int index = 0;
		for(CountdownClock clock : clocks.values()){
			if(clock.position.getWorld()!=world) continue;
			clockSection = clocksSection.createSection("clock_"+index);
			clock.save(clockSection);
			index++;
		}
		File clocksFile = CountdownClock.getCountdownClocksFile(world);
		if(!clocksFile.getParentFile().exists()){
			clocksFile.getParentFile().mkdirs();
		}
		yamlConfiguration.save(clocksFile);
	}
	
	protected static void loadAll(){
		for(World world : Bukkit.getWorlds()){
			CountdownClock.loadAll(world);
		}
	}
	
	protected static Collection<CountdownClock> getAll(World world){
		Collection<CountdownClock> result = new ArrayList<CountdownClock>();
		for(CountdownClock clock : clocks.values()){
			if(clock.position.getWorld()!=world) continue;
			result.add(clock);
		}
		return result;
	}
	
	protected static void loadAll(World world){
		CountdownClock.cancelAll(world);
		File clocksFile = CountdownClock.getCountdownClocksFile(world);
		if(!clocksFile.exists()) return;
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(clocksFile);
		if(yamlConfiguration==null || !yamlConfiguration.contains("clocks")) return;
		ConfigurationSection clocksSection = yamlConfiguration.getConfigurationSection("clocks");
		ConfigurationSection clockSection;
		CountdownClock clock;
		for(String key : clocksSection.getKeys(false)){
			clockSection = clocksSection.getConfigurationSection(key);
			try{
				clock = new CountdownClock(world, clockSection);
			}
			catch(Exception e){
				Bukkit.getLogger().info("[CountdownClock] Countdown '"+clockSection.getString("name")+"' hat eine ungültige Konfiguration!");
				e.printStackTrace();
				continue;
			}
			clock.task = Bukkit.getScheduler().runTaskTimer(CountdownClockPlugin.plugin, clock, 0, 20);
			clocks.put(clock.getName().toLowerCase(), clock);
		}
	}
	
	protected static void cancelAll(World world){
		for(CountdownClock clock : new ArrayList<CountdownClock>(clocks.values())){
			if(clock.position.getWorld()==world) clock.cancel();
		}
	}
	
	protected static File getCountdownClocksFile(World world){
		return new File(world.getWorldFolder(), "plugindata/countdown_clocks.yml");
	}
	
	public static CountdownClock get(String name){
		return clocks.get(name.toLowerCase());
	}
	
	public static CountdownClock run(String name, Block position, BlockFace direction, Material numberMaterial, Material gapMaterial, long deadline){
		CountdownClock result = new CountdownClock(name, position,direction, numberMaterial, gapMaterial, deadline);
		result.task = Bukkit.getScheduler().runTaskTimer(CountdownClockPlugin.plugin, result, 0, 20);
		clocks.put(result.getName().toLowerCase(), result);
		CountdownClock.saveAll(position.getWorld());
		return result;
	}
}
