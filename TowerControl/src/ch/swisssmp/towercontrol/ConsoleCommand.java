package ch.swisssmp.towercontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.mysql.jdbc.StringUtils;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class ConsoleCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Player player = (Player) sender;
		if(args==null) return false;
		if(args.length<1) return false;
		switch(command.getName()){
		case "karte":{
		switch(args[0]){
			case "reload":
			case "aktualisieren":
			{
				if(TowerControl.game!=null)TowerControl.game.setFinished(null, null);
				Arena.loadArenas();
				sender.sendMessage("[TowerControl] Karten aktualisiert");
				return true;
			}
			case "set":
			case "aktivieren":
			case "aktiviere":{
				if(args.length<2) return false;
				if(!StringUtils.isStrictlyNumeric(args[1])) return false;
				int arena_id = Integer.parseInt(args[1]);
				Arena arena = Arena.get(arena_id);
				if(arena==null){
					sender.sendMessage("[TowerControl] Karte '"+args[1]+"' nicht gefunden.");
					return true;
				}
				else{
					TowerControl.setCurrentArena(arena);
					sender.sendMessage("[TowerControl] Karte '"+arena.getName()+"' aktiviert.");
				}
				return true;
			}
			case "edit":
			case "bearbeiten":
			{
				if(!(sender instanceof Player)){
					sender.sendMessage("[TowerControl] Kann nur ingame verwendet werden.");
					return true;
				}
				if(TowerControl.getCurrentArena()==null){
					sender.sendMessage("[TowerControl] Zuerst eine Karte mit '/karte aktiviere [Karten-ID]' aktivieren.");
					return true;
				}
				Player player = (Player) sender;
				player.teleport(TowerControl.getCurrentArena().getWorld().getSpawnLocation());
				Inventory inventory = player.getInventory();
				inventory.clear();
				CustomItemBuilder markerBlueBuilder = CustomItems.getCustomItemBuilder("MARKER_BLUE");
				markerBlueBuilder.setDisplayName("Spawnpunkt Blau");
				markerBlueBuilder.setLore(Arrays.asList("§hSET_SPAWNPOINT_BLUE","Rechtsklick um Punkt zu setzen"));
				inventory.addItem(markerBlueBuilder.build());
				CustomItemBuilder markerRedBuilder = CustomItems.getCustomItemBuilder("MARKER_RED");
				markerRedBuilder.setDisplayName("Spawnpunkt Rot");
				markerRedBuilder.setLore(Arrays.asList("§hSET_SPAWNPOINT_RED","Rechtsklick um Punkt zu setzen"));
				inventory.addItem(markerRedBuilder.build());
				CustomItemBuilder towerBlueBuilder = CustomItems.getCustomItemBuilder("TOWER_BLUE");
				towerBlueBuilder.setDisplayName("Turm Blau");
				towerBlueBuilder.setLore(Arrays.asList("§hSET_TOWER_BLUE","Rechtsklick auf Druckplatte"));
				inventory.addItem(towerBlueBuilder.build());
				CustomItemBuilder towerRedBuilder = CustomItems.getCustomItemBuilder("TOWER_RED");
				towerRedBuilder.setDisplayName("Turm Rot");
				towerRedBuilder.setLore(Arrays.asList("§hSET_TOWER_RED","Rechtsklick auf Druckplatte"));
				inventory.addItem(towerRedBuilder.build());
				CustomItemBuilder towerBypassBuilder = CustomItems.getCustomItemBuilder("TOWER_BYPASS");
				towerBypassBuilder.setDisplayName("Bypass Turm");
				towerBypassBuilder.setLore(Arrays.asList("§hSET_TOWER_BYPASS","Rechtsklick auf Druckplatte"));
				inventory.addItem(towerBypassBuilder.build());
				CustomItemBuilder towerPassageBuilder = CustomItems.getCustomItemBuilder("TOWER_PASSAGE");
				towerPassageBuilder.setDisplayName("Passage Turm");
				towerPassageBuilder.setLore(Arrays.asList("§hSET_TOWER_PASSAGE","Rechtsklick auf Druckplatte"));
				inventory.addItem(towerPassageBuilder.build());
				CustomItemBuilder towerPotionBuilder = CustomItems.getCustomItemBuilder("TOWER_POTION");
				towerPotionBuilder.setDisplayName("Trank Turm");
				towerPotionBuilder.setLore(Arrays.asList("§hSET_TOWER_POTION","Rechtsklick auf Druckplatte"));
				inventory.addItem(towerPotionBuilder.build());
				CustomItemBuilder towerSniperBuilder = CustomItems.getCustomItemBuilder("TOWER_SNIPER");
				towerSniperBuilder.setDisplayName("Sniper Turm");
				towerSniperBuilder.setLore(Arrays.asList("§hSET_TOWER_SNIPER","Rechtsklick auf Druckplatte"));
				inventory.addItem(towerSniperBuilder.build());
				return true;
			}
			case "list":
			case "auflisten":{
				sender.sendMessage("[TowerControl] Folgende Karten stehen zur Verfügung:");
				for(Arena arena : Arena.getArenas()){
					if(TowerControl.getCurrentArena()==arena){
						sender.sendMessage("- ["+arena.getArenaId()+"] "+ChatColor.GOLD+arena.getName()+" (aktiv)");
					}
					else{
						sender.sendMessage("- ["+arena.getArenaId()+"] "+arena.getName());
					}
				}
				return true;
			}
			case "items":
			case "inventar":
			case "inventory":{
				if(!(sender instanceof Player)){
					sender.sendMessage("[TowerControl] Kann nur ingame verwendet werden.");
					return true;
				}
				Arena arena = Arena.get(((Player)sender).getWorld());
				if(arena==null) arena = TowerControl.getCurrentArena();
				if(arena==null){
					sender.sendMessage("[TowerControl] Zuerst eine Arena betreten oder aktivieren mit '/karte aktivieren [Arena-ID]'");
					return true;
				}
				PlayerInventory templateInventory = ((Player)sender).getInventory();
				List<String> inventoryStrings = new ArrayList<String>();
				for(ItemStack itemStack : templateInventory){
					if(itemStack==null) continue;
					inventoryStrings.add("items[]="+SwissSMPUtils.encodeItemStack(itemStack));
				}
				if(inventoryStrings.size()==0){
					String materialString;
					for(ItemStack itemStack : arena.getInventoryTemplate()){
						materialString = itemStack.getType().toString().toLowerCase();
						if(materialString.contains("helmet")){
							templateInventory.setHelmet(itemStack);
						}
						else if(materialString.contains("chestplate")){
							templateInventory.setChestplate(itemStack);
						}
						else if(materialString.contains("leggings")){
							templateInventory.setLeggings(itemStack);
						}
						else if(materialString.contains("boots")){
							templateInventory.setBoots(itemStack);
						}
						else if(materialString.contains("shield")){
							templateInventory.setItemInOffHand(itemStack);
						}
						else{
							templateInventory.addItem(itemStack);
						}
					}
					sender.sendMessage("[TowerControl] Inventar ausgerüstet.");
					return true;
				}
//				else{
//					YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("towercontrol/set_items.php", new String[]{
//							"arena="+arena.getArenaId(),
//							String.join("&",inventoryStrings)
//					});
//					if(yamlConfiguration==null || !yamlConfiguration.contains("message")) return true;
//					sender.sendMessage(yamlConfiguration.getString("message"));
//					return true;
//				}
			}
			default:
				break;
		}
		break;
		}
		case "spiel":{
		switch(args[0]){
			case "start":
			case "starten":{
				if(TowerControl.game==null || TowerControl.game.getGameState()==GameState.FINISHED){
					TowerControl.getPlugin().prepareGame(sender);
				}
				if(TowerControl.game==null) return true;
				if(TowerControl.game.getGameState()==GameState.PREGAME){
					TowerControl.game.setFightphase();
					sender.sendMessage("[TowerControl] Partie gestartet.");
					return true;
				}
				else if(TowerControl.game.getGameState()==GameState.FIGHT){
					sender.sendMessage("[TowerControl] Die Partie läuft bereits.");
					return true;
				}
				else{
					sender.sendMessage("[TowerControl] Konnte keine neue Partie starten. (Fehler");
				}
				return true;
			}
			case "end":
			case "beenden":{
				if(TowerControl.game==null){
					sender.sendMessage("[TowerControl] Momentan läuft keine Partie.");
					return true;
				}
				TowerControl.game.setFinished(null, null);
				sender.sendMessage("[TowerControl] Partie beendet.");
				return true;
			}
			case "debug":{
				TowerControl.debug = !TowerControl.debug;
				if(TowerControl.debug){
					sender.sendMessage("[TowerControl] Debug Modus eingeschaltet.");
				}
				else{
					sender.sendMessage("[TowerControl] Debug Modus ausgeschaltet.");
				}
				return true;
			}
			default:
			break;
		}
			break;
		}
		}
		return false;
	}

}
