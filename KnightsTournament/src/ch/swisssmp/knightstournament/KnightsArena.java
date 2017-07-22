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
	private final Location posTwo;
	private Tournament tournament;
	
	public KnightsArena(String name, Location posOne, Location posTwo){
		this.name = name;
		this.posOne = posOne;
		this.posTwo = posTwo;
		loadedArenas.put(this.name.toLowerCase(), this);
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
				Location posTwo = arenaSection.getLocation("pos_2");
				if(posOne!=null && posTwo!=null) return new KnightsArena(name, posOne, posTwo);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static KnightsArena get(String name){
		if(loadedArenas.containsKey(name.toLowerCase()))
			return loadedArenas.get(name);
		else
			return load(name);
	}
}
