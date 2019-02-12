package ch.swisssmp.farm;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SoilWorker {
	
	protected static final Material[] soilMaterials = new Material[]{
		Material.DIRT,
		Material.GRASS_BLOCK,
		Material.GRASS_PATH
	};
	
	public static boolean isSoil(Material material){
		for(Material soil : soilMaterials){
			if(soil==material) return true;
		}
		return false;
	}
	
	public static void workGround(Player player, EquipmentSlot hand, Block center, int radius){
		World world = center.getWorld();
		int y = center.getY();
		ItemStack itemStack = hand==EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
		Damageable damageable = (Damageable) itemStack.getItemMeta();
		for(int z = -radius; z <=radius; z++){
			for(int x = -radius; x<=radius; x++){
				Block block = world.getBlockAt(center.getX()+x, y, center.getZ()+z);
				if(!isSoil(block.getType()) || block.getRelative(BlockFace.UP).getType()!=Material.AIR) continue;
				BlockState replacedBlockState = block.getState();
				block.setType(Material.FARMLAND);
				BlockPlaceEvent event = new BlockPlaceEvent(block,replacedBlockState,block.getRelative(BlockFace.DOWN), itemStack, player, true, hand);
				try{
					Bukkit.getPluginManager().callEvent(event);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				if(event.isCancelled() || !event.canBuild()){
					block.setType(replacedBlockState.getType());
					continue;
				}
				
				block.getState().update();
				if(player.getGameMode()==GameMode.CREATIVE || itemStack.getItemMeta().isUnbreakable()) continue;
				damageable.setDamage(damageable.getDamage()+1);
				if(damageable.getDamage()<itemStack.getType().getMaxDurability()) continue;
				itemStack.setAmount(0);
				return;
			}
		}
		itemStack.setItemMeta((ItemMeta) damageable);
	}
}
