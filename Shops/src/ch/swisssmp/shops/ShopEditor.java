package ch.swisssmp.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitTask;

class ShopEditor implements Listener{
	
	private final Shop shop;
	private final Player player;
	private InventoryView inventoryView;
	private final Inventory inventory;
	private final Profession[] professions = new Profession[]{Profession.BLACKSMITH, Profession.BUTCHER, Profession.FARMER, Profession.LIBRARIAN, Profession.NITWIT, Profession.PRIEST};
	private int profession_index;
	
	private boolean waitForInput = false;
	private BukkitTask waitTimeoutTask = null;
	private boolean closed = false;
	
	private ShopEditor(Shop shop, Player player, InventoryView inventoryView){
		this.shop = shop;
		this.player = player;
		this.inventoryView = inventoryView;
		this.inventory = inventoryView.getTopInventory();
		for(int i = 0; i < this.professions.length; i++){
			if(professions[i]==shop.getProfession()){
				this.profession_index = i;
				break;
			}
		}
		Bukkit.getPluginManager().registerEvents(this, ShopManager.plugin);
	}
	protected static ShopEditor open(Shop shop, Player player, InventoryView inventoryView){
		if(player==null || inventoryView==null) return null;
		return new ShopEditor(shop, player, inventoryView);
	}
	
	private void updateShopTrades(ItemStack[] newContents){
		List<MerchantRecipe> newRecipes = new ArrayList<MerchantRecipe>();
		MerchantRecipe recipe;
		ItemStack ingredient;
		for(int i = 0; i < 8; i++){
			ItemStack result = newContents[i];
			if(result==null || result.getType()==Material.AIR){
				if(newContents[i+9]!=null){
					this.player.getWorld().dropItem(player.getLocation(), newContents[i+9]);
				}
				if(newContents[i+18]!=null){
					this.player.getWorld().dropItem(player.getLocation(), newContents[i+18]);
				}
				continue;
			}
			else if(newContents[i+9]==null && newContents[i+18]==null){
				this.player.getWorld().dropItem(player.getLocation(), newContents[i]);
				continue;
			}
			recipe = new MerchantRecipe(result, Integer.MAX_VALUE);
			ingredient = newContents[i+9];
			if(ingredient!=null){
				recipe.addIngredient(ingredient);
			}
			ingredient = newContents[i+18];
			if(ingredient!=null){
				recipe.addIngredient(ingredient);
			}
			if(recipe.getIngredients().size()>0){
				newRecipes.add(recipe);
			}
		}
		this.shop.setRecipes(newRecipes);
	}
	
	protected void close(){
		if(closed) return;
		//Bukkit.getLogger().info("[ShopManager] Schliesse Editor");
		this.closed = true;
		if(this.waitTimeoutTask!=null) this.waitTimeoutTask.cancel();
		HandlerList.unregisterAll(this);
		this.updateShopTrades(this.inventoryView.getTopInventory().getContents());
		this.inventoryView.close();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this.inventoryView){
			return;
		}
		if(event.getClickedInventory()!=this.inventory) return;
		if(event.getSlot()==8){
			//edit profession
			event.setCancelled(true);
			if(event.getClick()==ClickType.LEFT){
				this.profession_index++;
			}
			else if(event.getClick()==ClickType.RIGHT){
				this.profession_index--;
			}
			else{
				return;
			}
			if(this.profession_index>=this.professions.length){
				this.profession_index = 0;
			}
			if(this.profession_index<0){
				this.profession_index = this.professions.length-1;
			}
			this.shop.setProfession(professions[this.profession_index]);
			this.inventory.setItem(8, new ItemStack(Material.WOOL, 1, this.shop.getColor().getWoolData()));
		}
		else if(event.getSlot()==17){
			//edit name
			this.waitForInput = true;
			Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
				public void run(){
					inventoryView.close();
					player.sendMessage("[§dMarktplatz§r] §ENeuen Shop-Namen in Chat eingeben");
				}
			}, 1L);
			waitTimeoutTask = Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
				public void run(){
					waitTimeoutTask = null;
					if(waitForInput) close();
				}
			}, 1200);
			event.setCancelled(true);
		}
		else if(event.getSlot()==26){
			//delete
			if(shop.getChest()!=null){
				//if the shop works with a chest it's not an admin shop and therefore needs to refund its contract
				ItemStack itemStack;
				Location location = event.getView().getPlayer().getEyeLocation();
				for(int i = 0; i < inventory.getSize(); i++){
					if(i==8||i==17||i==26) continue;
					itemStack = inventory.getItem(i);
					if(itemStack==null) continue;
					location.getWorld().dropItem(location, itemStack);
				}
				location.getWorld().dropItem(location, ShopManager.plugin.shopContractBuilder.build());
			}
			shop.delete();
			event.setCancelled(true);
			this.close();
		}
	}
	
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event){
		if(!waitForInput) return;
		if(event.getPlayer()!=this.player) return;
		waitForInput = false;
		event.setCancelled(true);
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				shop.setName("§a"+event.getMessage());
				close();
				shop.openEditor(player, shop.getAgents().length>0 ? shop.getAgents()[0] : null);
			}
		}, 1L);
		if(this.waitTimeoutTask!=null){
			waitTimeoutTask.cancel();
			waitTimeoutTask = null;
		}
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(this.waitForInput || this.closed) return;
		if(event.getView()==this.inventoryView){
			this.close();
		}
	}
}
