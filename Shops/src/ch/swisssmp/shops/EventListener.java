package ch.swisssmp.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.EventPoints;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.VectorKey;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		if(ShopManager.plugin.worlds.containsKey(event.getWorld())) return;
		World world = event.getWorld();
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				ShopManager.plugin.worlds.put(world, ShoppingWorld.load(world));
			}
		}, 1L);
	}
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		ShopManager.plugin.worlds.remove(event.getWorld());
	}
	@EventHandler
	private void onTrade(InventoryClickEvent event){
		if(event.getView().getType()!=InventoryType.MERCHANT) return;
		MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
		if((event.getClickedInventory() instanceof MerchantInventory) && (event.getSlot()==0 || event.getSlot()==1)){
			ItemStack itemStack = merchantInventory.getItem(event.getSlot());
			if(itemStack!=null){
				String customEnum = CustomItems.getCustomEnum(itemStack);
				if(customEnum!=null && customEnum.equals("EVENT_POINT")){
					event.setCancelled(true);
				}
			}
		}
		if(event.getSlotType() != SlotType.RESULT) return;
		InventoryHolder holder = merchantInventory.getHolder();
		if(holder==null) return;
		if(!(holder instanceof Villager)) return;
		Villager villager = (Villager) holder;
		ShoppingWorld shoppingWorld = ShoppingWorld.get(villager.getWorld());
		if(shoppingWorld==null) return;
		Shop shop = shoppingWorld.getShop(villager);
		if(shop==null) return;
		if(merchantInventory.getItem(2)==null) return;
		MerchantRecipe recipe = merchantInventory.getSelectedRecipe();
		Chest chest = shop.getChest();
		int playerCanBuy = ShopUtil.countTradeBuyerSide(recipe, merchantInventory.getContents());
		if(chest!=null){
			recipe.setExperienceReward(false);
			//Bukkit.getLogger().info("[ShopManager] Player Shop");
			//Bukkit.getLogger().info("[ShopManager] Verbleibende Käufe: "+recipe.getMaxUses());
			//Bukkit.getLogger().info("[ShopManager] Spieler kann "+playerCanBuy+"x handeln");
			//find out how many times the trade is completed
			int ownerCanProvide = ShopUtil.countTradeOwnerSide(recipe, chest.getInventory());
			int buyCount = Math.min(ownerCanProvide, playerCanBuy);
			if(!event.isShiftClick()){
				//Bukkit.getLogger().info("[ShopManager] Einzeleinkauf");
				buyCount = Math.min(buyCount, 1);
			}
			else{
				//Bukkit.getLogger().info("[ShopManager] Masseneinkauf");
			}
			//Bukkit.getLogger().info("[ShopManager] Handel wird "+buyCount+"x durchgeführt");
			//cancel if the trade cannot be completed (lack of storage from the owner)
			if(buyCount==0){
				event.setCancelled(true);
				return;
			}
			//remove items from the chest
			int transferAmount = recipe.getResult().getAmount()*buyCount;
			
			for(int i = 0; i < chest.getInventory().getSize() && transferAmount>0; i++){
				ItemStack itemStack = chest.getInventory().getItem(i);
				if(itemStack==null) continue;
				if(itemStack.isSimilar(recipe.getResult())){
					if(itemStack.getAmount()>transferAmount){
						itemStack.setAmount(itemStack.getAmount()-transferAmount);
						transferAmount = 0;
					}
					else{
						transferAmount-=itemStack.getAmount();
						chest.getInventory().setItem(i, null);
					}
				}
			}
			//add items to the chest
			for(ItemStack itemStack : recipe.getIngredients()){
				for(int i = 0; i < buyCount; i++) chest.getInventory().addItem(itemStack);
			}
		}
		else{
			Bukkit.getLogger().info("[ShopManager] Admin Shop");
			ItemStack ingredient = recipe.getIngredients().get(0);
			if(ingredient!=null){
				String customEnum = CustomItems.getCustomEnum(ingredient);
				if(customEnum!=null && customEnum.equals("EVENT_POINT")){
					int buyCount = playerCanBuy;
					if(!event.isShiftClick()){
						buyCount = Math.min(buyCount, 1);
					}
					if(buyCount>0)
					{
						DataSource.getResponse("players/event_points.php", new String[]{
							"player="+event.getView().getPlayer().getUniqueId().toString(),
							"amount="+String.valueOf(-buyCount*ingredient.getAmount())
						});
					}
				}
			}
		}
		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				shop.updateAgents();
			}
		}, 1l);
	}
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if(!(block.getState() instanceof Chest)) return;
		Chest chest = (Chest)block.getState();
		ShoppingWorld shoppingWorld = ShoppingWorld.get(block.getWorld());
		if(shoppingWorld==null) return;
		Shop existing = shoppingWorld.getShop(chest);
		//Check whether the player wants to create a new shop
		ItemStack itemStack = event.getItem();
		boolean isShopContract = ShopManager.isShopContract(itemStack);
		//Check whether the player is permitted to interact with said cest
		if(existing!=null){
			if(!existing.getOwnerUUID().equals(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("shop.admin")){
				event.setCancelled(true);
				event.getPlayer().sendMessage("[§dMarktplatz§r] Diese Kiste gehört zu einem Shop von "+existing.getOwnerName()+"§r.");
				return;
			}
			else if(existing.getCurrentTradingPartner()!=null){
				event.setCancelled(true);
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cShop wird verwendet");
				return;
			}
			else if(!isShopContract){
				existing.setCurrentTradingPartner(event.getPlayer());
				return;
			}
		}
		if(!isShopContract) return;
		//Check whether the player has permission to create a shop
		if(!event.getPlayer().hasPermission("shop.create")){
			event.setCancelled(true);
			SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
			return;
		}
		//Check if chest already in use
		if(existing!=null){
			event.setCancelled(true);
			event.getPlayer().sendMessage("[§dMarktplatz§r] Diese Kiste gehört bereits zu deinem Shop '"+existing.getName()+"§r'.");
			return;
		}
		int marketplace_id = -1;
		//Check whether it's possible to create a shop here
		ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionManager(block.getWorld()).getApplicableRegions(block.getLocation());
		List<String> regionNames = new ArrayList<String>();
		for(ProtectedRegion region : regions){
			regionNames.add("regions[]="+region.getId());
		}
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("shop/check_marketplace.php", new String[]{
				"player="+event.getPlayer().getUniqueId().toString(),
				"x="+block.getX(),
				"y="+block.getY(),
				"z="+block.getZ(),
				"world="+block.getWorld().getName(),
				String.join("&", regionNames)
				});
		if(yamlConfiguration==null || !yamlConfiguration.contains("permission") || !yamlConfiguration.getBoolean("permission")){
			if(!event.getPlayer().hasPermission("shop.admin")){
				event.setCancelled(true);
				event.getPlayer().sendMessage("[§dMarktplatz§r] Du kannst nur auf einem Marktplatz einen Shop eröffnen.");
				return;
			}
		}
		if(yamlConfiguration!=null && yamlConfiguration.contains("marketplace_id")){
			marketplace_id = yamlConfiguration.getInt("marketplace_id");
		}
		ShopCreator shopCreator = ShopCreator.creatorMap.get(event.getPlayer());
		if(shopCreator==null){
			event.getPlayer().sendMessage("[§dMarktplatz§r] Kiste augewählt. Platziere nun den Händler in der Nähe.");
			ShopCreator.initiate(event.getPlayer(), marketplace_id, chest, itemStack);
			event.setCancelled(true);
		}
		else{
			shopCreator.setChest(chest);
			shopCreator.setMarketplaceId(marketplace_id);
			event.getPlayer().sendMessage("[§dMarktplatz§r] Kiste geändert.");
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(event.getRightClicked().getType()!=EntityType.VILLAGER){
			//Bukkit.getLogger().info("[ShopManager] Kein Villager");
			return;
		}
		Villager villager = (Villager)event.getRightClicked();
		ShoppingWorld shoppingWorld = ShoppingWorld.get(villager.getWorld());
		if(shoppingWorld==null){
			//Bukkit.getLogger().info("[ShopManager] Keine Shopping-Welt");
			return;
		}
		Shop shop = shoppingWorld.getShop(villager);
		if(shop==null){
			//Bukkit.getLogger().info("[ShopManager] Kein Shop");
			return;
		}
		if(event.getPlayer().isSneaking()){
			//Bukkit.getLogger().info("[ShopManager] Spieler schleicht");
			if(shop.getOwnerUUID().equals(event.getPlayer().getUniqueId()) || event.getPlayer().hasPermission("shop.admin")){
				event.setCancelled(true);
				shop.openEditor(event.getPlayer());
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
				shop.updateAgents();
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
		if(!itemStack.hasItemMeta()) return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasDisplayName()) return;
		if(itemMeta.getDisplayName().equals(EventPoints.getSignature())){
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("players/balance.php", new String[]{
				"player="+event.getPlayer().getUniqueId().toString(),
			});
			if(yamlConfiguration==null || !yamlConfiguration.contains("balance")) return;
			int balance = Math.min(64, yamlConfiguration.getInt("balance"));
			Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
				public void run(){
					ItemStack eventPoints = EventPoints.getItem(balance);
					merchantInventory.setItem(0, eventPoints);
				}
			}, 1L);
		}
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
		ShoppingWorld shoppingWorld;
		Inventory inventory = event.getInventory();
		Chest chest;
		if(inventory.getHolder() instanceof Chest){
			chest = (Chest)inventory.getHolder();
		}
		else if(inventory.getHolder() instanceof DoubleChest){
			chest = (Chest)((DoubleChest)inventory.getHolder()).getLeftSide();
		}
		else if(inventory.getType() == InventoryType.MERCHANT){
			for(int i = 0; i < 2; i++){
				ItemStack itemStack = inventory.getItem(i);
				if(itemStack==null) continue;
				if(!itemStack.hasItemMeta()) continue;
				ItemMeta itemMeta = itemStack.getItemMeta();
				if(itemMeta.hasDisplayName()&&itemMeta.getDisplayName().equals(EventPoints.getSignature())){
					inventory.setItem(i, null);
				}
			}
			Villager villager = (Villager) inventory.getHolder();
			shoppingWorld = ShoppingWorld.get(villager.getWorld());
			if(shoppingWorld==null) return;
			Shop shop = shoppingWorld.getShop(villager);
			if(shop==null) return;
			shop.setCurrentTradingPartner(null);
			return;
		}
		else return;
		shoppingWorld = ShoppingWorld.get(chest.getWorld());
		if(shoppingWorld==null) return;
		Shop shop = shoppingWorld.getShop(chest);
		if(shop==null) return;
		shop.updateAgents();
		shop.setCurrentTradingPartner(null);
	}
	
	//divine block protection
	
	@EventHandler(ignoreCancelled=true)
	private void onPistonExtend(BlockPistonExtendEvent event){
		World world = event.getBlock().getWorld();
		ShoppingWorld shoppingWorld = ShoppingWorld.get(world);
		if(shoppingWorld==null) return;
		for(Block block : event.getBlocks()){
			if(shoppingWorld.blockMap.containsKey(new VectorKey(block.getLocation().toVector()))){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		//chest protection
		if(block.getState() instanceof Chest){
			Chest chest = (Chest)block.getState();
			if(chest.getCustomName()==null) return;
			if(!chest.getCustomName().equals("§rHändler-Kiste"))
				return;
			event.setCancelled(true);
			event.getPlayer().sendMessage("[§dMarktplatz§r] §cDiese Kiste gehört zu einem Händler.");
		}
		//block protection
		else{
			World world = event.getBlock().getWorld();
			ShoppingWorld shoppingWorld = ShoppingWorld.get(world);
			if(shoppingWorld==null) return;
			if(shoppingWorld.blockMap.containsKey(new VectorKey(event.getBlock().getLocation().toVector()))){
				event.setCancelled(true);
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cBlock geschützt.");
			}
			return;
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockExplode(BlockExplodeEvent event){
		World world = event.getBlock().getWorld();
		ShoppingWorld shoppingWorld = ShoppingWorld.get(world);
		if(shoppingWorld==null) return;
		for(VectorKey vectorKey : shoppingWorld.chestMap.keySet()){
			event.blockList().remove(world.getBlockAt(vectorKey.getVector().getBlockX(), vectorKey.getVector().getBlockY(), vectorKey.getVector().getBlockZ()));
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockExplode(EntityExplodeEvent event){
		World world = event.getEntity().getWorld();
		ShoppingWorld shoppingWorld = ShoppingWorld.get(world);
		if(shoppingWorld==null) return;
		for(VectorKey vectorKey : shoppingWorld.chestMap.keySet()){
			event.blockList().remove(world.getBlockAt(vectorKey.getVector().getBlockX(), vectorKey.getVector().getBlockY(), vectorKey.getVector().getBlockZ()));
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onItemMove(InventoryMoveItemEvent event){
		if(event.getSource()==null) return;
		InventoryHolder holder = event.getSource().getHolder();
		if(!(holder instanceof Chest)) return;
		Chest chest = (Chest)holder;
		if(chest.getCustomName()==null) return;
		if(!chest.getCustomName().equals("§rHändler-Kiste"))
			return;
		if(event.getInitiator().getHolder() instanceof Player) return;
		event.setCancelled(true);
	}
	
	/*@EventHandler
	private void onEntityDismount(EntityDismountEvent event){
		Entity entity = event.getEntity();
		if(entity.getType()!=EntityType.VILLAGER) return;
		if(event.getDismounted().getType()!=EntityType.ARMOR_STAND) return;
		if(event.getDismounted().getCustomName()==null) return;
		if(event.getDismounted().getCustomName().contains("Shop_")){
			if(!event.getDismounted().isValid() || !event.getEntity().isValid()) return;
			//Bukkit.getLogger().info("[ShopManager] Agent versuchte zu fliehen!");
			event.getDismounted().addPassenger(entity);
		}
	}*/
}
