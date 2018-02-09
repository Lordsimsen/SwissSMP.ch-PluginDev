package ch.swisssmp.elytrarace;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class RaceContest {
	private final int contest_id;
	private final String contest_name;
	private final RaceCourse[] courses;
	private boolean running = false;
	private boolean finished = false;
	
	private RaceContest(ConfigurationSection dataSection){
		this.contest_id = dataSection.getInt("contest_id");
		this.contest_name = dataSection.getString("name");
		if(dataSection.contains("courses")){
			List<RaceCourse> coursesList = new ArrayList<RaceCourse>();
			RaceCourse course;
			for(int course_id : dataSection.getIntegerList("courses")){
				course = RaceCourse.get(course_id);
				if(course!=null)coursesList.add(course);
			}
			this.courses = new RaceCourse[coursesList.size()];
			coursesList.toArray(this.courses);
		}
		else this.courses = new RaceCourse[0];
	}
	
	public void start(){
		for(PlayerRace race : ElytraRace.races.values()){
			race.cancel();
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			SwissSMPler.get(player).sendTitle(this.contest_name, "Viel Erfolg!");
		}
		this.running = true;
		this.finished = false;
	}
	
	public void finish(){
		for(PlayerRace race : ElytraRace.races.values()){
			race.cancel();
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			SwissSMPler.get(player).sendTitle("Ende", "Danke für deine Teilnahme!");
			player.sendMessage("[ElytraRace] "+ChatColor.YELLOW+"Der Wettkampf ist beendet!");
			Bukkit.dispatchCommand(player, "rangliste");
		}
		this.running = false;
		this.finished = true;
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
	public boolean isFinished(){
		return this.finished;
	}
	
	public int getContestId(){
		return this.contest_id;
	}
	
	public String getName(){
		return this.contest_name;
	}
	
	public RaceCourse[] getCourses(){
		return this.courses;
	}
	
	public static RaceContest create(CommandSender sender, String contest_name, String[] courses){
		String contest_name_encoded;
		String[] coursesEncoded = new String[courses.length];
		try{
			contest_name_encoded = URLEncoder.encode(contest_name, "utf-8");
			for(int i = 0; i < courses.length; i++){
				coursesEncoded[i] = "courses[]="+URLEncoder.encode(courses[i], "utf-8");
			}
		}
		catch(Exception e){
			return null;
		}
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("elytra_race/contest_create.php", new String[]{
				"name="+contest_name_encoded,
				String.join(",",coursesEncoded)
		});
		if(yamlConfiguration==null){
			sender.sendMessage("[ElytraRace] "+ChatColor.RED+"Etwas ist schiefgelaufen.");
			return null;
		}
		if(yamlConfiguration.contains("contest")){
			return new RaceContest(yamlConfiguration.getConfigurationSection("contest"));
		}
		else if(yamlConfiguration.contains("message")){
			sender.sendMessage(yamlConfiguration.getString("message"));
			return null;
		}
		else{
			sender.sendMessage("[ElytraRace] "+ChatColor.RED+"Konnte den Wettkampf nicht erstellen.");
			return null;
		}
	}
	
	public static RaceContest load(int contest_id){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("elytra_race/contest_load.php", new String[]{
				"contest="+contest_id,
		});
		if(yamlConfiguration.contains("contest")){
			return new RaceContest(yamlConfiguration.getConfigurationSection("contest"));
		}
		return null;
	}
}
