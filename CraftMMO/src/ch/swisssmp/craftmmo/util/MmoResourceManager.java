package ch.swisssmp.craftmmo.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;
import ch.swisssmp.craftmmo.mmoitem.MmoLootInventory;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayerParty;
import ch.swisssmp.craftmmo.mmoplayer.MmoQuestbook;
import ch.swisssmp.craftmmo.mmoquest.MmoQuestObjective;
import ch.swisssmp.craftmmo.mmoshop.MmoShop;
import net.md_5.bungee.api.ChatColor;

public class MmoResourceManager {
	
	public static String rootURL;
	public static String pluginToken;
	private static Random random = new Random();
	
	public static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	public static String getResponse(String relativeURL, String[] params){
		String resultString = "";
		try{
			String urlString = rootURL+relativeURL+"?token="+pluginToken+"&random="+random.nextInt(1000);
			if(params!=null && params.length>0){
				urlString+="&"+String.join("&", params);
			}
			Main.info("Connecting to: "+urlString);
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String tempString = "";
			while(null!=(tempString = br.readLine())){
				resultString+= tempString;
			}
			if(resultString.isEmpty()){
				Main.info("Returning empty result");
				return "";
			}
			return resultString;
		}
		catch(Exception e){
			e.printStackTrace();
			Main.info("Causing the error: "+resultString);
			return "";
		}
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL){
		return getYamlResponse(relativeURL, null);
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL, String[] params){
		String resultString = convertWebYamlString(getResponse(relativeURL, params));
		if(resultString.isEmpty()){
			Main.info("Returning empty result");
			return new YamlConfiguration();
		}
		try{
			Main.info("Result: "+resultString);
			YamlConfiguration yamlConfiguration = new YamlConfiguration();
			yamlConfiguration.loadFromString(resultString);
			return yamlConfiguration;
		}
		catch(Exception e){
			e.printStackTrace();
			Main.info("Causing the error: "+resultString);
			return new YamlConfiguration();
		}
	}
    public static String convertWebYamlString(String webYamlString){
    	webYamlString = webYamlString.replace("<br>", "\r\n");
    	webYamlString = webYamlString.replace("&nbsp;", " ");
    	return webYamlString;
    }
    public static void processYamlResponse(UUID player_uuid, String relativeURL){
    	processYamlResponse(player_uuid, relativeURL, null);
    }
	public static void processYamlResponse(UUID player_uuid, String relativeURL, String[] params){
    	YamlConfiguration serverResponse = getYamlResponse(relativeURL, params);
    	processYamlData(player_uuid, serverResponse);
    }
	public static void processYamlData(UUID player_uuid, YamlConfiguration yamlConfiguration){
		Player player = Bukkit.getPlayer(player_uuid);
    	MmoMob speaker = null;
    	if(yamlConfiguration.contains("speaker")){
    		int mmo_mob_id = yamlConfiguration.getInt("speaker");
    		speaker = MmoMob.get(mmo_mob_id);
    	}
		if(yamlConfiguration.contains("text") && player!=null){
			if(speaker!=null){
				player.sendMessage("<"+ChatColor.GRAY+speaker.name+ChatColor.RESET+"> "+yamlConfiguration.getString("text"));
			}
			else{
				player.sendMessage(yamlConfiguration.getString("text"));
			}
		}
		if(yamlConfiguration.contains("quests")){
			ConfigurationSection questsSection = yamlConfiguration.getConfigurationSection("quests");
			if(player!=null){
				for(String stringIndex : questsSection.getKeys(false)){
					ConfigurationSection questSection = questsSection.getConfigurationSection(stringIndex);
					int quest_instance_id = questSection.getInt("quest_instance_id");
					String action = questSection.getString("action");
					switch(action){
					case "start":{
						String label = "Questbuch aktualisiert.";
						if(questSection.contains("name")){
							label = questSection.getString("name");
						}
						if(questSection.contains("show_new")){
							if(questSection.getInt("show_new")==1){
								MmoPlayer.sendTitle(player, "Neue Quest!", label, 1, 3, 1);
							}
						}
						else{
							MmoPlayer.sendTitle(player, "Neue Quest!", label, 1, 3, 1);
						}
					}
						break;
					case "fail":{
						MmoQuestObjective questObjective = MmoQuestObjective.get(quest_instance_id);
						if(questObjective==null){
							break;
						}
						if(!questObjective.isCompleted()){
							questObjective.setCompleted(false);
						}
					}
						break;
					case "success":{
						MmoQuestObjective questObjective = MmoQuestObjective.get(quest_instance_id);
						if(questObjective==null){
							break;
						}
						if(!questObjective.isCompleted()){
							questObjective.setCompleted(true);
						}
					}
						break;
					default:
						break;
					}
				}
			}
		}
		if(yamlConfiguration.contains("choices")){
			ArrayList<String> choices = new ArrayList<String>();
			ConfigurationSection choicesSection = yamlConfiguration.getConfigurationSection("choices");
			for(String choiceID : choicesSection.getKeys(false)){
				ConfigurationSection choiceSection = choicesSection.getConfigurationSection(choiceID);
				String label = choiceSection.getString("text");
				int lore_progress_id = choiceSection.getInt("lore_progress_id");
				String choice = choiceSection.getString("choice");
				String source = choiceSection.getString("source");
				String choiceString = "{'text':'-"+label+"- ','italic':'true','color':'gray','clickEvent':{'action':'run_command','value':'/talk "+pluginToken+" "+lore_progress_id+" source "+source+" choice "+choice+"'}}";
				choices.add(choiceString);
			}
			if(choices.size()>0){
				MmoPlayer.sendRawMessage(player, "{'text':'','extra':["+String.join(",", choices)+"]}");
			}
		}
		if(yamlConfiguration.contains("items") && player!=null){
			ConfigurationSection rewardsSection = yamlConfiguration.getConfigurationSection("items");
			for(String stringIndex : rewardsSection.getKeys(false)){
				//get item data
				ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(stringIndex);
				ItemStack itemStack = MmoItemManager.getItemFromHybridID(rewardSection);
				if(itemStack==null)
					continue;
				//drop the actual item
				Location location = player.getLocation();
				World world = location.getWorld();
				world.dropItem(location, itemStack);
				String name = itemStack.getType().name();
				if(itemStack.getItemMeta().hasDisplayName())
					name = itemStack.getItemMeta().getDisplayName();
				MmoPlayer.sendMessage(player, ChatColor.YELLOW+""+itemStack.getAmount()+" "+name+ChatColor.YELLOW+" erhalten!");
			}
		}
		if(yamlConfiguration.contains("questbook") && player!=null){
			switch(yamlConfiguration.getString("questbook")){
			case "show":
				MmoQuestbook.show(player_uuid);
				break;
			case "hide":
				MmoQuestbook.hide(player_uuid);
				break;
			case "toggle":
				MmoQuestbook.toggle(player_uuid);
				break;
			case "load":
				MmoQuestbook.load(player_uuid);
				break;
			case "reload":
				MmoQuestbook.load(player_uuid);
				MmoQuestbook.relinkInstance(player_uuid);
				break;
			}
		}
		if(yamlConfiguration.contains("shop") && player!=null){
			int mmo_shop_id = yamlConfiguration.getInt("shop");
			MmoShop mmoShop = MmoShop.get(mmo_shop_id);
			if(mmoShop!=null){
				mmoShop.open(player);
			}
		}
		if(yamlConfiguration.contains("loot") && player!=null){
			ConfigurationSection lootInfoSection = yamlConfiguration.getConfigurationSection("loot_info");
			String inventory_name = lootInfoSection.getString("name");
			int reset_time = lootInfoSection.getInt("time");
			String type = lootInfoSection.getString("type");
			boolean global = (lootInfoSection.getInt("global")==1);
			String action = lootInfoSection.getString("action");
			int x = lootInfoSection.getInt("x");
			int y = lootInfoSection.getInt("y");
			int z = lootInfoSection.getInt("z");
			String worldName = lootInfoSection.getString("world_instance");
			World world = Bukkit.getWorld(worldName);
			Location location = new Location(world, x, y, z);
			ConfigurationSection lootSection = yamlConfiguration.getConfigurationSection("loot");
			ArrayList<ItemStack> loot = new ArrayList<ItemStack>();
			for(String key : lootSection.getKeys(false)){
				ConfigurationSection itemSection = lootSection.getConfigurationSection(key);
				ItemStack itemStack = MmoItemManager.getItemFromHybridID(itemSection);
				if(itemStack!=null)
					loot.add(itemStack);
			}
			ItemStack[] items = new ItemStack[loot.size()];
			items = loot.toArray(items);
			Inventory inventory = Bukkit.createInventory(null, InventoryType.valueOf(type), inventory_name);
			inventory.setStorageContents(items);
			MmoLootInventory lootInventory = MmoLootInventory.create(player, action, global, location.getBlock(), inventory);
			BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.plugin, lootInventory, reset_time*20);
			lootInventory.task_id = task.getTaskId();
			player.openInventory(lootInventory.inventory);
		}
		if(yamlConfiguration.contains("drops") && player!=null){
			ConfigurationSection lootInfoSection = yamlConfiguration.getConfigurationSection("drops_info");
			boolean global = (lootInfoSection.getInt("global")==1);
			int x = lootInfoSection.getInt("x");
			int y = lootInfoSection.getInt("y");
			int z = lootInfoSection.getInt("z");
			String worldName = lootInfoSection.getString("world_instance");
			World world = Bukkit.getWorld(worldName);
			Location location = new Location(world, x+0.5, y+0.5, z+0.5);
			ConfigurationSection dropsSection = yamlConfiguration.getConfigurationSection("drops");
			for(String key : dropsSection.getKeys(false)){
				ConfigurationSection itemSection = dropsSection.getConfigurationSection(key);
				ItemStack itemStack = MmoItemManager.getItemFromHybridID(itemSection);
				if(itemStack!=null){
					if(global){
						world.dropItem(location, itemStack);
					}
					else{
						PlayerInventory playerInventory = player.getInventory();
						HashMap<Integer, ItemStack> failed = playerInventory.addItem(itemStack);
						if(failed.size()>0){
							for(ItemStack failedStack : failed.values()){
								world.dropItem(location, failedStack);
							}
						}
					}
				}
			}
		}
		if(yamlConfiguration.contains("commands") && player!=null){
			List<String> commandsList = yamlConfiguration.getStringList("commands");
			for(String command : commandsList){
				if(player!=null){
					command = command.replace("[player]", player.getName());
					command = command.replace("[world]", player.getWorld().getName());
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); 
			}
		}
		if(yamlConfiguration.contains("party")){
			ConfigurationSection partySection = yamlConfiguration.getConfigurationSection("party");
			int mmo_party_id = partySection.getInt("mmo_party_id");
			MmoPlayerParty party = MmoPlayerParty.get(mmo_party_id);
			String action = partySection.getString("action");
			switch(action){
			case "create":{
				new MmoPlayerParty(partySection);
				break;
			}
			case "disband":{
				if(party!=null){
					for(UUID uuid : party.members){
						MmoPlayerParty.playerMap.remove(uuid);
						Player leavePlayer = Bukkit.getPlayer(uuid);
						if(leavePlayer!=null){
							party.team.removeEntry(leavePlayer.getName());
							party.scoreboard.resetScores(leavePlayer.getName());
							MmoPlayerParty.clearPlayer(leavePlayer);
							MmoPlayer.sendMessage(uuid, ChatColor.GRAY+"Deine Gruppe wurde aufgelöst.");
						}
					}
					MmoPlayerParty.parties.remove(party.mmo_party_id);
				}
				break;
			}
			case "join":{
				UUID join_uuid = UUID.fromString(partySection.getString("player_uuid"));
				if(party!=null){
					MmoPlayerParty oldParty = MmoPlayerParty.get(join_uuid);
					if(oldParty!=null){
						oldParty.leave(join_uuid);
					}
					MmoPlayerParty.playerMap.put(join_uuid, mmo_party_id);
					party.members.add(join_uuid);
					party.membersMap.put(partySection.getString("player_name"), join_uuid);
					party.setupPlayer(join_uuid);
					MmoPlayer.sendMessage(join_uuid, ChatColor.GRAY+"Du bist nun in der Gruppe '"+party.name+"'.");
					Player joinPlayer = Bukkit.getPlayer(join_uuid);
					if(joinPlayer!=null){
						MmoPlayer.sendMessage(party.leader, ChatColor.GREEN+joinPlayer.getDisplayName()+ChatColor.GREEN+" ist deiner Gruppe beigetreten!");
					}
					
				}
				break;
			}
			case "leave":{
				UUID leave_uuid = UUID.fromString(partySection.getString("player_uuid"));
				MmoPlayerParty.playerMap.remove(leave_uuid);
				if(party!=null){
					party.members.remove(leave_uuid);
					Player leavePlayer = Bukkit.getPlayer(leave_uuid);
					if(leavePlayer!=null) {
						MmoPlayerParty.clearPlayer(leavePlayer);
						party.members.remove(leave_uuid);
						party.membersMap.remove(partySection.getString("player_name"));
						party.team.removeEntry(leavePlayer.getName());
						party.scoreboard.resetScores(leavePlayer.getName());
						if(!party.leader.equals(leave_uuid)){
							MmoPlayer.sendMessage(party.leader, ChatColor.GRAY+leavePlayer.getDisplayName()+ChatColor.GRAY+" hat deine Gruppe verlassen.");
						}
					}
					MmoPlayer.sendMessage(leave_uuid, ChatColor.GRAY+"Du hast die Gruppe '"+party.name+"' verlassen.");
				}
				break;
			}
			case "edit":{
				UUID newLeader = UUID.fromString(partySection.getString("leader"));
				if(!party.leader.equals(newLeader)){
					MmoPlayer.sendMessage(party.leader, ChatColor.RED+"Du bist nicht mehr Anführer der Gruppe!");
					party.leader = newLeader;
					MmoPlayer.sendMessage(newLeader, ChatColor.GREEN+"Du bist nun Anführer der Gruppe '"+party.getName()+"'!");
				}
				String newName = partySection.getString("name");
				if(!party.name.equals(newName)){
					party.name = newName;
					party.team.setDisplayName(newName);
					for(UUID member : party.members){
						MmoPlayer.sendMessage(member, ChatColor.YELLOW+"Deine Gruppe heisst nun "+newName+".");
					}
					party.team.setDisplayName(newName);
					party.objective.setDisplayName(newName);
				}
				String newTag = partySection.getString("tag");
				if(!party.tag.equals(newTag)){
					party.tag = newTag;
					party.team.setPrefix("["+newTag+"] ");
					for(UUID member : party.members){
						MmoPlayer.sendMessage(member, ChatColor.YELLOW+"Dein Gruppen-Tag ist nun "+newTag+".");
					}
				}
				for(UUID member : party.members){
					Player onlineMember = Bukkit.getPlayer(member);
					if(onlineMember!=null){
						party.setupPlayer(onlineMember);
					}
				}
				break;
			}
			default:
				break;
			}
		}
    }
}
