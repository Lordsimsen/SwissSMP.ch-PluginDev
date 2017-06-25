package ch.swisssmp.adventuredungeons.mmoblock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoevent.MmoAction;
import ch.swisssmp.adventuredungeons.mmoevent.MmoActionEvent;
import ch.swisssmp.adventuredungeons.mmoevent.MmoBlockChangeEvent;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class MmoBlockScheduler implements Listener{
	private final File blocksFile;
	public final World world;
	public final HashMap<Location, MmoScheduledBlock> blocks = new HashMap<Location, MmoScheduledBlock>();
	
	public MmoBlockScheduler(MmoWorldInstance worldInstance){
		this.blocksFile = new File(worldInstance.getDataFolder(), "blocks.yml");
		this.world = worldInstance.world;
		if(blocksFile.exists()){
			YamlConfiguration blocksData;
			try {
				blocksData = YamlConfiguration.loadConfiguration(blocksFile);
				for(String key : blocksData.getKeys(false)){
					ConfigurationSection dataSection = blocksData.getConfigurationSection(key);
					MmoScheduledBlock.load(this, dataSection);
				}
			} catch (IOException e) {
				Main.info("Block-Daten konnten nicht geladen werden.");
				e.printStackTrace();
			}
		}
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}
	
	public static MmoScheduledBlock schedule(Block block, MaterialData targetData, int time, UUID player_uuid){
		MmoWorldInstance worldInstance = MmoWorld.getInstance(block);
		if(worldInstance==null) return null;
		return MmoScheduledBlock.create(worldInstance.blockScheduler, block.getLocation(), targetData, time, player_uuid);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onWorldSave(WorldSaveEvent event){
		this.saveAll();
	}
	@EventHandler(ignoreCancelled=true)
	private void onBlockChange(MmoBlockChangeEvent event){
		if(event.block.getType()==Material.REDSTONE_TORCH_ON && event.targetData.getItemType()==Material.TORCH){
			Player player = Bukkit.getPlayer(event.player_uuid);
			ItemStack itemStack = null;
			if(player!=null){
				itemStack = player.getInventory().getItemInMainHand();
			}
			MmoActionEvent actionEvent = new MmoActionEvent(MmoAction.TORCH_ON, event.player_uuid, itemStack, event.block);
			Bukkit.getPluginManager().callEvent(actionEvent);
		}
		else if(event.block.getType()==Material.TORCH && event.targetData.getItemType()==Material.REDSTONE_TORCH_ON){
			MmoActionEvent actionEvent = new MmoActionEvent(MmoAction.TORCH_OFF, event.player_uuid, null, event.block);
			Bukkit.getPluginManager().callEvent(actionEvent);
		}
	}
	
	public void saveAll(){
		Main.info("Saving scheduled blocks");
		YamlConfiguration blocksData = new YamlConfiguration();
		int index = 0;
		for(MmoScheduledBlock scheduledBlock : this.blocks.values()){
			ConfigurationSection scheduledSection = blocksData.createSection("block_"+String.valueOf(index));
			scheduledBlock.save(scheduledSection);
			index++;
		}
		blocksData.save(blocksFile);
	}
}
