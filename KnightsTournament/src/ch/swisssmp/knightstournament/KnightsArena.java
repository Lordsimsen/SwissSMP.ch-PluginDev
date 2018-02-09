package ch.swisssmp.knightstournament;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.bukkit.Location;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class KnightsArena {
	private static HashMap<String,KnightsArena> loadedArenas = new HashMap<String,KnightsArena>();
	private final String name;
	private final Location posOne;
	private final Location center;
	private final Location posTwo;
	private Tournament tournament;
	private final String beginSound;
	private final String callSound;
	private final String endSound;
	
	private KnightsArena(String name, Location posOne, Location center, Location posTwo, String beginSound, String callSound, String endSound){
		this.name = name;
		this.posOne = posOne;
		this.center = center;
		this.posTwo = posTwo;
		this.beginSound = beginSound;
		this.callSound = callSound;
		this.endSound = endSound;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Location getPosOne(){
		return this.posOne;
	}
	
	public Location getPosTwo(){
		return this.posTwo;
	}
	
	public void playBeginSound(){
		this.center.getWorld().playSound(center, this.beginSound, 50, 1);
	}
	
	public void playCallSound(){
		this.center.getWorld().playSound(center, this.callSound, 50, 1);
	}
	
	public void playEndSound(){
		this.center.getWorld().playSound(center, this.endSound, 50, 1);
	}
	
	protected void runTournament(Tournament tournament){
		this.tournament = tournament;
	}
	
	public Tournament getTournament(){
		return this.tournament;
	}
	
	public static KnightsArena load(String name){
		try {
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("knights_tournament/arena.php", new String[]{
				"arena="+URLEncoder.encode(name, "utf-8")	
			});
			if(yamlConfiguration.contains("arena")){
				ConfigurationSection arenaSection = yamlConfiguration.getConfigurationSection("arena");
				Location posOne = arenaSection.getLocation("pos_1");
				Location center = arenaSection.getLocation("center");
				Location posTwo = arenaSection.getLocation("pos_2");
				String beginSound = arenaSection.getString("begin_sound");
				String callSound = arenaSection.getString("call_sound");
				String endSound = arenaSection.getString("end_sound");
				if(posOne!=null && center!=null && posTwo!=null){
					KnightsArena result = new KnightsArena(name, posOne, center, posTwo, beginSound, callSound, endSound);
					KnightsArena.loadedArenas.put(name.toLowerCase(), result);
					return result;
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static KnightsArena get(String name){
		if(loadedArenas.containsKey(name.toLowerCase()))
			return loadedArenas.get(name.toLowerCase());
		else
			return KnightsArena.load(name.toLowerCase());
	}
}
