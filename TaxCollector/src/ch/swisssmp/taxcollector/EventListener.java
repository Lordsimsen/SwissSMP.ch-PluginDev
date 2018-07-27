package ch.swisssmp.taxcollector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{

	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(TaxCollector.plugin,  new Runnable(){
			public void run(){
				if(event.getPlayer().isOnline()){
					TaxCollector.inform_player(new String[]{"player="+event.getPlayer().getUniqueId(), "flags[]=login"}, event.getPlayer());
				}
			}
		}, 600L);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(block.getType()!=Material.CHEST && block.getType()!=Material.TRAPPED_CHEST) return;
		Chest chest = (Chest)block.getState();
		if(chest.getCustomName()==null) return;
		if(!chest.getCustomName().equals("§dAkroma Kiste"))
			return;
		event.setCancelled(true);
		event.getPlayer().sendMessage("[§5AkromaTempel§r] §cDie Opfergabentruhe ist heilig!");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockExplode(BlockExplodeEvent event){
		for(TaxChest chest : TaxCollector.taxChests.values()){
			event.blockList().remove(chest.getBlock());
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockExplode(EntityExplodeEvent event){
		for(TaxChest chest : TaxCollector.taxChests.values()){
			event.blockList().remove(chest.getBlock());
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onInventoryOpen(InventoryOpenEvent event){
		Inventory inventory = event.getInventory();
		InventoryHolder inventoryHolder = inventory.getHolder();
		Chest chest;
		if(inventoryHolder instanceof Chest){
			chest = (Chest) inventoryHolder;
		}
		else if(inventoryHolder instanceof DoubleChest){
			chest = (Chest)((DoubleChest)inventoryHolder).getLeftSide();
		}
		else{
			return;
		}
		
		if(chest.getCustomName()==null){
			return;
		}
		if(!chest.getCustomName().equals("§dAkroma Kiste")){
			return;
		}
		Bukkit.getLogger().info("[TaxCollector] Viewers: "+event.getViewers().size());
		HumanEntity humanEntity = event.getPlayer();
		if(!(humanEntity instanceof Player)){
			return;
		}
		YamlConfiguration response = DataSource.getYamlResponse("taxes/info.php", new String[]{
				"player="+event.getPlayer().getUniqueId(),
				"flags[0]=raw",
				"flags[1]=chest",
				"block[world]="+URLEncoder.encode(chest.getWorld().getName()),
				"block[x]="+chest.getX(),
				"block[y]="+chest.getY(),
				"block[z]="+chest.getZ()
				});
		if(response.contains("data")){
			ConfigurationSection dataSection = response.getConfigurationSection("data");
			TaxInventory taxInventory = TaxInventory.open((Player)humanEntity,event.getInventory(),dataSection);
			if(taxInventory==null){
				event.setCancelled(true);
				SwissSMPler.get((Player)event.getPlayer()).sendActionBar("Die Truhe ist blockiert.");
				return;
			}
			if(taxInventory instanceof PenaltyInventory) event.setCancelled(true);
		}
		else if(response.contains("message")){
			humanEntity.sendMessage(response.getString("message"));
			event.setCancelled(true);
		}
		else{
			SwissSMPler.get((Player)event.getPlayer()).sendActionBar("Die Truhe ist verschlossen.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		if(inventory.getType()!=InventoryType.CHEST) return;
		TaxInventory taxInventory = TaxInventory.get(inventory);
		if(taxInventory==null) return;
		taxInventory.close();
	}
}
