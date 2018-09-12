package ch.swisssmp.dungeongenerator;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.YamlConfiguration;

public class EventListener implements Listener{
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) return;
		if(event.getItem()==null) return;
		int generator_id = ItemUtil.getInt(event.getItem(), "generator_id");
		if(generator_id==0) return;
		GeneratorManager manager = GeneratorManager.get(event.getPlayer().getWorld());
		if(manager==null) return;
		DungeonGenerator generator = manager.get(generator_id);
		if(generator==null) return;
		if(event.getAction()==Action.LEFT_CLICK_BLOCK){
			if(event.getClickedBlock().getType()!=generator.getBoundingBoxMaterial() && event.getClickedBlock().getType()!=generator.getGenerationBoxMaterial()) return;
			event.setCancelled(true);
			Block gridOrigin = GeneratorUtil.getGridOrigin(event.getClickedBlock(), event.getClickedBlock().getType(), generator.getPartSizeXZ(),generator.getPartSizeY());
			if(gridOrigin.getType()==generator.getBoundingBoxMaterial()){
				generator.setTemplatePosition(new BlockVector(gridOrigin.getX()+1,gridOrigin.getY()+1,gridOrigin.getZ()+1));
			}
			else{
				generator.setGenerationPosition(new BlockVector(gridOrigin.getX(),gridOrigin.getY(),gridOrigin.getZ()));
			}
		}
		else if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType()!=generator.getBoundingBoxMaterial()) return;
			YamlConfiguration yamlConfiguration = PartConfigurationUtil.getPartConfiguration(event.getClickedBlock());
			Player player = event.getPlayer();
			for(String key : yamlConfiguration.getKeys(false)){
				player.sendMessage(key+": "+yamlConfiguration.getString(key));
			}
		}
	}
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		GeneratorManager.get(event.getWorld());
	}
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		GeneratorManager manager = GeneratorManager.get(event.getWorld());
		if(manager!=null){
			manager.saveAll();
			manager.unload();
		}
	}
	@EventHandler
	private void onItemRename(PlayerRenameItemEvent event){
		int generator_id = ItemUtil.getInt(event.getItemStack(), "generator_id");
		if(generator_id==0) return;
		GeneratorManager manager = GeneratorManager.get(event.getPlayer().getWorld());
		if(manager==null) return;
		DungeonGenerator generator = manager.get(generator_id);
		generator.setName(event.getNewName());
		manager.saveAll();
		event.setName(ChatColor.LIGHT_PURPLE+event.getNewName());
	}
}
