package ch.swisssmp.shops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

class Shop {
	private static HashMap<Integer,Shop> shops = new HashMap<Integer, Shop>();
	
	private final int shop_id;
	private String name;
	private String owner_name;
	private UUID owner_uuid;
	private Profession profession;
	private List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
	
	private boolean tradesAvailable;
	private Player currentTradingPartner;
	
	private Shop(ConfigurationSection dataSection){
		this.shop_id = dataSection.getInt("shop_id");
		this.name = dataSection.getString("name");
		this.owner_name = dataSection.getString("owner_name");
		this.owner_uuid = UUID.fromString(dataSection.getString("owner_uuid"));
		this.profession = Profession.valueOf(dataSection.getString("profession"));
		ConfigurationSection trades = dataSection.getConfigurationSection("trades");
		if(trades!=null){
			ConfigurationSection tradeSection;
			for(String key : trades.getKeys(false)){
				try{
					tradeSection = trades.getConfigurationSection(key);
					ItemStack price_1 = tradeSection.contains("price_1") ? tradeSection.getItemStack("price_1") : null;
					ItemStack price_2 = tradeSection.contains("price_2") ? tradeSection.getItemStack("price_2") : null;
					ItemStack result = tradeSection.contains("result") ? tradeSection.getItemStack("result") : null;
					if(price_1!=null && result!=null){
						MerchantRecipe merchantRecipe = new MerchantRecipe(result, Integer.MAX_VALUE);
						List<ItemStack> price = new ArrayList<ItemStack>();
						price.add(price_1);
						if(price_2!=null) price.add(price_2);
						merchantRecipe.setIngredients(price);
						recipes.add(merchantRecipe);
					}
				}
				catch(Exception e){
					System.out.println("[Shops] Konnte Zeile '"+key+"' nicht verarbeiten.");
					e.printStackTrace();
				}
			}
		}
	}
	
	protected String getOwnerName(){
		return this.owner_name;
	}
	
	protected UUID getOwnerUUID(){
		return this.owner_uuid;
	}
	
	protected int getShopId(){
		return this.shop_id;
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected void setName(String name){
		this.name = name;
		this.updateShopSettings();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop reload agents");
	}
	
	protected boolean hasTrades(){
		return this.recipes.size()>0;
	}

	protected Profession getProfession(){
		return this.profession;
	}
	
	protected void setProfession(Profession profession){
		this.profession = profession;
		this.updateShopSettings();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop reload agents");
	}
	
	protected void spawnAgent(Location location){
		ArmorStand armorStand = spawnArmorStand(this.shop_id, location);
		Villager villager = spawnVillager(armorStand);
		this.updateAgent(villager);
	}
	
	protected void updateAgent(Villager villager){
		//Bukkit.getLogger().info("[ShopManager] Shop "+this.name+" aktualisiert Agenten");
		//update recipe counts
		tradesAvailable = this.recipes.size()>0;
		for(int i = 0; i < this.recipes.size(); i++){
			MerchantRecipe recipe = recipes.get(i);
			recipe.setUses(0);
			recipe.setExperienceReward(false);
			recipe.setMaxUses(Integer.MAX_VALUE);
		}
		//update agent configuration
		if(this.tradesAvailable){
			villager.setCustomName(this.name);
		}
		else{
			villager.setCustomName(this.name+"§r (Ausverkauft!)");
		}
		villager.setCustomNameVisible(false);
		villager.setProfession(this.profession);
		villager.setRecipes(this.recipes);
	}
	
	protected DyeColor getColor(){
		switch(this.profession){
		case BLACKSMITH:{
			return DyeColor.GRAY;
		}
		case BUTCHER:{
			return DyeColor.SILVER;
		}
		case FARMER:{
			return DyeColor.BROWN;
		}
		case LIBRARIAN:{
			return DyeColor.WHITE;
		}
		case NITWIT:{
			return DyeColor.GREEN;
		}
		case PRIEST:{
			return DyeColor.PURPLE;
		}
		default:{
			return DyeColor.BROWN;
		}
		}
	}
	
	private void updateShopSettings(){
		DataSource.getResponse("shop/settings.php", new String[]{
			"shop_id="+this.shop_id,
			"name="+URLEncoder.encode(this.name),
			"profession="+this.profession.toString()
		});
	}
	
	@SuppressWarnings("deprecation")
	protected void openEditor(Player player, Villager villager){
		Inventory editor = Bukkit.createInventory(null, 27, "Shop bearbeiten");
		
		ItemStack itemStack;
		ItemMeta itemMeta;
		
		itemStack = new ItemStack(Material.WOOL, 1, this.getColor().getWoolData());
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§aAussehen ändern");
		itemStack.setItemMeta(itemMeta);
		editor.setItem(8, itemStack);
		
		itemStack = new ItemStack(Material.NAME_TAG);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§aName ändern");
		itemStack.setItemMeta(itemMeta);
		editor.setItem(17, itemStack);
		
		itemStack = new ItemStack(Material.BARRIER);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§cHändler entfernen");
		itemStack.setItemMeta(itemMeta);
		editor.setItem(26, itemStack);
		
		for(int i = 0; i < villager.getRecipeCount(); i++){
			MerchantRecipe recipe = villager.getRecipe(i);
			List<ItemStack> ingredients = recipe.getIngredients();
			editor.setItem(i, recipe.getResult());
			editor.setItem(i+9, ingredients.get(0));
			if(ingredients.size()>1){
				editor.setItem(i+18, ingredients.get(1));
			}
		}
		ShopEditor.open(this, villager, player, player.openInventory(editor));
	}
	
	protected void setRecipes(List<MerchantRecipe> recipes, Villager villager){
		this.recipes = recipes;
		try{
			List<String> arguments = new ArrayList<String>();
			for(int i = 0; i < this.recipes.size(); i++){
				MerchantRecipe recipe = this.recipes.get(i);
				List<ItemStack> ingredients = recipe.getIngredients();
				ItemStack result = recipe.getResult();
				ItemStack price_1 = ingredients.get(0);
				ItemStack price_2 = (ingredients.size()>1)?ingredients.get(1):null;
				String resultString = SwissSMPUtils.encodeItemStack(result);
				String price1String = SwissSMPUtils.encodeItemStack(price_1);
				String price2String = SwissSMPUtils.encodeItemStack(price_2);
				if(resultString!=null){
					arguments.add("trades["+i+"][result]="+resultString);
				}
				if(price1String!=null){
					arguments.add("trades["+i+"][price_1]="+price1String);
				}
				if(price2String!=null){
					arguments.add("trades["+i+"][price_2]="+price2String);
				}
			}
			DataSource.getResponse("shop/trades.php", new String[]{
					"shop_id="+this.shop_id,
					String.join("&", arguments)
			});
			//Bukkit.getLogger().info("[ShopManager] Handel vom Shop "+this.getName()+" aktualisiert.");
			this.updateAgent(villager);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void delete(Villager villager){
		DataSource.getResponse("shop/delete.php", new String[]{
				"shop_id="+this.shop_id	
			});
		if(villager.getVehicle()!=null){
			villager.getVehicle().remove();
		}
		villager.remove();
	}
	
	protected void setCurrentTradingPartner(Player player){
		this.currentTradingPartner = player;
	}
	
	protected Player getCurrentTradingPartner(){
		return this.currentTradingPartner;
	}
	
	//static stuff
	
	private static ArmorStand spawnArmorStand(int shop_id, Location location){
		ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX(),location.getY()-0.5, location.getZ()), EntityType.ARMOR_STAND);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setCustomName("§rShop_"+shop_id);
		armorStand.setCustomNameVisible(false);
		return armorStand;
	}
	
	private static Villager spawnVillager(ArmorStand armorStand){
		Villager villager = (Villager)armorStand.getWorld().spawnEntity(armorStand.getLocation(), EntityType.VILLAGER);
		armorStand.addPassenger(villager);
		villager.setInvulnerable(true);
		return villager;
	}
	
	public static Shop create(Player owner, Location location){
		String shopName = "§a"+owner.getName()+"s Shop";
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("shop/create.php", new String[]{
				"player="+owner.getUniqueId(),
				"name="+URLEncoder.encode(shopName)
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("shop")){
			owner.sendMessage("[§dMarktplatz§r] §cBeim Erstellen des Händlers ist ein Fehler aufgetreten.");
			return null;
		}
		Shop shop = new Shop(yamlConfiguration.getConfigurationSection("shop"));
		shops.put(shop.shop_id, shop);
		shop.spawnAgent(location);
		return shop;
	}
	
	protected static Shop get(Villager villager){
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
		return Shop.get(shop_id);
	}
	
	protected static Shop get(int shop_id){
		if(shops.containsKey(shop_id)) return shops.get(shop_id);
		return Shop.load(shop_id);
	}
	
	private static Shop load(int shop_id){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("shop/load.php", new String[]{
				"shop="+shop_id
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("shop")) return null;
		return Shop.load(yamlConfiguration.getConfigurationSection("shop"));
	}
	
	protected static Shop load(ConfigurationSection dataSection){
		int shop_id = dataSection.getInt("shop_id");
		if(shops.containsKey(shop_id)) return shops.get(shop_id);
		Shop shop = new Shop(dataSection);
		shops.put(shop_id,shop);
		return shop;
	}
}
