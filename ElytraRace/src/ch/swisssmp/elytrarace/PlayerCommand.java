package ch.swisssmp.elytrarace;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import eu.crushedpixel.camerastudio.CameraStudio;

import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player) player = (Player) sender;
		switch(label){
			case "courses":
			case "strecken":{
				if(args.length<=0)return false;
				switch(args[0]){
				case "aktualisieren":
				case "reload":{
					RaceCourse.loadCourses();
					sender.sendMessage("[ElytraRace] Strecken neugeladen.");
					return true;
				}
				case "auflisten":
				case "list":{
					sender.sendMessage("[ElytraRace] Aktuell geladene Strecken:");
					for(RaceCourse raceCourse : RaceCourse.getCourses()){
						sender.sendMessage(" - ("+raceCourse.getCourseId()+") "+raceCourse.getName());
					}
					return true;
				}
				default: return false;
				}
			}
			case "contest":
			case "wettkampf":
			{
				if(args.length<1) return false;
				switch(args[0]){
					case "erstellen":
					case "create":{
						if(args.length<3) return false;
						String contest_name = args[1];
						String[] courses = args[2].split(",");
						RaceContest raceContest = RaceContest.create(sender, contest_name, courses);
						if(raceContest==null){
							sender.sendMessage("[ElytraRace] "+ChatColor.RED+"Konnte den Wettkampf nicht erstellen.");
							return true;
						}
						else{
							sender.sendMessage("[ElytraRace] "+ChatColor.GREEN+"Wettbewerb '"+raceContest.getName()+ChatColor.RESET+"' erstellt.");
							ElytraRace.currentContest = raceContest;
							return true;
						}
					}
					case "laden":
					case "load":{
						if(args.length<2) return false;
						int contest_id = Integer.parseInt(args[1]);
						RaceContest raceContest = RaceContest.load(contest_id);
						if(raceContest==null){
							sender.sendMessage("[ElytraRace] "+ChatColor.RED+"Konnte den Wettkampf nicht laden.");
							return true;
						}
						else{
							sender.sendMessage("[ElytraRace] "+ChatColor.GREEN+"Wettbewerb '"+raceContest.getName()+ChatColor.RESET+ChatColor.GREEN+"' geladen.");
							ElytraRace.currentContest = raceContest;
							return true;
						}
					}
					case "starten":
					case "start":{
						if(ElytraRace.currentContest!=null)ElytraRace.currentContest.start();
						else{
							sender.sendMessage("[ElytraRace] Es ist kein Wettkampf geladen.");
						}
						return true;
					}
					case "beenden":
					case "finish":{
						if(ElytraRace.currentContest!=null)ElytraRace.currentContest.finish();
						else{
							sender.sendMessage("[ElytraRace] Es ist kein Wettkampf geladen.");
						}
						return true;
					}
					default: return false;
				}
			}
			case "spielen":
			case "mitmachen":
			case "go":
			case "join":
			case "play":{
				if(args.length>0 && (!(sender instanceof Player) || sender.hasPermission("elytrarace.admin"))){
					player = Bukkit.getPlayer(args[0]);
					if(player==null){
						sender.sendMessage("[ElytraRace] Spieler '"+args[0]+"' nicht gefunden.");
						return true;
					}
				}
				else if(player==null){
					return false;
				}
				CameraStudio.stop(player.getUniqueId());
				ElytraRace.preparePlayerPlay(player);
				break;
			}
			case "zuschauen":
			case "spectate":
			{
				if(player==null){
					sender.sendMessage("[ElytraRace] Befehl kann nur ingame verwendet werden.");
					return true;
				}
				CameraStudio.stop(player.getUniqueId());
				PlayerRace race = ElytraRace.races.get(player.getUniqueId());
				if(race!=null) race.cancel();
				ElytraRace.preparePlayerSpectate(player);
				break;
			}
			case "rangliste":
			case "ranking":
			{
				if(player==null){
					sender.sendMessage("[ElytraRace] Befehl kann nur ingame verwendet werden. Ranglisten Daten sind auch im Web-Interface einsehbar.");
					return true;
				}
				RaceCourse raceCourse = RaceCourse.get(player.getWorld());
				int course_id = (raceCourse!=null)?raceCourse.getCourseId():0;
				YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("elytra_race/ranking.php", new String[]{
					"contest="+(ElytraRace.currentContest!=null?ElytraRace.currentContest.getContestId():0),
					"course="+course_id,
					"player="+player.getUniqueId()
				});
				if(yamlConfiguration==null || !yamlConfiguration.contains("message")){
					sender.sendMessage("[ElytraRace] Konnte keine Ranglistendaten abrufen.");
					return true;
				}
				for(String line : yamlConfiguration.getStringList("message")){
					sender.sendMessage(line);
				}
				break;
			}
			case "verlassen":
			case "leave":
			case "exit":
			case "quit":
			{
				if(sender instanceof CommandBlock && args.length>0){
					player = Bukkit.getPlayer(args[0]);
				}
				if(player!=null)
					CameraStudio.stop(player.getUniqueId());
					player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0,0.5,0));
				break;
			}
			case "reset":
			{
				if(args!=null && args.length>0 && player!=null){
					YamlConfiguration yamlConfiguration;
					try {
						yamlConfiguration = DataSource.getYamlResponse("elytra_race/contest_reset.php", new String[]{
								"contest_id="+((ElytraRace.currentContest!=null)?ElytraRace.currentContest.getContestId():0),
								"player="+URLEncoder.encode(args[0], "utf-8")
						});
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return true;
					}
					if(yamlConfiguration==null || !yamlConfiguration.contains("message")){
						sender.sendMessage("[ElytraRace] Es ist ein Fehler aufgetreten.");
						return true;
					}
					
					player.sendMessage(yamlConfiguration.getString("message"));
				}
				else{
					for(PlayerRace race : ElytraRace.races.values()){
						race.cancel();
					}
					ElytraRace.races.clear();
					DataSource.getResponse("elytra_race/contest_reset.php", new String[]{
							"contest_id="+ElytraRace.currentContest.getContestId(),
					});
					player.sendMessage(ChatColor.YELLOW+"Erfolg: Alle Highscores zurückgesetzt");
				}
				break;
			}
			default:
				break;
		}
		return true;
	}

}
