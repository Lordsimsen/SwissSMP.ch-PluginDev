package ch.swisssmp.craftalchemy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.Random;

public class CraftAlchemy extends JavaPlugin implements Listener{
	public ItemMeta philosopherstonemeta;
	public Logger logger;
	public ItemStack philosopherstone;
	public Server server;
	public Map<Material, Byte> loops;
	public Random random;
	protected NamespacedKey namespacedKey;
	
	public void onEnable() {
		random = new Random();
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		namespacedKey = new NamespacedKey(this, "Alchemie");
		
		server.getPluginManager().registerEvents(this, this);
		createRecipe();
		createTransmutation();
	}
	public void createRecipe(){
		logger = Logger.getLogger("Minecraft");
		MaterialData mat_data = new MaterialData(Material.FIREWORK_CHARGE);
		philosopherstone = mat_data.toItemStack(1);
		ItemMeta meta = philosopherstone.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 3, true);
		meta.setDisplayName("Stein der Weisen");
		meta.setLore(Arrays.asList("Der Stein der Transmutation", "beherrscht die Elemente."));
		philosopherstonemeta = meta;
		philosopherstone.setItemMeta(philosopherstonemeta);
		ShapedRecipe stone = new ShapedRecipe(namespacedKey, philosopherstone);
		stone.shape("bnb","dfd","brb");
		stone.setIngredient('b', Material.BLAZE_POWDER);
		stone.setIngredient('n', Material.NETHER_STAR);
		stone.setIngredient('d', Material.DRAGONS_BREATH);
		stone.setIngredient('f', Material.FIREBALL);
		stone.setIngredient('r', Material.RABBIT_FOOT);
		server.addRecipe(stone);
		logger.info("Custom Recipe has been created");
	}
	public void createTransmutation(){
		loops = new HashMap<Material, Byte>();
		loops.put(Material.STONE, (byte)7);
		loops.put(Material.WOOL, (byte)16);
		loops.put(Material.WOOD, (byte)4);
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    @EventHandler(ignoreCancelled=true)
    public void craftItem(PrepareItemCraftEvent event) {
    	if(event.getRecipe()==null) return;
    	ItemStack result = event.getRecipe().getResult();
        HumanEntity human = event.getView().getPlayer();
    	if(result.hashCode() != philosopherstone.hashCode() || !(human instanceof Player)){
    		return;
    	}
    	Player crafter = (Player)human;
    	if(!crafter.hasPermission("craftalchemy.craft"))
    		event.getInventory().setResult(null);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	ItemStack item = event.getItem();
    	if(item == null)
    		return;
    	if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND)) {
    		ItemStack checkitem = item.clone();
	    	if(checkitem.containsEnchantment(Enchantment.ARROW_DAMAGE)){
	    		checkitem = noEnchant(item);
	    	}
	    	else return;
	    	if(checkitem.hashCode() == noEnchant(philosopherstone).hashCode() && item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE)>0) {
	    		Block block = event.getClickedBlock();
	    		Material material = block.getType();
	    		if(loops.containsKey(material)){
	    			byte b = block.getData();
	    			byte max_b = loops.get(material);
	    			if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
	        			b++;
	        			if(b>=max_b)
	        				b=0;
	    			}
	    			else{
	        			b--;
	        			if(b<0)
	        				b = (byte) (max_b-1);
	    			}
	    			block.setData(b);
	    			block.getState().update();
	    			if(random.nextFloat()>0.999){
	    				Map<Enchantment, Integer> map = item.getEnchantments();
	    				if(map.containsKey(Enchantment.ARROW_DAMAGE)){
	    					ItemMeta meta = item.getItemMeta();
	    					int prev_level = meta.getEnchantLevel(Enchantment.ARROW_DAMAGE);
	    					if(prev_level>0){
	    						meta.removeEnchant(Enchantment.ARROW_DAMAGE);
	    						meta.addEnchant(Enchantment.ARROW_DAMAGE, prev_level-1, true);
	    						if(prev_level-1>0){
	    							event.getPlayer().sendMessage(ChatColor.AQUA+"Die Macht des Steins schwindet!");
	    	    					item.setItemMeta(meta);
	    						}
	    						else {
	    							event.getPlayer().sendMessage(ChatColor.DARK_RED+"Die Macht des Steins erlischt!");
	    							item.setItemMeta(null);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
		}
	}
    public ItemStack noEnchant(ItemStack toConvert){
    	toConvert = toConvert.clone();
    	ItemMeta meta = toConvert.getItemMeta();
    	Set<Enchantment> enchants = toConvert.getEnchantments().keySet();
    	for(Enchantment e : enchants)
    		meta.removeEnchant(e);
    	toConvert.setItemMeta(meta);
    	return toConvert;
    }
	@EventHandler
    public void onPlayerEntityInteract(PlayerInteractEntityEvent event) {
    	Player player = event.getPlayer();
    	Integer hand = player.getInventory().getHeldItemSlot();
    	ItemStack item = player.getInventory().getItem(hand);
    	if(item == null)
    		return;
    	else if(noEnchant(item).hashCode() == noEnchant(philosopherstone).hashCode() && event.getHand() == EquipmentSlot.HAND){
			Integer prev_level = item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
    		if(prev_level>0){
    			Entity _target = event.getRightClicked();
    			if(_target instanceof LivingEntity){
    				LivingEntity target = (LivingEntity) _target;
    				if(target.getHealth()!=target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
        				target.setHealth(Math.min(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), target.getHealth()+10));
        				Location location = target.getEyeLocation();
        				for(int i = 0; i < 5; i++){
            				target.getWorld().spawnParticle(Particle.HEART, location.clone().add(random.insideUnitSphere().multiply(0.2f)), 1);
        				}
        				ItemMeta meta = item.getItemMeta();
        				meta.removeEnchant(Enchantment.ARROW_DAMAGE);
						meta.addEnchant(Enchantment.ARROW_DAMAGE, prev_level-1, true);
						if(prev_level-1>0){
							event.getPlayer().sendMessage(ChatColor.AQUA+"Die Macht des Steins schwindet!");
	    					item.setItemMeta(meta);
						}
						else {
							event.getPlayer().sendMessage(ChatColor.DARK_RED+"Die Macht des Steins erlischt!");
							item.setItemMeta(null);
						}
    				}
    			}
    		}
    	}
    	
    }
}
