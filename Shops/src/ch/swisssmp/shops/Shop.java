package ch.swisssmp.shops;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.VectorKey;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

class Shop {
	
	private final ShoppingWorld shoppingWorld;
	private final int marketplace_id;
	private final int shop_id;
	private String name;
	private String owner_name;
	private UUID owner_uuid;
	private Profession profession;
	private final List<Villager> agents;
	private List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
	
	private boolean tradesAvailable;
	private Player currentTradingPartner;
	
	//location
	private int location_x;
	private int location_y;
	private int location_z;
	
	//agents
	private List<Chunk> agentChunks;
	
	//chest
	private int chest_x;
	private int chest_y;
	private int chest_z;
	
	private Shop(ShoppingWorld shoppingWorld, ConfigurationSection dataSection){
		this.shoppingWorld = shoppingWorld;
		this.marketplace_id = dataSection.getInt("marketplace_id");
		this.shop_id = dataSection.getInt("shop_id");
		this.name = dataSection.getString("name");
		this.owner_name = dataSection.getString("owner_name");
		this.owner_uuid = UUID.fromString(dataSection.getString("owner_uuid"));
		this.profession = Profession.valueOf(dataSection.getString("profession"));
		List<String> trades = dataSection.getStringList("trades");
		if(trades!=null){
			for(String tradeData : trades){
				org.bukkit.configuration.file.YamlConfiguration tradeConfiguration = new org.bukkit.configuration.file.YamlConfiguration();
				try{
					tradeConfiguration.loadFromString(new String(java.util.Base64.getDecoder().decode(tradeData)));
				}
				catch(Exception e){
					e.printStackTrace();
					continue;
				}
				ItemStack price_1 = null;
				ItemStack price_2 = null;
				ItemStack result = null;
				if(tradeConfiguration.contains("price_1")) price_1 = tradeConfiguration.getItemStack("price_1");
				if(tradeConfiguration.contains("price_2")) price_2 = tradeConfiguration.getItemStack("price_2");
				if(tradeConfiguration.contains("result")) result = tradeConfiguration.getItemStack("result");
				if(price_1!=null && result!=null){
					MerchantRecipe trade = new MerchantRecipe(result, Integer.MAX_VALUE);
					List<ItemStack> price = new ArrayList<ItemStack>();
					price.add(price_1);
					if(price_2!=null) price.add(price_2);
					trade.setIngredients(price);
					recipes.add(trade);
				}
			}
		}
		this.agents = new ArrayList<Villager>();
		this.agentChunks = new ArrayList<Chunk>();
		if(dataSection.contains("agents")){
			World world = shoppingWorld.getWorld();
			Location location;
			ConfigurationSection agentsSection = dataSection.getConfigurationSection("agents");
			for(String key : agentsSection.getKeys(false)){
				ConfigurationSection agentSection = agentsSection.getConfigurationSection(key);
				location = new Location(world, agentSection.getInt("x"), agentSection.getInt("y"), agentSection.getInt("z"));
				if(!this.agentChunks.contains(location.getChunk())){
					this.agentChunks.add(location.getChunk());
				}
			}
		}
		this.location_x = dataSection.getInt("location_x");
		this.location_y = dataSection.getInt("location_y");
		this.location_z = dataSection.getInt("location_z");
		//register content chest
		if(dataSection.contains("chest")){
			ConfigurationSection chestSection = dataSection.getConfigurationSection("chest");
			this.chest_x = chestSection.getInt("x");
			this.chest_y = chestSection.getInt("y");
			this.chest_z = chestSection.getInt("z");
			
			Block block = shoppingWorld.getWorld().getBlockAt(chest_x,chest_y,chest_z);
			if(block.getState() instanceof Chest){
				shoppingWorld.chestMap.put(new VectorKey(new Vector(this.chest_x, this.chest_y, this.chest_z)), this);
				BlockFace[] neighbours = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
				for(BlockFace neighbourFace : neighbours){
					if(block.getRelative(neighbourFace).getType()==block.getType()){
						shoppingWorld.chestMap.put(new VectorKey(block.getRelative(neighbourFace).getLocation().toVector()), this);
					}
				}
			}
			this.shoppingWorld.blockMap.put(new VectorKey(new Vector(this.location_x,this.location_y,this.location_z)), this);
		}
		Chest chest = this.getChest();
		if(chest!=null) chest.setCustomName(this.name);
		
		this.updateAgents();
	}
	
	protected String getOwnerName(){
		return this.owner_name;
	}
	
	protected UUID getOwnerUUID(){
		return this.owner_uuid;
	}
	
	protected Location getLocation(){
		int y = this.chest_y;
		if(y==0) y = 64;
		return new Location(this.shoppingWorld.getWorld(), this.chest_x, y, this.chest_z);
	}
	
	protected Chest getChest(){
		if(this.chest_y==0) return null;
		Block block = this.shoppingWorld.getWorld().getBlockAt(this.chest_x,this.chest_y,this.chest_z);
		if(block==null) return null;
		if(!(block.getState() instanceof Chest)) 
			return null;
		return (Chest) block.getState();
	}
	
	protected int getShopId(){
		return this.shop_id;
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected void setName(String name){
		this.name = name;
		Chest chest = this.getChest();
		if(chest!=null){
			Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
				public void run(){
					chest.setCustomName(name);
				}
			}, 1L);
			
		}
		this.updateShopSettings();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop reload agents");
	}
	
	protected int getMarketplaceId(){
		return this.marketplace_id;
	}
	
	protected Marketplace getMarketplace(){
		return this.shoppingWorld.getMarketplace(this.marketplace_id);
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
	
	protected Villager[] getAgents(){
		return this.agents.toArray(new Villager[this.agents.size()]);
	}
	
	protected void spawnAgent(Location location){
		ArmorStand armorStand = spawnArmorStand(this.shop_id, location);
		spawnVillager(armorStand);
		DataSource.getResponse("shop/spawn_agent.php", new String[]{
			"shop_id="+this.shop_id,
			"location[x]="+location.getBlockX(),
			"location[y]="+(location.getBlockY()+1),
			"location[z]="+location.getBlockZ()
		});
		this.updateAgents();
	}
	
	protected void removeAgent(Villager villager){
		if(villager==null) return;
		Location location = villager.getLocation();
		DataSource.getResponse("shop/spawn_agent.php", new String[]{
				"shop_id="+this.shop_id,
				"location[x]="+location.getBlockX(),
				"location[y]="+location.getBlockY(),
				"location[z]="+location.getBlockZ()
			});
		if(villager.getVehicle()!=null){
			villager.getVehicle().remove();
		}
		villager.remove();
		this.findAgents();
	}
	
	private void findAgents(){
		this.agents.clear();
		for(Chunk chunk : this.agentChunks){
			//Bukkit.getLogger().info("Checking chunk "+chunk.getX()+","+chunk.getZ());
			if(!chunk.isLoaded()) chunk.load();
			for(Entity entity : chunk.getEntities()){
				if(entity.getType()!=EntityType.ARMOR_STAND) continue;
				ArmorStand armorStand = (ArmorStand) entity;
				if(armorStand.getCustomName()==null) continue;
				if(armorStand.getCustomName().equals("§rShop_"+this.shop_id)){
					if(armorStand.getPassengers().size()==0){
						armorStand.remove();
						continue;
					}
					Villager villager = (Villager) armorStand.getPassengers().get(0);
					if(!this.agents.contains(villager))
						this.agents.add(villager);
				}
			}
		}
	}
	
	protected void updateAgents(){
		this.findAgents();
		//Bukkit.getLogger().info("[ShopManager] Shop "+this.name+" aktualisiert Agenten");
		//update recipe counts
		Chest chest = this.getChest();
		if(chest!=null){
			Inventory inventory = chest.getInventory();
			tradesAvailable = false;
			for(int i = 0; i < this.recipes.size(); i++){
				MerchantRecipe recipe = recipes.get(i);
				recipe.setUses(0);
				recipe.setExperienceReward(false);
				recipe.setMaxUses(ShopUtil.countTradeOwnerSide(recipe, inventory));
				if(recipe.getMaxUses()>0){
					tradesAvailable = true;
				}
			}
		}
		else{
			tradesAvailable = this.recipes.size()>0;
			for(int i = 0; i < this.recipes.size(); i++){
				MerchantRecipe recipe = recipes.get(i);
				recipe.setUses(0);
				recipe.setExperienceReward(false);
				recipe.setMaxUses(Integer.MAX_VALUE);
			}
		}
		//update agent configuration
		for(Villager villager : this.agents){
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
		try{
			DataSource.getResponse("shop/settings.php", new String[]{
				"shop_id="+this.shop_id,
				"name="+URLEncoder.encode(this.name, "utf-8"),
				"profession="+this.profession.toString()
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void openEditor(Player player){
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
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§EVertrag wird zurückerstattet.");
		tooltip.add("§7Shop und zugehörige");
		tooltip.add("§7Vermittler in anderen");
		tooltip.add("§7Städten werden entfernt.");
		itemMeta.setLore(tooltip);
		tooltip = null;
		itemStack.setItemMeta(itemMeta);
		editor.setItem(26, itemStack);
		
		for(int i = 0; i < this.recipes.size(); i++){
			MerchantRecipe recipe = this.recipes.get(i);
			List<ItemStack> ingredients = recipe.getIngredients();
			editor.setItem(i, recipe.getResult());
			editor.setItem(i+9, ingredients.get(0));
			if(ingredients.size()>1){
				editor.setItem(i+18, ingredients.get(1));
			}
		}
		ShopEditor.open(this, player, player.openInventory(editor));
	}
	
	protected void setRecipes(List<MerchantRecipe> recipes){
		this.recipes = recipes;
		try{
			List<String> arguments = new ArrayList<String>();
			org.bukkit.configuration.file.YamlConfiguration yamlConfiguration;
			for(int i = 0; i < this.recipes.size(); i++){
				MerchantRecipe recipe = this.recipes.get(i);
				List<ItemStack> ingredients = recipe.getIngredients();
				ItemStack result = recipe.getResult();
				ItemStack price_1 = ingredients.get(0);
				ItemStack price_2 = (ingredients.size()>1)?ingredients.get(1):null;
				yamlConfiguration = new org.bukkit.configuration.file.YamlConfiguration();
				yamlConfiguration.set("result", result);
				yamlConfiguration.set("price_1", price_1);
				if(price_2!=null){
					yamlConfiguration.set("price_2", price_2);
				}
				arguments.add("trades["+i+"]="+URLEncoder.encode(yamlConfiguration.saveToString(), "utf-8"));
				arguments.add("trades_display["+i+"][result][material]="+result.getType());
				arguments.add("trades_display["+i+"][result][durability]="+result.getDurability());
				arguments.add("trades_display["+i+"][result][amount]="+result.getAmount());
				if(result.hasItemMeta()){
					ItemMeta itemMeta = result.getItemMeta();
					if(itemMeta.hasDisplayName()){
						arguments.add("trades_display["+i+"][result][name]="+URLEncoder.encode(itemMeta.getDisplayName(), "utf-8"));
					}
				}
				arguments.add("trades_display["+i+"][price_1][material]="+price_1.getType());
				arguments.add("trades_display["+i+"][price_1][durability]="+price_1.getDurability());
				arguments.add("trades_display["+i+"][price_1][amount]="+price_1.getAmount());
				if(price_1.hasItemMeta()){
					ItemMeta itemMeta = price_1.getItemMeta();
					if(itemMeta.hasDisplayName()){
						arguments.add("trades_display["+i+"][price_1][name]="+URLEncoder.encode(itemMeta.getDisplayName(), "utf-8"));
					}
				}
				if(price_2!=null){
					arguments.add("trades_display["+i+"][price_2][material]="+price_2.getType());
					arguments.add("trades_display["+i+"][price_2][durability]="+price_2.getDurability());
					arguments.add("trades_display["+i+"][price_2][amount]="+price_2.getAmount());
					if(price_2.hasItemMeta()){
						ItemMeta itemMeta = price_2.getItemMeta();
						if(itemMeta.hasDisplayName()){
							arguments.add("trades_display["+i+"][price_2][name]="+URLEncoder.encode(itemMeta.getDisplayName(), "utf-8"));
						}
					}
				}
			}
			DataSource.getResponse("shop/trades.php", new String[]{
					"shop_id="+this.shop_id,
					String.join("&", arguments)
			});
			//Bukkit.getLogger().info("[ShopManager] Handel vom Shop "+this.getName()+" aktualisiert.");
			this.updateAgents();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void delete(){
		DataSource.getResponse("shop/delete.php", new String[]{
				"shop_id="+this.shop_id	
			});
		this.shoppingWorld.shops.remove(this.shop_id);
		for(Villager villager : this.agents.toArray(new Villager[this.agents.size()])){
			if(villager.getVehicle()!=null){
				villager.getVehicle().remove();
			}
			villager.remove();
		}
		Chest chest = this.getChest();
		if(chest!=null){
			this.shoppingWorld.getWorld().getBlockAt(location_x, location_y, location_z).setType(this.getMarketplace().getAgentMarker());
			chest.setCustomName(null);
			if(chest.getInventory().getHolder() instanceof DoubleChest){
				DoubleChest doubleChest = (DoubleChest)chest.getInventory().getHolder();
				this.shoppingWorld.chestMap.remove(new VectorKey(((Chest)doubleChest.getLeftSide()).getLocation().toVector()));
				this.shoppingWorld.chestMap.remove(new VectorKey(((Chest)doubleChest.getRightSide()).getLocation().toVector()));
			}
			else{
				this.shoppingWorld.chestMap.remove(new VectorKey(new Vector(this.chest_x, this.chest_y, this.chest_z)));
			}
			this.shoppingWorld.blockMap.remove(new VectorKey(new Vector(this.location_x,this.location_y,this.location_z)));
		}
		this.shoppingWorld.chestMap.remove(new VectorKey(new Vector(this.chest_x, this.chest_y, this.chest_z)));
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop reload");
			}
		}, 1L);
	}
	
	protected void setCurrentTradingPartner(Player player){
		this.currentTradingPartner = player;
	}
	
	protected Player getCurrentTradingPartner(){
		return this.currentTradingPartner;
	}
	
	//static stuff
	
	protected static Shop load(ShoppingWorld shoppingWorld, ConfigurationSection dataSection){
		Shop shop = new Shop(shoppingWorld, dataSection);
		return shop;
	}
	
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
	
	public static Shop create(Player owner, int marketplace_id, Chest chest, Location location){
		String shopName = "§a"+owner.getName()+"s Shop";
		int chest_x = (chest!=null) ? chest.getX() : location.getBlockX();
		int chest_y = (chest!=null) ? chest.getY() : 0;
		int chest_z = (chest!=null) ? chest.getZ() : location.getBlockZ();
		YamlConfiguration yamlConfiguration;
		try {
			yamlConfiguration = DataSource.getYamlResponse("shop/create.php", new String[]{
					"world="+owner.getWorld().getName(),
					"marketplace_id="+marketplace_id,
					"player="+owner.getUniqueId(),
					"name="+URLEncoder.encode(shopName, "utf-8"),
					"location[x]="+location.getBlockX(),
					"location[y]="+location.getBlockY(),
					"location[z]="+location.getBlockZ(),
					"chest[x]="+chest_x,
					"chest[y]="+chest_y,
					"chest[z]="+chest_z,
			});
					
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			owner.sendMessage("[§dMarktplatz§r] §cBeim Erstellen des Händlers ist ein Fehler aufgetreten.");
			return null;
		}
		if(yamlConfiguration==null || !yamlConfiguration.contains("shop")){
			owner.sendMessage("[§dMarktplatz§r] §cBeim Erstellen des Händlers ist ein Fehler aufgetreten.");
			return null;
		}
		Shop shop = new Shop(ShoppingWorld.get(location.getWorld()), yamlConfiguration.getConfigurationSection("shop"));
		shop.shoppingWorld.shops.put(shop.shop_id, shop);
		shop.spawnAgent(location);
		if(shop.getChest()!=null){
			location.getBlock().setType(shop.getMarketplace().getShopMarker());
		}
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop reload");
			}
		}, 1L);
		return shop;
	}
}
