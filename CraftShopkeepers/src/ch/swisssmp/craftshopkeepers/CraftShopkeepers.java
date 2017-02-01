package ch.swisssmp.craftshopkeepers;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftShopkeepers extends JavaPlugin implements Listener{
	public Logger logger;
	public ItemStack shopkeeper;
	public Server server;
	public ShapedRecipe shopkeeperRecipe;
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		createRecipe();
		
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	public void createRecipe(){
		logger = Logger.getLogger("Minecraft");
		MaterialData mat_data = new MaterialData(Material.MONSTER_EGG);
		shopkeeper = mat_data.toItemStack(1);
		shopkeeper.setDurability((short)120);
		ItemMeta meta = shopkeeper.getItemMeta();
		meta.setDisplayName("Händler");
		meta.setLore(Arrays.asList("Platziere diesen Händler", "auf einem Marktplatz,", "um einen Stand zu eröffnen."));
		shopkeeper.setItemMeta(meta);
		shopkeeperRecipe = new ShapedRecipe(shopkeeper);
		shopkeeperRecipe.shape("eee","drd","eee");
		shopkeeperRecipe.setIngredient('d', Material.DIAMOND);
		shopkeeperRecipe.setIngredient('r', Material.ROTTEN_FLESH);
		shopkeeperRecipe.setIngredient('e', Material.EMERALD);
		server.addRecipe(shopkeeperRecipe);
		logger.info("Custom Recipe has been created");
	}
    @EventHandler
    public void craftItem(PrepareItemCraftEvent event) {
    	ItemStack result = event.getRecipe().getResult();
    	if(!result.hasItemMeta())
    		return;
    	if(!result.getItemMeta().hasLore())
    		return;
    	if(result.getItemMeta().getLore().hashCode() == shopkeeper.getItemMeta().getLore().hashCode()){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(!crafter.hasPermission("craftshopkeepers.craft")){
            		event.getInventory().setResult(null);
            	}
            }
    	}
    }
    /*@EventHandler(ignoreCancelled=true)
    private void onPlayerChat(PlayerInteractEvent event){
    	if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
    	if(event.getItem().getType()==Material.MONSTER_EGG){
    		Player player = event.getPlayer();
    		Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
	        ArmorStand vehicle = (ArmorStand) player.getWorld().spawn(new Location(location.getWorld(), location.getX(), location.getY()-1.5, location.getZ()), ArmorStand.class);
	        Villager villager = (Villager) player.getWorld().spawn(location, Villager.class);
	        villager.setCustomName("Händler");
	        villager.setCustomNameVisible(true);
	        villager.setInvulnerable(true);
	        villager.setCollidable(false);
	        vehicle.setVisible(false);
	        vehicle.setGravity(false);
	        vehicle.setCustomName("shop_1");
	        vehicle.setCustomNameVisible(false);
	        vehicle.setPassenger(villager);
	        event.setCancelled(true);
    	}
    }
    @EventHandler(ignoreCancelled=true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
    	Entity interacted = event.getRightClicked();
    	if(interacted.getType()!=EntityType.VILLAGER) return;
    	Entity vehicle = interacted.getVehicle();
    	if(vehicle==null) return;
    	if(vehicle.getType()!=EntityType.ARMOR_STAND) return;
    	event.getPlayer().sendMessage(vehicle.getCustomName());
    }
    @EventHandler(ignoreCancelled=true)
    private void onEntityDeath(EntityDeathEvent event){
    	Entity dead = event.getEntity();
    	if(dead.getType()!=EntityType.VILLAGER) return;
    	Entity vehicle = dead.getVehicle();
    	if(vehicle==null) return;
    	if(vehicle.getType()!=EntityType.ARMOR_STAND) return;
    	vehicle.remove();
    }*/
}
