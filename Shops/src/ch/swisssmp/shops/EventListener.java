package ch.swisssmp.shops;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.event.NPCEditorOpenEvent;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.shops.editor.ShopEditor;
import ch.swisssmp.utils.CurrencyInfo;
import ch.swisssmp.utils.EventPoints;

public class EventListener implements Listener{
	
	@EventHandler
	private void onVillagerAcquireTrade(VillagerAcquireTradeEvent event){
		NPCInstance npc = NPCInstance.get(event.getEntity());
		if(npc==null) return;
		Shop shop = Shop.get(npc);
		if(shop==null) return;
		event.setCancelled(true);
	}
	
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
		Trade trade = new Trade(shop, recipe, (Player) event.getWhoClicked(), event.getView());
		if(trade.isIngredientCurrency() && event.isShiftClick()){
			event.setCancelled(true); //prevent player from making a mistake
			return;
		}
		if(event.getClick()!=ClickType.LEFT && event.getClick()!=ClickType.RIGHT){
			event.setCancelled(true);
			return;
		}
		int playerCanBuy = ShopUtil.countTradeBuyerSide(recipe, merchantInventory.getContents());
		trade.setCount(event.isShiftClick() ? playerCanBuy : 1);
		trade.perform();
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractNPCEvent event){
		if(event.getHand()!=EquipmentSlot.HAND){
			event.setCancelled(true);
		}
		if(event.getNPC().getEntity().getType()!=EntityType.VILLAGER){
			//Bukkit.getLogger().info("[ShopManager] Kein Villager");
			return;
		}
		Shop shop = Shop.get(event.getNPC());
		if(shop==null){
			//Bukkit.getLogger().info("[ShopManager] Kein Shop");
			return;
		}
		if(event.getPlayer().isSneaking() && event.getPlayer().hasPermission("shop.admin")){
			return;
		}

		event.setCancelled(true);
		event.setPreventDefault(false);
	}
	
	@EventHandler
	private void onNPCEditor(NPCEditorOpenEvent event){
		Shop shop = Shop.get(event.getNPC());
		if(shop==null) return;
		event.getEditors().add(new ShopEditor(event.getView(), shop));
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
		if(customEnum==null){
			return;
		}
		CurrencyInfo currencyInfo = EventPoints.getInfo(customEnum);
		if(currencyInfo==null || !currencyInfo.getCurrencyType().equals(customEnum)){
			return;
		}
		int balance = EventPoints.getBalance(event.getPlayer().getUniqueId().toString(), currencyInfo.getCurrencyType());
		int displayBalance = Math.min(64, balance);
		Bukkit.getScheduler().runTaskLater(ShopsPlugin.plugin, new Runnable(){
			public void run(){
				ItemStack eventPoints = currencyInfo.getItem(displayBalance);
				merchantInventory.setItem(0, eventPoints);
			}
		}, 1L);
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
	}
}
