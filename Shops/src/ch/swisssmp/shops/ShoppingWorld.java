package ch.swisssmp.shops;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.VectorKey;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

class ShoppingWorld {
	protected HashMap<Integer,Marketplace> marketplaces = new HashMap<Integer,Marketplace>();
	protected HashMap<Integer,Shop> shops = new HashMap<Integer, Shop>();
	protected HashMap<VectorKey,Shop> chestMap = new HashMap<VectorKey,Shop>();
	protected HashMap<VectorKey,Shop> blockMap = new HashMap<VectorKey,Shop>();
	
	private final World world;
	
	private ShoppingWorld(World world){
		this.world = world;
	}
	
	protected World getWorld(){
		return this.world;
	}
	
	protected static ShoppingWorld load(World world){
		ShoppingWorld result = new ShoppingWorld(world);
		result.reloadShops();
		result.reloadMarketplaces();
		return result;
	}
	
	protected Map<Villager,Shop> getAgents(Chunk chunk){
		Map<Villager,Shop> result = new HashMap<Villager,Shop>();
		int shop_id;
		Shop shop;
		ArmorStand armorStand;
		Villager villager;
		for(Entity entity : chunk.getEntities()){
			try{
				if(entity.getType()!=EntityType.ARMOR_STAND) continue;
				armorStand = (ArmorStand) entity;
				if(armorStand.getCustomName()==null) continue;
				if(!armorStand.getCustomName().contains("§rShop_")){
					continue;
				}
				shop_id = Integer.parseInt(armorStand.getCustomName().split("_")[1]);
				shop = this.getShop(shop_id);
				if(shop==null){
					entity.getPassengers().get(0).remove();
					entity.remove();
					continue;
				}
				villager = (Villager)armorStand.getPassengers().get(0);
				result.put(villager, shop);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	protected void reloadShops(){
		shops.clear();
		chestMap.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("shop/shops.php", new String[]{
				"world="+this.world.getName()
		});
		if(yamlConfiguration==null){
			//Bukkit.getLogger().info("[ShopManager] Fehler beim laden der Shops der Welt "+this.world.getName());
			return;
		}
		if(!yamlConfiguration.contains("shops"))return;
		ConfigurationSection chestsSection = yamlConfiguration.getConfigurationSection("shops");
		for(String key : chestsSection.getKeys(false)){
			ConfigurationSection shopSection = chestsSection.getConfigurationSection(key);
			Integer shop_id = shopSection.getInt("shop_id");
			Shop shop = Shop.load(this, shopSection);
			if(shop==null) continue;
			shops.put(shop_id, shop);
		}
	}
	
	protected void reloadMarketplaces(){
		marketplaces.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("shop/marketplaces.php", new String[]{
				"world="+this.world.getName()
		});
		if(yamlConfiguration==null){
			//Bukkit.getLogger().info("[ShopManager] Fehler beim laden der Marktplätze der Welt "+this.world.getName());
			return;
		}
		if(!yamlConfiguration.contains("marketplaces"))return;
		ConfigurationSection marketplacesSection = yamlConfiguration.getConfigurationSection("marketplaces");
		for(String key : marketplacesSection.getKeys(false)){
			ConfigurationSection dataSection = marketplacesSection.getConfigurationSection(key);
			Integer marketplace_id = dataSection.getInt("addon_instance_id");
			Marketplace marketplace = Marketplace.load(this, dataSection);
			if(marketplace==null) continue;
			marketplaces.put(marketplace_id, marketplace);
		}
	}
	
	protected Shop getShop(Chest chest){
		if(chest==null) return null;
		VectorKey vectorKey = new VectorKey(chest.getLocation().toVector());
		return chestMap.get(vectorKey);
	}
	
	protected Shop getShop(Villager villager){
		if(villager.getVehicle()==null){
			return null;
		}
		if(villager.getVehicle().getType()!=EntityType.ARMOR_STAND){
			Bukkit.getLogger().info("[ShopManager] Vehicle ist kein ArmorStand");
			return null;
		}
		ArmorStand armorStand = (ArmorStand) villager.getVehicle();
		if(armorStand.getCustomName()==null){
			Bukkit.getLogger().info("[ShopManager] ArmorStand hat keinen CustomName");
			return null;
		}
		String customName = armorStand.getCustomName();
		if(!customName.contains("§rShop_")){
			Bukkit.getLogger().info("[ShopManager] ArmorStand hat keine gültige Identifikation");
			return null;
		}
		int shop_id = Integer.parseInt(customName.split("_")[1]);
		Bukkit.getLogger().info("[ShopManager] Shop-ID ist "+shop_id);
		ShoppingWorld shoppingWorld = ShoppingWorld.get(armorStand.getWorld());
		return shoppingWorld.getShop(shop_id);
	}
	
	protected Shop getShop(int shop_id){
		return this.shops.get(shop_id);
	}
	
	protected Marketplace getMarketplace(int marketplace_id){
		return this.marketplaces.get(marketplace_id);
	}
	
	protected Marketplace[] getMarketplaces(){
		return this.marketplaces.values().toArray(new Marketplace[this.marketplaces.size()]);
	}
	
	protected static ShoppingWorld get(World world){
		return ShopManager.plugin.worlds.get(world);
	}
	
	protected static ShoppingWorld[] getWorlds(){
		return ShopManager.plugin.worlds.values().toArray(new ShoppingWorld[ShopManager.plugin.worlds.size()]);
	}
}
