package ch.swisssmp.craftmmo.mmoevent;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoblock.MmoBlock;
import ch.swisssmp.craftmmo.mmoworld.MmoWorld;
import ch.swisssmp.craftmmo.mmoworld.MmoWorldInstance;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoEventListenerBlock extends MmoEventListener{
	public MmoEventListenerBlock(JavaPlugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()==GameMode.CREATIVE){
			return;
		}
		Main.info("Block has been broken!");
		Block block = event.getBlock();
		String action = "onbreak";
		MmoWorldInstance worldInstance = MmoWorld.getInstance(block);
		MmoResourceManager.processYamlResponse(player.getUniqueId(), "treasure.php", new String[]{
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
