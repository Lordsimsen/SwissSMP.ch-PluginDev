package ch.swisssmp.craftmmo.mmoquest;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoevent.MmoEvent;
import ch.swisssmp.craftmmo.mmoevent.MmoEventType;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayer;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayerParty;
import ch.swisssmp.craftmmo.mmoplayer.MmoQuestbook;
import ch.swisssmp.craftmmo.util.MmoResourceManager;
import net.md_5.bungee.api.ChatColor;

public abstract class MmoQuestObjective implements Listener{
	
	public static HashMap<Integer, MmoQuestObjective> questInstances = new HashMap<Integer, MmoQuestObjective>();
	
	public final UUID player_uuid;
	public final int mmo_quest_id;
	public final int mmo_quest_instance_id;
	public final int mmo_quest_objective_id;
	public final String name;
	public final String description;
	public final MmoQuestObjectiveType type;
	private boolean completed;
	public final File localSaveData;
	public final boolean isPartyQuest;
	public final boolean isHiddenQuest;
	
	public final String action_label;
	
	private boolean successful = false;

	public final HashMap<MmoEventType, MmoEvent> events = new HashMap<MmoEventType, MmoEvent>();
	
	public MmoQuestObjective(UUID player_uuid, ConfigurationSection dataSection){
		this.player_uuid = player_uuid;
		this.mmo_quest_instance_id = dataSection.getInt("quest_instance_id");
		this.mmo_quest_objective_id = dataSection.getInt("quest_objective_id");
		this.name = dataSection.getString("name");
		this.description = dataSection.getString("description");
		this.type = MmoQuestObjectiveType.get(dataSection.getString("type"));
		this.mmo_quest_id = dataSection.getInt("mmo_quest_id");
		this.isPartyQuest = (dataSection.getInt("isPartyQuest")==1);
		this.isHiddenQuest = (dataSection.getInt("isHiddenQuest")==1);
		if(!MmoQuest.quests.containsKey(mmo_quest_id)){
			new MmoQuest(dataSection.getConfigurationSection("quest"));
		}
		this.action_label = dataSection.getConfigurationSection("configuration").getString("action_label");
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		MmoEvent.registerAll(dataSection, events);
		localSaveData = new File(Main.plugin.getDataFolder(), "quest_instances/"+dataSection.getInt("quest_instance_id")+".yml");
		questInstances.put(this.mmo_quest_instance_id, this);
		MmoEvent.fire(events, MmoEventType.QUEST_START, player_uuid);
	}
	
	public String getObjectiveHeader(){
		MmoQuest mmoQuest = MmoQuest.get(mmo_quest_id);
		String result = mmoQuest.getQuestHeader();
		result+= createSubtitle(name);
		result+= ChatColor.RESET+"Aufgabe: "+type.getColor()+action_label+"\n";
		result+= ChatColor.RESET+description+"\n";
		return result;
	}
	
	public String createSubtitle(String text){
		return ChatColor.RESET+""+ChatColor.ITALIC+text+"\n";
	}
	
	public abstract String getObjectiveText();
	
	public abstract void save();
	public abstract void load();
	
	public void updateObjective(){
		MmoQuestbook.update(this, this.player_uuid);
	}
	
	public boolean canContribute(UUID player_uuid){
		return canContribute(player_uuid, this.isPartyQuest);
	}
	
	public boolean canContribute(UUID player_uuid, boolean checkParty){
		if(player_uuid==null){
			Main.debug("UUID==null");
			return false;
		}
		if(player_uuid==this.player_uuid){
			Main.debug("canContribute");
			return true;
		}
		else if(checkParty){
			Main.debug("check party");
			MmoPlayerParty ownersParty = MmoPlayerParty.get(this.player_uuid);
			MmoPlayerParty contributorsParty = MmoPlayerParty.get(player_uuid);
			if(ownersParty==null || contributorsParty==null)return false;
			return (ownersParty.mmo_party_id==contributorsParty.mmo_party_id);			
		}
		else{
			Main.debug("else return false");
			return false;
		}
	}
	
	public void setCompleted(boolean successful){
		this.completed = true;
		this.successful = successful;
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(this.successful){
			MmoEvent.fire(events, MmoEventType.QUEST_CLEARED, player_uuid);
			MmoPlayer.sendTitle(player, "Quest abgeschlossen!", this.name, 1, 3, 1);
			MmoResourceManager.processYamlResponse(this.player_uuid, "clearquest.php", new String[]{"quest="+mmo_quest_instance_id});
		}
		else{
			MmoEvent.fire(events, MmoEventType.QUEST_FAILED, player_uuid);
			MmoPlayer.sendTitle(player, "Quest gescheitert!", this.name, 1, 3, 1);
			MmoResourceManager.processYamlResponse(this.player_uuid, "failquest.php", new String[]{"quest="+mmo_quest_instance_id});
		}
		this.delete();
		Runnable runnable = new Runnable(){
			@Override
			public void run(){
				MmoQuestbook.load(player_uuid);
				MmoQuestbook.relinkInstance(player_uuid);
			}
		};
		Bukkit.getScheduler().runTaskLater(Main.plugin, runnable, 10);
	}
	
	public boolean isCompleted(){
		return this.completed;
	}
	
	public void delete(){
		HandlerList.unregisterAll(this);
		if(localSaveData.exists()){
			localSaveData.delete();
		}
		if(questInstances.containsKey(this.mmo_quest_instance_id)){
			questInstances.remove(this.mmo_quest_instance_id);
		}
	}
	
	//static methods ==========================================================
	
	public static MmoQuestObjective get(int mmo_quest_instance_id){
		return questInstances.get(mmo_quest_instance_id);
	}
	
	public static MmoQuestObjective construct(UUID player_uuid, ConfigurationSection dataSection){
		MmoQuestObjectiveType questType = MmoQuestObjectiveType.get(dataSection.getString("type"));
		switch(questType){
		case VARIOUS:
			return new MmoQuestObjectiveVarious(player_uuid, dataSection);
		case HUNT:
			return new MmoQuestObjectiveHunt(player_uuid, dataSection);
		case GATHER:
			return new MmoQuestObjectiveGather(player_uuid, dataSection);
		case DISCOVER:
			return new MmoQuestObjectiveDiscover(player_uuid, dataSection);
		case PROTECT:
			return new MmoQuestObjectiveProtect(player_uuid, dataSection);
		case SIEGE:
			return new MmoQuestObjectiveSiege(player_uuid, dataSection);
		case ACTION:
			return new MmoQuestObjectiveAction(player_uuid, dataSection);
		default:
			return null;
		}
	}
}
