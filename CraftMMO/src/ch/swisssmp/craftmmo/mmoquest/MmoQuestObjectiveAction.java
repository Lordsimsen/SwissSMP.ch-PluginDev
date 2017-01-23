package ch.swisssmp.craftmmo.mmoquest;


import java.io.IOException;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoevent.MmoAction;
import ch.swisssmp.craftmmo.mmoevent.MmoActionEvent;
import ch.swisssmp.craftmmo.mmoitem.MmoItemStack;
import ch.swisssmp.craftmmo.mmoworld.MmoRegion;
import net.md_5.bungee.api.ChatColor;

public class MmoQuestObjectiveAction extends MmoQuestObjective{

	public final int mmo_region_id;
	public final Material material_target;
	public final String target_name;
	public final int target_amount;
	public final int itemInHand;
	public final MmoAction action;
	public int current_amount;
	
	public MmoQuestObjectiveAction(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		this.mmo_region_id = configurationSection.getInt("region");
		if(configurationSection.contains("material_target")){
			String material_target = configurationSection.getString("material_target").toUpperCase();
			if(!material_target.equals("-1")){
				this.material_target = Material.valueOf(material_target);
			}
			else{
				this.material_target = null;
			}
		}
		else{
			this.material_target = null;
		}
		this.itemInHand = configurationSection.getInt("material_hand");
		String actionName = configurationSection.getString("action");
		if(actionName!=null && !actionName.equals("-1")){
			this.action = MmoAction.valueOf(actionName.toUpperCase());
		}
		else{
			this.action = null;
		}
		this.target_amount = configurationSection.getInt("count");
		this.target_name = configurationSection.getString("target_name");
		this.current_amount = 0;
		if(localSaveData.exists()){
			load();
		}
	}
	
	@EventHandler
	private void onPlayerInteract(MmoActionEvent event){
		if(this.action != null && event.getMmoAction()!=this.action){
			Main.info("Action wrong");
			return;
		}
		Block block = event.getClickedBlock();
		if(material_target != null && block.getType()!=this.material_target){
			Main.info("No material match");
			return;
		}
		if(!MmoRegion.regionMatch(event.getClickedBlock().getLocation(), this.mmo_region_id)){
			Main.info("No region match");
			return;
		}
		if(this.itemInHand==0 && event.getMmoItemStack()!=null){
			Main.info("Hand should be empty");
			return;
		}
		else if(this.itemInHand>0){
			MmoItemStack mmoItemStack = event.getMmoItemStack();
			if(mmoItemStack==null || mmoItemStack.mmoItem==null){
				Main.info("Hand seems to be empty");
				return;
			}
			if(this.itemInHand!=mmoItemStack.mmoItem.mmo_item_id){
				Main.info("Hand is wrong");
				return;
			}
		}
		UUID player_uuid = event.getPlayerUUID();
		if(canContribute(player_uuid)){
			this.current_amount+=1;
			save();
			updateCleared();
			if(!this.isCompleted()){
				updateObjective();
			}
		}
		else{
			Main.info("Player cannot contribute");
		}
	}
		
	public boolean updateCleared(){
		if(this.current_amount<this.target_amount){
			return false;
		}
		this.setCompleted(true);
		return true;
	}
	
	@Override
	public String getObjectiveText(){
		String result = getObjectiveHeader();
		result+= createSubtitle("Fortschritt:");
		result+= ChatColor.RESET+String.valueOf(this.current_amount)+"/"+this.target_amount+" "+this.target_name+"\n";
		return result;
	}

	@Override
	public void save() {
		YamlConfiguration saveData = new YamlConfiguration();
		saveData.set("current_amount", this.current_amount);
		try {
			saveData.save(localSaveData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		YamlConfiguration saveData = new YamlConfiguration();
		try {
			saveData.load(localSaveData);
			this.current_amount = saveData.getInt("current_amount");
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
