package ch.swisssmp.shops;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class ShopTradeEvent extends InventoryInteractEvent{

	private static final HandlerList handlers = new HandlerList();
	
	private final Shop shop;
	private final MerchantRecipe recipe;
	private ItemStack product;
	
	public ShopTradeEvent(InventoryView transaction, Shop shop, MerchantRecipe recipe, ItemStack product) {
		super(transaction);
		this.shop = shop;
		this.recipe = recipe;
		this.product = product;
	}

	public Shop getShop() {
		return shop;
	}
	
	public MerchantRecipe getRecipe() {
		return recipe;
	}
	
	public ItemStack getProduct() {
		return product;
	}
	
	public void setProduct(ItemStack product) {
		this.product = product;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
