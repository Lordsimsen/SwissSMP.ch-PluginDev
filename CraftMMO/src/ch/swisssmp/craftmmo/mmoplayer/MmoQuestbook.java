package ch.swisssmp.craftmmo.mmoplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoquest.MmoQuest;
import ch.swisssmp.craftmmo.mmoquest.MmoQuestObjective;
import ch.swisssmp.craftmmo.util.MmoResourceManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_11_R1.MojangsonParseException;
import net.minecraft.server.v1_11_R1.MojangsonParser;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class MmoQuestbook {
	public static final HashMap<UUID, MmoQuestbook> questbooks = new HashMap<UUID, MmoQuestbook>();

	public final UUID player_uuid;
	public ArrayList<Integer> questInstanceIDs;
	private ItemStack instance;
	public HashMap<Integer, Integer> questPageMap;
	private ArrayList<String> pages;
	
	private MmoQuestbook(UUID uuid){
		this.player_uuid = uuid;
		load();
		questbooks.put(this.player_uuid, this);
	}
	
	public void cleanup(){
		for(Integer mmo_quest_instance_id : questInstanceIDs){
			MmoQuestObjective objective = MmoQuestObjective.get(mmo_quest_instance_id);
			if(objective!=null){
				objective.delete();
			}
		}
	}
	
	public static void load(UUID player_uuid){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.load();
	}
	
	public void load(){
		if(this.questInstanceIDs!=null){
			cleanup();
		}
		questInstanceIDs = new ArrayList<Integer>();
		YamlConfiguration mmoQuestsConfiguration = MmoResourceManager.getYamlResponse("loadquests.php", new String[]{"player="+player_uuid});
		for(String questIDstring : mmoQuestsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoQuestsConfiguration.getConfigurationSection(questIDstring);
			int mmo_quest_instance_id = dataSection.getInt("quest_instance_id");
			MmoQuestObjective.construct(player_uuid, dataSection);
			questInstanceIDs.add(mmo_quest_instance_id);
		}
	}
	
	private void preparePages(){
		ArrayList<String> lines = new ArrayList<String>();
		HashMap<Integer, ArrayList<MmoQuestObjective>> quests_data = new HashMap<Integer, ArrayList<MmoQuestObjective>>();
		this.pages = new ArrayList<String>();
		String titlepage = "";
		int maxLines = 8;
		int current_line = 1;
		HashMap<Integer, Integer> overviewPageIndexes = new HashMap<Integer, Integer>();
		for(Integer mmo_quest_instance_id : questInstanceIDs){
			current_line = 1;
			MmoQuestObjective objective = MmoQuestObjective.get(mmo_quest_instance_id);
			if(objective==null)continue;
			if(objective.isCompleted()||objective.isHiddenQuest){
				continue;
			}
			if(!quests_data.containsKey(objective.mmo_quest_id)){
				MmoQuest quest = MmoQuest.get(objective.mmo_quest_id);
				if(quest==null){
					Main.info("could not find quest with id "+objective.mmo_quest_id);
					continue;
				}
				lines.add("{text:'- "+quest.name+"\n',clickEvent:{action:'change_page',value:'[index_"+quest.mmo_quest_id+"]'}}");
				overviewPageIndexes.put(quest.mmo_quest_id, pages.size());
				current_line++;
				if(current_line>=maxLines){
					current_line = 0;
					titlepage ="{text:'"+ChatColor.UNDERLINE+"Quests"+ChatColor.RESET+"\n\n',extra:["+String.join(",", lines)+"]}";
					pages.add("'"+titlepage.replace("'", "\\'")+"'");
					lines.clear();
				}
				quests_data.put(objective.mmo_quest_id, new ArrayList<MmoQuestObjective>());
			}
			quests_data.get(objective.mmo_quest_id).add(objective);
		}
		if(current_line<maxLines && current_line>0){
			if(lines.size()<1){
				lines.add("{text:'Du hast momentan keine offenen Quests.'}");
			}
			titlepage ="{text:'"+ChatColor.UNDERLINE+"Quests"+ChatColor.RESET+"\n\n',extra:["+String.join(",", lines)+"]}";
			pages.add("'"+titlepage.replace("'", "\\'")+"'");
		}
		questPageMap = new HashMap<Integer, Integer>();
		int pageIndex = pages.size();
		for(int mmo_quest_id : quests_data.keySet()){
			MmoQuest mmoQuest = MmoQuest.get(mmo_quest_id);
			if(mmoQuest==null){
				continue;
			}
			int questPageIndex = pages.size();
			int overviewPageIndex = overviewPageIndexes.get(mmoQuest.mmo_quest_id);
			pages.set(overviewPageIndex, pages.get(overviewPageIndex).replace("[index_"+mmoQuest.mmo_quest_id+"]", String.valueOf(questPageIndex+1)));
			pages.add(simplePage(mmoQuest.getQuestText()));
			pageIndex++;
			for(MmoQuestObjective objective : quests_data.get(mmo_quest_id)){
				pages.add("'{text:\\'"+objective.getObjectiveText()+"\\'}'");
				questPageMap.put(objective.mmo_quest_instance_id, pageIndex);
				pageIndex++;
			}
		}
	}
	
	private String simplePage(String content){
		return "'{text:\\'"+content+"\\'}'";
	}
	
	private void materialize(){
		preparePages();
		try {
			applyContents("Questbuch hervorgeholt. Du kannst es jederzeit mit /quests verstauen oder einfach wegwerfen.");
		} catch (MojangsonParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void applyContents(UUID player_uuid, String finishMessage){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)
			try {
				questbook.applyContents(finishMessage);
			} catch (MojangsonParseException e) {
				e.printStackTrace();
			}
	}
	
	public void applyContents(String finishMessage) throws MojangsonParseException{
			relinkInstance(false);
			Player player = Bukkit.getPlayer(this.player_uuid);
			if(player==null)
				return;
			if(instance != null){
				player.getInventory().remove(this.instance);
			}
			ItemStack questbook = new ItemStack(Material.WRITTEN_BOOK);
			String dataString = "{title:'Questbuch',author:'"+player.getName()+"',pages:["+String.join(",", pages)+"]}";
			NBTTagCompound tags = MojangsonParser.parse(dataString.replace("'", "\""));
			net.minecraft.server.v1_11_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(questbook);
			itemStack.setTag(tags);
			this.instance = CraftItemStack.asCraftMirror(itemStack);
			HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(this.instance);
			if(failedItems.size()>0){
				player.sendMessage(ChatColor.GRAY+"Konnte das Questbuch nicht erstellen, da kein Platz im Inventar vorhanden ist.");
				this.instance = null;
			}
			else if(!finishMessage.isEmpty()){
				player.sendMessage(ChatColor.GRAY+finishMessage);
			}
	}
	
	private void dematerialize(){
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player==null)
			return;
		player.getInventory().remove(this.instance);
		this.instance = null;
		player.sendMessage(ChatColor.GRAY+"Questbuch verstaut. Du kannst es jederzeit mit /quests hervorholen.");
	}
	public static MmoQuestbook get(Player player, ItemStack itemStack){
		if(itemStack.getType()!=Material.WRITTEN_BOOK){
			return null;
		}
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		if(!bookMeta.hasTitle()){
			return null;
		}
		if(bookMeta.getTitle().contains("Questbuch")){
			return get(player.getUniqueId());
		}
		return null;
	}
	public static void update(MmoQuestObjective objective, UUID player_uuid){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.update(objective);
	}
	public void update(MmoQuestObjective objective){
		if(questPageMap==null){
			return;
		}
		if(relinkInstance(false)){
			if(!questPageMap.containsKey(objective.mmo_quest_instance_id)){
				return;
			}
			int page = questPageMap.get(objective.mmo_quest_instance_id);
			pages.set(page, simplePage(objective.getObjectiveText()));
			try {
				applyContents("");
			} catch (MojangsonParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void show(UUID player_uuid){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.show();
	}
	public void show(){
		relinkInstance(false);
		if(this.instance==null){
			this.materialize();
		}
	}
	
	public static void hide(UUID player_uuid){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.hide();
	}
	public void hide(){
		dematerialize();
	}
	
	public static void toggle(UUID player_uuid){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.toggle();
	}
	public void toggle(){
		relinkInstance(false);
		if(this.instance==null){
			this.materialize();
		}
		else{
			this.dematerialize();
		}
	}
	
	public static void relinkInstance(UUID player_uuid){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.relinkInstance();
	}
	public boolean relinkInstance(){
		return relinkInstance(true);
	}
	
	public static void relinkInstance(UUID player_uuid, boolean autoMaterialize){
		MmoQuestbook questbook = get(player_uuid);
		if(questbook!=null)questbook.relinkInstance(autoMaterialize);
	}
	public boolean relinkInstance(boolean autoMaterialize){
		this.instance = null;
		Player player = Bukkit.getPlayer(this.player_uuid);
		if(player==null)
			return false;
		PlayerInventory playerInventory = player.getInventory();
		for(ItemStack itemStack : playerInventory){
			if(itemStack==null){
				continue;
			}
			if(itemStack.getType()!=Material.WRITTEN_BOOK){
				continue;
			}
			BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
			if(bookMeta.hasTitle()){
				if(bookMeta.getTitle().contains("Questbuch")){
					this.instance = itemStack;
					break;
				}
			}
		}
		if(this.instance!=null){
			if(autoMaterialize){
				materialize();
			}
			return true;
		}
		else{
			Main.info("Kein Questbuch erkannt.");
			return false;
		}
	}
	
	public static MmoQuestbook get(UUID uuid){
		if(!questbooks.containsKey(uuid)){
			return new MmoQuestbook(uuid);
		}
		return questbooks.get(uuid);
	}
}
