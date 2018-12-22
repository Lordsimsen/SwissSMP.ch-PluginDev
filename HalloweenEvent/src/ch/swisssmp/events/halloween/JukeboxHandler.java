package ch.swisssmp.events.halloween;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockVector;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.SwissSMPler;

public class JukeboxHandler {
	protected static void handleJukeboxInteraction(PlayerInteractEvent event){
		Block block = event.getClickedBlock();
		BlockVector blockVector = new BlockVector(block.getX(),block.getY(),block.getZ());
		if(FlashMobBattle.get(blockVector)!=null){
			event.setCancelled(true);
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.LIGHT_PURPLE+"Die Platte wurde vom Plattenspieler verschlungen...");
			return;
		}
		if(event.getItem()==null) return;
		String custom_enum = CustomItems.getCustomEnum(event.getItem());
		if(custom_enum==null || !custom_enum.equals("SPOOKY_DISC")) return;
		World world = event.getClickedBlock().getWorld();
		if(world!=Bukkit.getWorlds().get(0) || world.getTime()<18000){
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.LIGHT_PURPLE+"Nichts passiert...");
			event.setCancelled(true);
			return;
		}
		if(FlashMobBattle.getNearby(block.getLocation())!=null){
			SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.LIGHT_PURPLE+"Bereits in der Nähe eines Festivals.");
			event.setCancelled(true);
			return;
		}
		FlashMobBattle battle = FlashMobBattle.start(event.getPlayer(), block);
		if(battle==null) return;
		event.getItem().setAmount(event.getItem().getAmount()-1);
	}
}
