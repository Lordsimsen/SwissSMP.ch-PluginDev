package ch.swisssmp.camerastudio;

import java.util.*;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class CamCommand implements CommandExecutor {

	public static HashMap<UUID, List<Location>> points = new HashMap<UUID, List<Location>>();
	final static int previewTime = CameraStudioPlugin.getInstance().getConfig().getInt("preview-time");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player player = (sender instanceof Player) ? (Player) sender : null;
		final String prefix = CameraStudioPlugin.getPrefix()+" ";
		final CameraStudio cameraStudio = CameraStudio.inst();
		if (args.length == 0) {
			sender.sendMessage(
					prefix + ChatColor.RED + "Type " + ChatColor.WHITE + "/cam help" + ChatColor.RED + " for details");
			return true;
		}

		if (player!=null && !CameraStudioPlugin.getInstance().getConfig().getStringList("allowed-gamemodes")
				.contains(player.getGameMode().toString()) && !player.hasPermission("camerastudio.override-gamemode")) {
			player.sendMessage(prefix + ChatColor.RED + "Du kannst diesen Befehl nicht in diesem GameMode verwenden.");
			return true;
		}

		String subcmd = args[0];

		{
			String[] newArgs = new String[args.length - 1];
			for (int p = 1; p < args.length; p++) {
				newArgs[(p - 1)] = args[p];
			}

			args = newArgs;
		}

		switch(subcmd.toLowerCase()){
			case "erstelle":
			case "create":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.create")){
					return true;
				}

				if(args.length<2){
					return false;
				}

				CameraStudioWorld world = CameraStudioWorld.get(player.getWorld());
				String[] nameParts = new String[args.length - 1];
				for (int p = 1; p < args.length; p++) {
					nameParts[(p - 1)] = args[p];
				}
				String name = String.join(" ", nameParts);
				if(args[0].equalsIgnoreCase("sequence") || args[0].equalsIgnoreCase("sequenz")){
					CameraPathSequence path = world.createSequence(name);
					player.getInventory().addItem(path.getItemStack());
				} else if(args[0].equalsIgnoreCase("path") || args[0].equalsIgnoreCase("pfad")) {
					CameraPath path = world.createPath(name);
					player.getInventory().addItem(path.getItemStack());
				}
				else{
					return false;
				}
				world.save();
				return true;
			}
			case "pfade":
			case "paths":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.paths")){
					return true;
				}

				CameraPathsView.open(player);
				return true;
			}
			case "sequenzen":
			case "sequences":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.sequences")){
					return true;
				}

				CameraPathSequencesView.open(player);
				return true;
			}

			case "punkt":
			case "point":
			case "p":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.point")){
					return true;
				}
				List<Location> locs = CamCommand.points.get(player.getUniqueId());
				if (locs == null) {
					locs = new ArrayList<>();
				}

				int maxPoints = new Integer(CameraStudioPlugin.getInstance().getConfig().getInt("maximum-points"));

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
			case "remove":
			case "entferne":
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
			case "punkte":
			case "points":
			case "list":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.list")){
					return true;
				}
				List<Location> locs = CamCommand.points.get(player.getUniqueId());
				if ((locs == null) || (locs.size() == 0)) {
					player.sendMessage(prefix + ChatColor.RED + "Du hast noch keine Punkte gesetzt.");
					return true;
				}

				int i = 1;
				for (Location loc : locs) {
					player.sendMessage(prefix + "Point " + i + ": " + cameraStudio.round(loc.getX(), 1) + ", "
							+ cameraStudio.round(loc.getY(), 1) + ", " + cameraStudio.round(loc.getZ(), 1) + " ("
							+ cameraStudio.round(loc.getYaw(), 1) + ", " + cameraStudio.round(loc.getPitch(), 1) + ")");
					i++;
				}
				return true;
			}
			case "clear":
			case "reset":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.reset")){
					return true;
				}
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
				CameraStudioPlugin.getInstance().reloadConfig();
				CameraStudioWorlds.unloadAll();
				CameraStudioWorlds.loadAll();
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
				cameraStudio.stop(player.getUniqueId());
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
				}
				try {
					player.performCommand("help CPCameraStudioReborn " + Integer.parseInt(args[0]));
					return true;
				} catch (NumberFormatException e) {
					player.sendMessage(prefix + ChatColor.YELLOW + args[0] + ChatColor.RED + "is not a number!");
					return true;
				}
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
				String sequenceKey = args[0];
				UUID sequenceUid;
				try{
					sequenceUid = UUID.fromString(sequenceKey);
				}
				catch (Exception e){
					return false;
				}

				CameraPathSequence sequence = cameraStudio.getSequence(sequenceUid).orElse(null);
				if(sequence==null){
					sender.sendMessage("[CamStudio] Sequenz nicht gefunden.");
					return true;
				}
				sequence.run(player);
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

				if (cameraStudio.isTravelling(currentPlayer.getUniqueId())) {
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
					cameraStudio.travel(currentPlayer, listOfLocs, time,
							prefix + ChatColor.RED + "Ein Fehler ist aufgetreten.",
							prefix + "Kamerafahrt beendet.");
				} catch (Exception e) {
					player.sendMessage(prefix + ChatColor.RED + "Du musst eine Dauer (in Sekunden) für die Kamerafahrt angeben."
							+ ChatColor.YELLOW + "Beispiel: /cam start 60");
				}
				return true;
			}
			case "lade":
			case "load":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}
				if(!player.hasPermission("camerastudio.load")){
					return true;
				}
				CameraPath path = getSelectedPath(player).orElse(null);
				if(path==null){
					SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Kein gültiges Pfad-Item");
					return true;
				}

				List<Location> points = path.getPoints();
				if(points.size()==0){
					SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Keine Punkte geladen");
					CamCommand.points.remove(player.getUniqueId());
					return true;
				}
				CamCommand.points.put(player.getUniqueId(), points);
				SwissSMPler.get(player).sendActionBar(prefix + ChatColor.BLUE + path.getName()
						+ ChatColor.YELLOW + " geladen.");
				return true;
			}
			case "speichere":
			case "save":{
				if(player==null){
					sender.sendMessage("Befehl kann nur ingame verwendet werden.");
					return true;
				}

				if(!player.hasPermission("camerastudio.save")){
					return true;
				}

				CameraPath path = getSelectedPath(player).orElse(null);
				if(path==null){
					SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Kein gültiges Pfad-Item");
					return true;
				}

				if (points.get(player.getUniqueId()) != null) {
					List<Location> points = CamCommand.points.get(player.getUniqueId());
					path.setPoints(points);
					path.getWorld().save();
					SwissSMPler.get(player).sendActionBar(prefix + ChatColor.BLUE + path.getName()
							+ ChatColor.YELLOW + " gespeichert.");
					return true;
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Du hast noch keine Punkte gesetzt.");
					return true;
				}
			}
			default: return false;
		}
	}

	private Optional<CameraPath> getSelectedPath(Player player){
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return Optional.empty();
		}

		return CameraPath.get(itemStack);
	}
}
