package ch.swisssmp.adventuredungeons.event;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.block.MmoBlock;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;
import ch.swisssmp.adventuredungeons.world.AdventureWorld;
import ch.swisssmp.adventuredungeons.world.AdventureWorldInstance;

public class MmoEventListenerBlock extends MmoEventListener{
	public MmoEventListenerBlock(JavaPlugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE){
			return;
		}
		AdventureDungeons.info("Block has been broken!");
		Block block = event.getBlock();
		String action = "onbreak";
		AdventureWorldInstance worldInstance = AdventureWorld.getInstance(block);
		MmoResourceManager.processYamlResponse(player.getUniqueId(), "adventure/treasure.php", new String[]{
				"player="+event.getPlayer().getUniqueId().toString(),
				"mc_enum="+MmoBlock.getMaterialString(block, true),
				"action="+action,
				"x="+block.getX(),
				"y="+block.getY(),
				"z="+block.getZ(),
				"world="+worldInstance.system_name,
				"world_instance="+worldInstance.world.getName()
				});
		MaterialData originalType = block.getState().getData();
		MaterialData minedType = MmoBlock.getMinedType(block.getState().getData());
		if(minedType!=null){
			MmoBlock.set(block, minedType, player.getUniqueId());
			event.setCancelled(true);
			block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, originalType);
		}
		
	}
}
