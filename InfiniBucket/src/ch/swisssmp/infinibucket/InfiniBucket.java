package ch.swisssmp.infinibucket;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class InfiniBucket extends JavaPlugin implements Listener{
	public ItemMeta infinibucketmeta;
	public Logger logger;
	public ItemStack infinibucket;
	public Server server;
	private NamespacedKey namespacedKey;
	
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		namespacedKey = new NamespacedKey(this, "Wasserversorgung");
		
		server.getPluginManager().registerEvents(this, this);
		createRecipe();
	}
	public void createRecipe(){
		logger = Logger.getLogger("Minecraft");
		MaterialData mat_data = new MaterialData(Material.WATER_BUCKET);
		infinibucket = mat_data.toItemStack(1);
		ItemMeta meta = infinibucket.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		meta.setDisplayName("InfiniBucket");
		meta.setLore(Arrays.asList("Dieser Eimer wird", "niemals leer."));
		infinibucketmeta = meta;
		infinibucket.setItemMeta(infinibucketmeta);
		ShapedRecipe bucket = new ShapedRecipe(namespacedKey, infinibucket);
		bucket.shape(" e "," w "," b ");
		bucket.setIngredient('e', Material.EYE_OF_ENDER);
		bucket.setIngredient('w', Material.POTION);
		bucket.setIngredient('b', Material.BUCKET);
		server.addRecipe(bucket);
		logger.info("Custom Recipe has been created");
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    @EventHandler(ignoreCancelled=true)
    public void craftItem(PrepareItemCraftEvent event) {
    	if(event.getRecipe()==null) return;
    	ItemStack result = event.getRecipe().getResult();
    	if(result.hashCode() == infinibucket.hashCode()){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(!crafter.hasPermission("infinibucket.craft"))
            		event.getInventory().setResult(null);
            }
    	}
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlaceWater(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        
        boolean checkOffhand = true;

		event.setItemStack(new ItemStack(Material.APPLE));
		
		boolean isInfiniBucket = false;
		
        if(mainHand != null)
        	if(mainHand.hasItemMeta())
        		if(mainHand.getItemMeta().hashCode() == infinibucketmeta.hashCode()){
        			isInfiniBucket = true;
        		}
        if(offHand != null && checkOffhand)
        	if(offHand.hasItemMeta())
        		if(offHand.getItemMeta().hashCode() == infinibucketmeta.hashCode()){
        			isInfiniBucket = true;
    			}
        if(isInfiniBucket)
        {
        	if(!player.hasPermission("infinibucket.use")){
        		player.sendMessage("Du hast keine Macht über diesen Eimer!");
            	event.setCancelled(true);
        	}
        	else{
            	setWater(event.getBlockClicked(), event.getBlockFace());
            	event.setCancelled(true);
            	event.getPlayer().updateInventory();
        	}
        }
    }
    public void setWater(Block block, BlockFace face){
    	Block target = block.getRelative(face);
    	target.setType(Material.WATER);
    }
}
