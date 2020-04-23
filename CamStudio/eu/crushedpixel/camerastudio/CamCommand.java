package eu.crushedpixel.camerastudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.mysql.jdbc.StringUtils;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class CamCommand implements CommandExecutor {

	public static HashMap<UUID, List<Location>> points = new HashMap<UUID, List<Location>>();
	private static String prefix = CameraStudio.prefix;
	final static int previewTime = CameraStudio.getInstance().getConfig().getInt("preview-time");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player player = (sender instanceof Player) ? (Player) sender : null;
		if (args.length == 0) {
			sender.sendMessage(
					prefix + ChatColor.RED + "Type " + ChatColor.WHITE + "/cam help" + ChatColor.RED + " for details");
			return true;
		}

		if (player!=null && !CameraStudio.getInstance().getConfig().getStringList("allowed-gamemodes")
				.contains(player.getGameMode().toString()) && !player.hasPermission("camerastudio.override-gamemode")) {
			player.sendMessage(prefix + ChatColor.RED + "Du kannst diesen Befehl nicht in diesem GameMode verwenden.");
			return true;
		}

		String subcmd = args[0];

		String[] newArgs = new String[args.length - 1];
		for (int p = 1; p < args.length; p++) {
			newArgs[(p - 1)] = args[p];
		}

		args = newArgs;

		switch(subcmd.toLowerCase()){
		case "p":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.point")){
				return true;
			}
			List<Location> locs = (List<Location>) CamCommand.points.get(player.getUniqueId());
			if (locs == null) {
				locs = new ArrayList<Location>();
			}

			int maxPoints = new Integer(CameraStudio.getInstance().getConfig().getInt("maximum-points"));

			if (player.isOp()) {
				maxPoints = Integer.MAX_VALUE;
			} else {
				for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
					String permS = perm.getPermission();
					if (permS == "*") {
						maxPoints = Integer.MAX_VALUE;
						break;
					}
					if (permS.startsWith("camerastudio.point.")) {
						String substring = permS.substring(19);
						if (substring == "*") {
							maxPoints = Integer.MAX_VALUE;
							break;
						} else {
							try {
								maxPoints = Integer.parseInt(substring);
							} catch (NumberFormatException e) {
								player.sendMessage(prefix + ChatColor.RED
										+ "Berechtigungen nicht korrekt definiert. Bitte benachrichtige einen Ressortleiter.");
							}
							break;
						}
					}
				}
			}

			if (locs.size() >= maxPoints) {
				player.sendMessage(
						prefix + ChatColor.RED + "Du hast bereits " + maxPoints + " Punkte gesetzt.");
				return true;
			}

			locs.add(player.getLocation());
			CamCommand.points.put(player.getUniqueId(), locs);

			player.sendMessage(prefix + "Point " + locs.size() + " has been set");

			return true;
		}
		case "r":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.remove")){
				return true;
			}
			List<Location> locs = (List<Location>) CamCommand.points.get(player.getUniqueId());
			if (locs == null) {
				locs = new ArrayList<Location>();
			}

			if (args.length == 0) {
				if (locs.size() > 0) {
					locs.remove(locs.size() - 1);
					player.sendMessage(prefix + "Punkt " + (locs.size() + 1) + " entfernt.");
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Du hast noch keine Punkte gesetzt.");
					return true;
				}
			} else if (args.length == 1) {
				try {
					int pos = Integer.valueOf(args[0]).intValue();
					if (locs.size() >= pos) {
						locs.remove(pos - 1);
						player.sendMessage(prefix + "Punkt " + pos + " entfernt.");
					} else {
						if (locs.size() == 1) {
							player.sendMessage(prefix + ChatColor.RED + "Du hast nur einen Punkt gesetzt.");
							return true;
						} else {
							player.sendMessage(prefix + ChatColor.RED + "Du hast nur " + locs.size() + " Punkte gesetzt.");
							return true;
						}
					}
				} catch (Exception e) {
					player.sendMessage(prefix + ChatColor.RED + args[0] + " ist keine gültige Zahl.");
					return true;
				}
			}

			CamCommand.points.put(player.getUniqueId(), locs);
			return true;
		}
		case "list":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.list")){
				return true;
			}
			List<Location> locs = (List<Location>) CamCommand.points.get(player.getUniqueId());
			if ((locs == null) || (locs.size() == 0)) {
				player.sendMessage(prefix + ChatColor.RED + "Du hast noch keine Punkte gesetzt.");
				return true;
			}

			int i = 1;
			for (Location loc : locs) {
				player.sendMessage(prefix + "Point " + i + ": " + CameraStudio.round(loc.getX(), 1) + ", "
						+ CameraStudio.round(loc.getY(), 1) + ", " + CameraStudio.round(loc.getZ(), 1) + " ("
						+ CameraStudio.round(loc.getYaw(), 1) + ", " + CameraStudio.round(loc.getPitch(), 1) + ")");
				i++;
			}
			return true;
		}
		case "reset":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.reset")){
				return true;
			}
			// Made an error, replaced it with the line below:
			// this.points.put(player, new ArrayList<Object>());
			CamCommand.points.remove(player.getUniqueId());
			player.sendMessage(prefix + "Alle Punkte entfernt.");
			return true;
		}
		case "reload":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.admin")){
				return true;
			}
			CameraStudio.getInstance().reloadConfig();
			sender.sendMessage(prefix + ChatColor.YELLOW + "Konfiguration neu geladen!");
			return true;
		}
		case "goto":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.goto")){
				return true;
			}
			if (args.length == 1) {
				try {
					int pos = Integer.valueOf(args[0]).intValue();
					List<Location> locs = (List<Location>) CamCommand.points.get(player.getUniqueId());
					if ((locs != null) && (locs.size() >= pos)) {
						player.teleport((Location) locs.get(pos - 1));
						player.sendMessage(prefix + "Zu Punkt " + pos + " teleportiert.");
						return true; // This was "break label1152;"
					}
					if (locs == null) {
						player.sendMessage(prefix + ChatColor.RED + "Du hast noch keine Punkte gesetzt.");
						return true;
					}
					player.sendMessage(prefix + ChatColor.RED + "Du hast nur " + locs.size() + " Punkte gesetzt");
					return true;
				} catch (Exception e) {
					player.sendMessage(prefix + ChatColor.RED + args[0] + " ist keine Zahl.");
					return true;
				}
			}
			player.sendMessage(prefix + ChatColor.RED + "Du musst einen Zielpunkt angeben.");

			// Says it wasn't referenced, I just removed it:
			// label1152:
			// return true;
			return true;
		}
		case "stop":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.stop")){
				return true;
			}
			CameraStudio.stop(player.getUniqueId());
			player.sendMessage(prefix + "Kamerafahrt gestoppt.");
			return true;
		}
		case "help":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.help")){
				return true;
			}
			if (args.length == 0) {
				player.performCommand("help CPCameraStudioReborn");
				return true;
			} else {
				if (args.length>0) {
					try {
						player.performCommand("help CPCameraStudioReborn " + Integer.parseInt(args[0]));
						return true;
					} catch (NumberFormatException e) {
						player.sendMessage(prefix + ChatColor.YELLOW + args[0] + ChatColor.RED + "is not a number!");
						return true;
					}
				}
			}
			return true;
		}
		case "sequence":{
			if(args.length<1) return false;
			Player currentPlayer;
			if(args.length<2) currentPlayer = player;
			else currentPlayer = Bukkit.getPlayer(args[1]);
			if(currentPlayer==null){
				sender.sendMessage("[CamStudio] Spieler "+args[1]+" nicht gefunden.");
				return true;
			}
			String sequence_key = args[0];
			CameraPathSequence cameraPathSequence;
			if(StringUtils.isStrictlyNumeric(sequence_key)){
				cameraPathSequence = CameraPathSequence.load(Integer.parseInt(sequence_key));
			}
			else{
				cameraPathSequence = CameraPathSequence.load(sequence_key);
			}
			if(cameraPathSequence==null){
				Bukkit.getLogger().info("[CamStudio] Sequence "+sequence_key+" not found.");
				return true;
			}
			cameraPathSequence.runSequence(currentPlayer, null);
			return true;
		}
		case "start":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.start")){
				return true;
			}

			List<Location> listOfLocs = new ArrayList<Location>();
			if (CamCommand.points.get(player.getUniqueId()) != null)
				listOfLocs.addAll(CamCommand.points.get(player.getUniqueId()));
			Player currentPlayer = player;

			if (CameraStudio.isTravelling(currentPlayer.getUniqueId())) {
				currentPlayer.sendMessage(prefix + ChatColor.RED + "Du bewegst dich bereits auf einem Pfad.");
				return true;
			}

			if (listOfLocs.isEmpty()) {
				player.sendMessage(prefix + ChatColor.RED + "Nicht genügend Punkte gesetzt.");
				return true;
			}
			try {
				int time = previewTime * (listOfLocs.size() - 1);
				if (subcmd.equalsIgnoreCase("start")) {
					time = Integer.parseInt(args[0])*20;
				}
				CameraStudio.travel(currentPlayer, listOfLocs, time,
						prefix + ChatColor.RED + "Ein Fehler ist aufgetreten.",
						prefix + "Kamerafahrt beendet.");
			} catch (Exception e) {
				player.sendMessage(prefix + ChatColor.RED + "Du musst eine Dauer (in Sekunden) für die Kamerafahrt angeben."
						+ ChatColor.YELLOW + "Beispiel: /cam start 60");
			}
			return true;
		}
		case "save":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.save")){
				return true;
			}
			if(args.length<1){
				return false;
			}
			String name = args[0];
			if (points.get(player.getUniqueId()) != null) {

				List<String> locationLines = new ArrayList<String>();
				int index = 0;
				for (Location loc : points.get(player.getUniqueId())) {
					locationLines.add("points["+index+"][x]="+loc.getX());
					locationLines.add("points["+index+"][y]="+loc.getY());
					locationLines.add("points["+index+"][z]="+loc.getZ());
					locationLines.add("points["+index+"][pitch]="+loc.getPitch());
					locationLines.add("points["+index+"][yaw]="+loc.getYaw());
					index++;
				}

				DataSource.getResponse(CameraStudio.getInstance(), "save_path.php", new String[]{
					"name="+URLEncoder.encode(name),
					String.join("&", locationLines)
				});
				player.sendMessage(prefix + ChatColor.BLUE + args[0]
							+ ChatColor.YELLOW + " gespeichert.");
				return true;
			} else {
				player.sendMessage(prefix + ChatColor.RED + "Du hast noch keine Punkte gesetzt.");
				return true;
			}
		}
		case "load":{
			if(player==null){
				sender.sendMessage("Befehl kann nur ingame verwendet werden.");
				return true;
			}
			if(!player.hasPermission("camerastudio.load")){
				return true;
			}
			if (CameraStudio.isTravelling(player.getUniqueId())) {
				player.sendMessage(prefix + ChatColor.RED + "Du bewegst dich bereits auf einem Pfad.");
				return true;
			}
			if(args.length<1) return false;
			HTTPRequest request = DataSource.getResponse(CameraStudio.getInstance(), "load_path.php", new String[]{
					"name="+URLEncoder.encode(args[0])	
				});
			final String path = args[0];
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration==null || !yamlConfiguration.contains("path")){
					sender.sendMessage(prefix + ChatColor.RED + "Pfad '"+path+"' nicht gefunden.");
					return;
				}
				ConfigurationSection pathSection = yamlConfiguration.getConfigurationSection("path");
				CameraPath cameraPath = new CameraPath(player.getWorld(),pathSection);
				List<Location> listOfLocations = new ArrayList<Location>();
				listOfLocations.addAll(cameraPath.getPoints());
				points.put(player.getUniqueId(), listOfLocations);
				player.sendMessage(prefix + ChatColor.YELLOW + "Pfad: " + ChatColor.BLUE + cameraPath.getName() + ChatColor.YELLOW + " geladen!");
			});
			player.sendMessage(prefix + ChatColor.YELLOW + "Pfad: " + ChatColor.BLUE + path + ChatColor.YELLOW + " wird geladen!");
			return true;
		}
		default: return false;
		}
	}
}
