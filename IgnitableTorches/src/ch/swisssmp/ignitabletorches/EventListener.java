package ch.swisssmp.ignitabletorches;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener {
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerIgniteTorch(PlayerInteractEvent event){
		Action action = event.getAction();
		if(action!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if(block.getType()!=Material.REDSTONE_TORCH && block.getType()!=Material.REDSTONE_WALL_TORCH) return;
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.ADVENTURE) return;
		@SuppressWarnings("deprecation")
		String gameRuleValue = player.getWorld().getGameRuleValue("ignitableTorches");
		if(gameRuleValue==null || !gameRuleValue.toLowerCase().equals("true")) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null || itemStack.getType()!=Material.FLINT_AND_STEEL) return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta.isUnbreakable() && itemMeta instanceof Damageable && ((Damageable)itemMeta).getDamage()>0) return;
		IgnitableTorches.igniteTorch(block);
	}
}
