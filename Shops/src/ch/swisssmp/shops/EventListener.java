package ch.swisssmp.shops;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.CurrencyInfo;
import ch.swisssmp.utils.EventPoints;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{
	@EventHandler
	private void onTrade(InventoryClickEvent event){
		if(event.getView().getType()!=InventoryType.MERCHANT) return;
		MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
		if((event.getClickedInventory() instanceof MerchantInventory) && (event.getSlot()==0 || event.getSlot()==1)){
			//prevent picking out currency items
			ItemStack itemStack = merchantInventory.getItem(event.getSlot());
			if(itemStack==null) return;
			String customEnum = CustomItems.getCustomEnum(itemStack);
			CurrencyInfo currencyInfo = (customEnum!=null) ? EventPoints.getInfo(customEnum) : null;
			if(currencyInfo==null) return;
			event.setCancelled(true);
			return;
		}
		if(event.getSlotType() != SlotType.RESULT) return;
		InventoryHolder holder = merchantInventory.getHolder();
		if(holder==null) return;
		if(!(holder instanceof Villager)) return;
		Villager villager = (Villager) holder;
		Shop shop = Shop.get(villager);
		if(shop==null) return;
		if(merchantInventory.getItem(2)==null) return;
		MerchantRecipe recipe = merchantInventory.getSelectedRecipe();
		Trade trade = new Trade(shop, recipe, villager, (Player) event.getWhoClicked(), event.getView());
		if(trade.isResultCurrency() && event.isShiftClick()){
			event.setCancelled(true); //prevent player from making a mistake
			return;
		}
		int playerCanBuy = ShopUtil.countTradeBuyerSide(recipe, merchantInventory.getContents());
		trade.setCount(event.isShiftClick() ? playerCanBuy : 1);
		trade.perform();
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(event.getRightClicked().getType()!=EntityType.VILLAGER){
			//Bukkit.getLogger().info("[ShopManager] Kein Villager");
			return;
		}
		Villager villager = (Villager)event.getRightClicked();
		Shop shop = Shop.get(villager);
		if(shop==null){
			//Bukkit.getLogger().info("[ShopManager] Kein Shop");
			return;
		}
		if(event.getPlayer().isSneaking()){
			//Bukkit.getLogger().info("[ShopManager] Spieler schleicht");
			if(shop.getOwnerUUID().equals(event.getPlayer().getUniqueId()) || event.getPlayer().hasPermission("shop.admin")){
				event.setCancelled(true);
				shop.openEditor(event.getPlayer(), villager);
				//Bukkit.getLogger().info("[ShopManager] Öffne Editor");
			}
			else{
				event.setCancelled(true);
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
			}
		}
		else{
			if(event.getHand()!=EquipmentSlot.HAND){
				event.setCancelled(true);
			}
			if(shop.getCurrentTradingPartner()==null || shop.getCurrentTradingPartner() == event.getPlayer()){
				shop.updateAgent(villager);
				if(shop.hasTrades()){
					Entity vehicle = villager.getVehicle();
					Location location = vehicle.getLocation();
					Location playerLocation = event.getPlayer().getLocation();
					//location = location.setDirection(direction);
					float angle = (float) Math.toDegrees(Math.atan2(playerLocation.getZ() - location.getZ(), playerLocation.getX() - location.getX()))-90;
					if(angle<0) angle+=360;
					location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), angle, 0);
					vehicle.eject();
					vehicle.teleport(location);
					vehicle.addPassenger(villager);
					shop.setCurrentTradingPartner(event.getPlayer());
				}
			}
			else{
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cShop besetzt");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		if(event.getInventory().getType()!=InventoryType.MERCHANT) return;
		MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
		if(merchantInventory.getHolder()==null){
			return;
		}
		Villager villager = (Villager) merchantInventory.getHolder();
		if(villager.getRecipeCount()==0) return;
		MerchantRecipe recipe = villager.getRecipe(0);
		ItemStack itemStack = recipe.getIngredients().get(0);
		String customEnum = CustomItems.getCustomEnum(itemStack);
		if(customEnum==null) return;
		CurrencyInfo currencyInfo = EventPoints.getInfo(customEnum);
		if(currencyInfo==null || !currencyInfo.getCurrencyType().equals(customEnum)) return;
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/balance.php", new String[]{
			"player="+URLEncoder.encode(event.getPlayer().getUniqueId().toString()),
			"currency="+URLEncoder.encode(currencyInfo.getCurrencyType())
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("balance")) return;
		int balance = Math.min(64, yamlConfiguration.getInt("balance"));
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				ItemStack eventPoints = currencyInfo.getItem(balance);
				merchantInventory.setItem(0, eventPoints);
			}
		}, 1L);
	}
	
	@EventHandler
	private void onPrepareItemCraft(PrepareItemCraftEvent event){
		if(event.getRecipe()==null) return;
		Recipe recipe = event.getRecipe();
		if(recipe.getResult()==null) return;
		ItemStack result = recipe.getResult();
		if(!ShopManager.isShopContract(result)) return;
		if(!event.getView().getPlayer().hasPermission("shop.craft")) event.getInventory().setResult(null);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		if(inventory.getType() != InventoryType.MERCHANT){
			return;
		}
		String customEnum;
		CurrencyInfo currencyInfo;
		for(int i = 0; i < 2; i++){
			ItemStack itemStack = inventory.getItem(i);
			if(itemStack==null) continue;
			customEnum = CustomItems.getCustomEnum(itemStack);
			if(customEnum==null) continue;
			currencyInfo = EventPoints.getInfo(customEnum);
			if(currencyInfo==null || !currencyInfo.getCurrencyType().equals(customEnum)) continue;
			inventory.setItem(i, null);
		}
		Villager villager = (Villager) inventory.getHolder();
		Shop shop = Shop.get(villager);
		if(shop==null) return;
		shop.setCurrentTradingPartner(null);
	}
}
