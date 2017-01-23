package ch.swisssmp.craftmmo.mmoquest;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

import ch.swisssmp.craftmmo.Main;
import net.md_5.bungee.api.ChatColor;

public class MmoQuest {
	public static HashMap<Integer, MmoQuest> quests = new HashMap<Integer, MmoQuest>();
	
	public final int mmo_quest_id;
	public final String name;
	public final String description;
	public HashMap<Integer, MmoQuestObjective> objectives = new HashMap<Integer, MmoQuestObjective>();
	
	public MmoQuest(ConfigurationSection dataSection){
		this.mmo_quest_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.description = dataSection.getString("description");
		quests.put(this.mmo_quest_id, this);
		Main.info("Registered Quest with id "+this.mmo_quest_id);
	}
	
	public String getQuestHeader(){
		String result = ChatColor.DARK_GRAY+name+"\n";
		result+= blockSeparator();
		return result;
	}
	
	public String blockSeparator(){
		return ChatColor.RESET+"O==========O\n";
	}
	
	public String getQuestText(){
		String result = getQuestHeader();
		result+= ChatColor.ITALIC+"Beschreibung\n";
		result+= ChatColor.RESET+description+"\n";
		return result;
	}
	
	public static MmoQuest get(int mmo_quest_id){
		return quests.get(mmo_quest_id);
	}
}
