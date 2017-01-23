package ch.swisssmp.adventuredungeons.mmoshop;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmoentity.MmoEntityType;
import ch.swisssmp.adventuredungeons.mmoentity.MmoMerchantAgent;
import ch.swisssmp.adventuredungeons.mmoitem.MmoItemManager;
import ch.swisssmp.adventuredungeons.mmosound.MmoSound;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;
import net.minecraft.server.v1_11_R1.Entity;

public class MmoShop implements Listener{
	public static HashMap<Integer, MmoShop> shops;
	
	public final int mmo_shop_id;
	public final String name;
	public final ArrayList<MerchantRecipe> trades;
	private final ArrayList<MmoMerchantAgent> agents = new ArrayList<MmoMerchantAgent>();
	
	public MmoShop(ConfigurationSection dataSection){
		this.mmo_shop_id = dataSection.getInt("mmo_shop_id");
		this.trades = new ArrayList<MerchantRecipe>();
		this.name = dataSection.getString("name");
		ConfigurationSection tradesSection = dataSection.getConfigurationSection("trades");
		for(String tradeKey : tradesSection.getKeys(false)){
			ConfigurationSection tradeSection = tradesSection.getConfigurationSection(tradeKey);
			ItemStack itemStack = MmoItemManager.getItemFromHybridID(tradeSection);
			if(itemStack==null){
				continue;
			}
			MerchantRecipe recipe = new MerchantRecipe(itemStack, Integer.MAX_VALUE);
			ConfigurationSection pricesSection = tradeSection.getConfigurationSection("price");
			for(String priceKey : pricesSection.getKeys(false)){
				ConfigurationSection priceSection = pricesSection.getConfigurationSection(priceKey);
				ItemStack priceStack = MmoItemManager.getItemFromHybridID(priceSection);
				if(priceStack==null){
					continue;
				}
				recipe.addIngredient(priceStack);
			}
			if(recipe.getIngredients().size()<1)
				continue;
			trades.add(recipe);
		}
		shops.put(mmo_shop_id, this);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}
	
	public void open(Player player){
		MmoMerchantAgent agent = new MmoMerchantAgent(this, player);
		MmoEntityType.spawnEntity(agent, player.getLocation().add(0, -2, 0));
		agents.add(agent);
		player.openMerchant((Villager)agent.getBukkitEntity(), true);
		MmoSound.play(player, 3);
	}
	
	@EventHandler
	private void onShopClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		if(!(inventory instanceof MerchantInventory)){
			return;
		}
		MerchantInventory merchantInventory = (MerchantInventory) inventory;
		InventoryHolder holder = merchantInventory.getHolder();
		if(!(holder instanceof Villager)){
			return;
		}
		Villager villager = (Villager)holder;
		Entity entity = ((CraftEntity)villager).getHandle();
		if(!(entity instanceof MmoMerchantAgent)){
			return;
		}
		MmoMerchantAgent agent = (MmoMerchantAgent)entity;
		if(!agents.contains(agent)){
			return;
		}
		agents.remove(agent);
		agent.remove();
	}
	
	public static void loadShops() throws Exception{
		shops = new HashMap<Integer, MmoShop>();
		
		YamlConfiguration mmoShopsConfiguration = MmoResourceManager.getYamlResponse("shops.php");
		for(String shopIDstring : mmoShopsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoShopsConfiguration.getConfigurationSection(shopIDstring);
			new MmoShop(dataSection);
		}
	}
	
	public static MmoShop get(int mmo_shop_id){
		return shops.get(mmo_shop_id);
	}
}
