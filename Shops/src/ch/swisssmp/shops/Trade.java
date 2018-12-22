package ch.swisssmp.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.CurrencyInfo;
import ch.swisssmp.utils.EventPoints;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;

public class Trade {
	
	private final Shop shop;
	private final MerchantRecipe recipe;
	private final Villager villager;
	private final Player player;
	private final InventoryView view;
	
	private int tradeCount = 1;

	ItemStack ingredient_0;
	ItemStack ingredient_1;
	ItemStack result;
	
	private CurrencyInfo ingredient_0_currency;
	private CurrencyInfo ingredient_1_currency;
	private CurrencyInfo result_currency;
	
	protected Trade(Shop shop, MerchantRecipe recipe, Villager villager, Player player, InventoryView view){
		this.shop = shop;
		this.recipe = recipe;
		this.villager = villager;
		this.player = player;
		this.view = view;
		
		this.ingredient_0 = recipe.getIngredients().size()>0 ? recipe.getIngredients().get(0) : null;
		this.ingredient_1 = recipe.getIngredients().size()>1 ? recipe.getIngredients().get(1) : null;
		this.result = recipe.getResult();
		this.ingredient_0_currency = this.getCurrencyInfo(ingredient_0);
		this.ingredient_1_currency = this.getCurrencyInfo(ingredient_1);
		this.result_currency = this.getCurrencyInfo(result);
	}
	
	/**
	 * Amount of times this trade is performed
	 */
	protected void setCount(int count){
		tradeCount = count;
	}
	
	protected boolean isResultCurrency(){
		return this.result_currency!=null;
	}
	
	private CurrencyInfo getCurrencyInfo(ItemStack itemStack){
		String customEnum = (itemStack!=null) ? CustomItems.getCustomEnum(itemStack) : null;
		if(customEnum==null) return null;
		return EventPoints.getInfo(customEnum);
	}
	
	private String getItemDescription(ItemStack itemStack){
		int amount = itemStack.getAmount();
		String typeName = itemStack.getType().name();
		if(itemStack.getItemMeta().hasDisplayName()) typeName = itemStack.getItemMeta().getDisplayName();
		return amount+"x "+typeName+ChatColor.RESET;
	}
	
	private void updateVillager(Villager villager){
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, ()->{
			shop.updateAgent(villager);
		}, 1l);
	}
	
	private void changePlayerCurrency(CurrencyInfo currency, int amount){
		if(amount==0) return;
		DataSource.getResponse("players/change_wallet.php", new String[]{
				"player="+URLEncoder.encode(this.player.getUniqueId().toString()),
				"amount="+(amount),
				"currency="+URLEncoder.encode(currency.getCurrencyType()),
				"reason="+URLEncoder.encode("Kauft "+this.getItemDescription(recipe.getResult())+" bei "+shop.getName()+" von "+shop.getOwnerName())
			});
	}
	
	private void updatePlayerCurrency(){
		if(ingredient_0_currency==null && ingredient_1_currency==null && result_currency==null){
			return; //nothing to do here
		}
		if(ingredient_0_currency!=null){
			this.changePlayerCurrency(ingredient_0_currency, -ingredient_0.getAmount()*this.tradeCount);
		}
		if(ingredient_1_currency!=null){
			this.changePlayerCurrency(ingredient_1_currency, -ingredient_1.getAmount()*this.tradeCount);
		}
		if(result_currency!=null){
			this.changePlayerCurrency(result_currency, result.getAmount()*this.tradeCount);
			Bukkit.getScheduler().runTaskLater(ShopManager.plugin, ()->{
				this.view.setCursor(null);
				Bukkit.dispatchCommand(this.player, "balance "+result_currency.getCurrencyType());
			}, 1L);
		}
	}
	
	protected void perform(){
		this.updatePlayerCurrency();
		this.updateVillager(villager);
	}
}
