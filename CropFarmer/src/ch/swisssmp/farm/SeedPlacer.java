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
import org.bukkit.inventory.PlayerInventory;

public class SeedPlacer {
	
	protected static final Material[] cropMaterials = new Material[]{
		Material.WHEAT,
		Material.BEETROOTS,
		Material.CARROTS,
		Material.POTATOES
	};
	
	public static boolean isCrop(Material material){
		for(Material crop : cropMaterials){
			if(crop==material) return true;
		}
		return false;
	}
	
	public static void placeSeeds(Player player, EquipmentSlot hand, Block center, int radius){
		World world = center.getWorld();
		int y = center.getY();
		ItemStack itemStack = hand==EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
		if(itemStack==null) return;
		Material material = itemStack.getType();
		int amount = getAmount(player.getInventory(),material)-1;
		ItemStack use = new ItemStack(material,1);
		for(int z = -radius; z <=radius && amount>0; z++){
			for(int x = -radius; x<=radius && amount>0; x++){
				if(x==0 && z==0) continue;
				Block block = world.getBlockAt(center.getX()+x, y, center.getZ()+z);
				if(block.getRelative(BlockFace.DOWN).getType()!=Material.FARMLAND) continue;
				BlockState replacedBlockState = block.getState();
				block.setBlockData(center.getBlockData());
				BlockPlaceEvent event = new BlockPlaceEvent(block,replacedBlockState,block.getRelative(BlockFace.DOWN), null, player, true, hand);
				try{
					Bukkit.getPluginManager().callEvent(event);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				if(event.isCancelled() || !event.canBuild()){
					block.setBlockData(replacedBlockState.getBlockData());
					continue;
				}
				block.setBlockData(center.getBlockData());
				if(player.getGameMode()==GameMode.CREATIVE) continue;
				amount--;
				player.getInventory().removeItem(use);
				if(amount>0) continue;
				break;
			}
		}
	}
	
	private static int getAmount(PlayerInventory inventory, Material material){
		int result = 0;
		for(ItemStack itemStack : inventory.all(material).values()){
			result+=itemStack.getAmount();
		}
		return result;
	}
}
