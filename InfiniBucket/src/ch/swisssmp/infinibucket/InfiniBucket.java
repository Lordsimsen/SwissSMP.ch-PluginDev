package ch.swisssmp.infinibucket;

import java.util.Arrays;

import org.bukkit.Bukkit;
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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class InfiniBucket extends JavaPlugin implements Listener{
	public ItemMeta infinibucketmeta;
	public ItemStack infinibucket;
	public Server server;
	private NamespacedKey namespacedKey;
	
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		namespacedKey = new NamespacedKey(this, "Wasserversorgung");
		
		server.getPluginManager().registerEvents(this, this);
		createRecipe();
	}
	public void createRecipe(){
		infinibucket = new ItemStack(Material.WATER_BUCKET);
		ItemMeta meta = infinibucket.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		meta.setDisplayName("InfiniBucket");
		meta.setLore(Arrays.asList("Dieser Eimer wird", "niemals leer."));
		infinibucketmeta = meta;
		infinibucket.setItemMeta(infinibucketmeta);
		ShapedRecipe bucket = new ShapedRecipe(namespacedKey, infinibucket);
		bucket.shape(" e "," w "," b ");
		bucket.setIngredient('e', Material.ENDER_EYE);
		bucket.setIngredient('w', Material.POTION);
		bucket.setIngredient('b', Material.BUCKET);
		server.addRecipe(bucket);
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
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
        		player.sendMessage("Du hast keine Macht �ber diesen Eimer!");
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
