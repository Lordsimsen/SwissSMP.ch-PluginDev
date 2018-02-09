package ch.swisssmp.shops;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.SwissSMPler;

class ShopCreator implements Listener{
	
	protected final static HashMap<Player,ShopCreator> creatorMap = new HashMap<Player,ShopCreator>();
	
	private final SwissSMPler swissSMPler;
	private final Player player;
	private int marketplace_id;
	private Chest chest;
	private final ItemStack item;
	
	private ShopCreator(Player player, int marketplace_id, Chest chest, ItemStack itemStack){
		this.swissSMPler = SwissSMPler.get(player);
		this.player = player;
		this.marketplace_id = marketplace_id;
		this.chest = chest;
		this.item = itemStack;
		creatorMap.put(player, this);
		Bukkit.getPluginManager().registerEvents(this, ShopManager.plugin);
		//Bukkit.getLogger().info("[ShopManager] ShopCreator initiiert.");
	}
	
	protected static ShopCreator initiate(Player player, int marketplace_id, Chest chest, ItemStack itemStack){
		if(player==null || chest==null || itemStack==null) return null;
		return new ShopCreator(player, marketplace_id, chest, itemStack);
	}
	
	protected void setMarketplaceId(int marketplace_id){
		this.marketplace_id = marketplace_id;
	}
	
	protected void setChest(Chest chest){
		//Bukkit.getLogger().info("[ShopManager] ShopCreator initiiert.");
		this.chest = chest;
	}
	
	protected void end(){
		//Bukkit.getLogger().info("[ShopManager] ShopCreator beendet.");
		HandlerList.unregisterAll(this);
		creatorMap.remove(this.player);
	}
	
	private void create(Location location){
		//Bukkit.getLogger().info("[ShopManager] Shop erstellt.");
		Shop.create(this.player, this.marketplace_id, this.chest, location);
		this.item.setAmount(0);
		this.end();
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		if(event.getPlayer()==this.player) this.end();
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK){
			//Bukkit.getLogger().info("[ShopManager] Falsche Aktion.");
			return;
		}
		if(event.getPlayer()!=this.player){
			//Bukkit.getLogger().info("[ShopManager] Falscher Spieler.");
			return;
		}
		if(!ShopManager.isShopContract(event.getItem())){
			//Bukkit.getLogger().info("[ShopManager] Falsches Item.");
			return;
		}
		Block block = event.getClickedBlock();
		if(!block.getType().isSolid()){
			//Bukkit.getLogger().info("[ShopManager] Kein solider Block.");
			return;
		}
		if(block.getLocation().distanceSquared(chest.getLocation())>9){
			swissSMPler.sendActionBar("§cZu weit weg.");
			return;
		}
		ShoppingWorld shoppingWorld = ShoppingWorld.get(event.getPlayer().getWorld());
		Marketplace marketplace = shoppingWorld.getMarketplace(marketplace_id);
		if(marketplace==null){
			swissSMPler.sendActionBar("§cEin Fehler ist aufgetreten. ("+marketplace_id+")");
			return;
		}
		if(event.getClickedBlock().getType()!=marketplace.getAgentMarker()){
			swissSMPler.sendActionBar("§cKeine Shop-Markierung ("+marketplace.getAgentMarker().name()+")");
			return;
		}
		this.create(block.getLocation().add(0.5, 0, 0.5));
	}
}
