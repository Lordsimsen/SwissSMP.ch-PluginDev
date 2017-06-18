package ch.swisssmp.craftsaddle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftSaddle extends JavaPlugin implements Listener{
	private Logger logger;
	private Server server;
	private PluginDescriptionFile pdfFile;
	private NamespacedKey namespacedKey;
	
	private ItemStack privateSaddle;
	private ItemMeta privateSaddleMeta;
	
	private List<DamageCause> protectedFrom;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		namespacedKey = new NamespacedKey(this, "Sattel");
		createRecipe();

		protectedFrom = new ArrayList<DamageCause>();
    	protectedFrom.add(DamageCause.BLOCK_EXPLOSION);
    	protectedFrom.add(DamageCause.ENTITY_EXPLOSION);
    	protectedFrom.add(DamageCause.ENTITY_ATTACK);
    	protectedFrom.add(DamageCause.FALLING_BLOCK);
    	protectedFrom.add(DamageCause.SUFFOCATION);
    	protectedFrom.add(DamageCause.LIGHTNING);
    	protectedFrom.add(DamageCause.PROJECTILE);
    	protectedFrom.add(DamageCause.MAGIC);
    	protectedFrom.add(DamageCause.POISON);
    	protectedFrom.add(DamageCause.CONTACT);
    	protectedFrom.add(DamageCause.FIRE);
    	protectedFrom.add(DamageCause.FIRE_TICK);
	}
	public void createRecipe(){
		logger = Logger.getLogger("Minecraft");
		MaterialData mat_data = new MaterialData(Material.SADDLE);
		privateSaddle = mat_data.toItemStack(1);
		privateSaddleMeta = privateSaddle.getItemMeta();
		privateSaddleMeta.addEnchant(Enchantment.DURABILITY, 1, false);
		privateSaddleMeta.setLore(Arrays.asList("PRIVAT"));
		privateSaddle.setItemMeta(privateSaddleMeta);
		ShapedRecipe recipe = new ShapedRecipe(namespacedKey, privateSaddle);
		recipe.shape(" n ","lsl"," i ");
		recipe.setIngredient('n', Material.NAME_TAG);
		recipe.setIngredient('l', Material.LEASH);
		recipe.setIngredient('s', Material.SADDLE);
		recipe.setIngredient('i', Material.IRON_INGOT);
		server.addRecipe(recipe);
		logger.info("Custom Recipe has been created");
	}
	public static boolean isPrivateSaddle(ItemStack item){
		if(item == null)
			return false;
		if(item.getType() != Material.SADDLE){
			return false;
		}
		ItemMeta meta = item.getItemMeta();
		if(!meta.hasLore())
			return false;
		List<String> lore = meta.getLore();
		if(lore.get(0).equals("PRIVAT") && meta.hasEnchant(Enchantment.DURABILITY)){
			return true;
		}
		else {
			return false;
		}
	}
    
    public static boolean isPrivateHorse(Entity entity){
    	if(!(entity instanceof Horse))
    		return false;
    	Horse horse = (Horse) entity;
    	return isPrivateHorse(horse.getInventory());
    }
    
    public static boolean isPrivateHorse(Horse horse){
    	return isPrivateHorse(horse.getInventory());
    }
    
    public static boolean isPrivateHorse(Inventory inventory){
    	if(!(inventory instanceof HorseInventory))
    		return false;
    	HorseInventory horseInventory = (HorseInventory) inventory;
    	return isPrivateHorse(horseInventory);
    }
    
    public static boolean isPrivateHorse(HorseInventory horseInventory){
    	ItemStack saddle = horseInventory.getSaddle();
    	if(!isPrivateSaddle(saddle))
    		return false;
    	return true;
    }
    
    public static boolean isOwner(Entity entity, Entity rightclicked){
    	if(!isPrivateHorse(rightclicked))
    		return false;
    	Horse horse = (Horse) rightclicked;
    	return isOwner(entity, horse);
    }
    
    public static boolean isOwner(Entity entity, Horse horse){
    	return isOwner(entity, horse.getInventory());
    }
    
    public static boolean isOwner(Entity entity, Inventory inventory){
    	if(!isPrivateHorse(inventory))
    		return false;
    	return isOwner(entity, (HorseInventory) inventory);
    }
    
    public static boolean isOwner(Entity entity, HorseInventory horseInventory){
    	ItemStack saddle = horseInventory.getSaddle();
    	ItemMeta meta = saddle.getItemMeta();
    	List<String> lore = meta.getLore();
    	if(lore.size() < 2)
    		return false;
    	return (lore.get(1).equals(entity.getName()));
    }
	
    @EventHandler(ignoreCancelled=true)
    public void prepareCraftItem(PrepareItemCraftEvent event) {
    	if(event.getRecipe()==null) return;
    	ItemStack result = event.getRecipe().getResult();
    	if(isPrivateSaddle(result)){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(crafter.hasPermission("craftSaddle.craft")){
            		ItemMeta meta = result.getItemMeta();
            		List<String> lore = meta.getLore();
            		lore.add(crafter.getName());
            		meta.setLore(lore);
            		result.setItemMeta(meta);
            		event.getInventory().setResult(result);
            	}
            	else{
            		event.getInventory().setResult(null);
            	}
            }
    	}
    }
    
    @EventHandler
    public void mountHorse(VehicleEnterEvent event){
    	Entity entity = event.getVehicle();
    	if(!isPrivateHorse(entity))
    		return;
		Entity rider = event.getEntered();
		if(!isOwner(rider, entity)){
			if(rider instanceof Player){
        		Player player = (Player) rider;
    			player.sendMessage(ChatColor.DARK_RED+"Dieses Pferd gehört nicht dir!");
			}
			event.setCancelled(true);
		}
    }

    @EventHandler
    public void damageHorse(EntityDamageEvent event){
    	if(!isPrivateHorse(event.getEntity()))
    		return;
    	DamageCause cause = event.getCause();
    	Horse horse = (Horse) event.getEntity();
    	if(protectedFrom.contains(cause) && horse.getPassengers().size()<1){
    		event.setCancelled(true);
    	}
    	else return;
    }
    
    @EventHandler
    public void accessHorse(InventoryOpenEvent event){
    	if(!isPrivateHorse(event.getInventory()))
    		return;
    	if(!isOwner(event.getPlayer(), event.getInventory())){
    		event.setCancelled(true);
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"Dieses Pferd gehört nicht dir!");
    	}
    }
    
    @EventHandler
    public void leashHorse(PlayerInteractEntityEvent event){
    	if(!isPrivateHorse(event.getRightClicked()))
    		return;
    	if(!isOwner(event.getPlayer(), event.getRightClicked())){
    		event.setCancelled(true);
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"Dieses Pferd gehört nicht dir!");
    	}
    }
    
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
