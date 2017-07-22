package ch.swisssmp.taxcollector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TaxListener implements Listener{
	
	private final Player player;
	private final Inventory inventory;
	private int iron_ingots = 0;
	private int gold_ingots = 0;
	private int diamonds = 0;
	private int glowstone = 0;
	private int quartz = 0;
	
	public TaxListener(Player player, ConfigurationSection taxData){
		this.player = player;
		for(String key : taxData.getKeys(false)){
			ConfigurationSection taxSection = taxData.getConfigurationSection(key);
			iron_ingots+=taxSection.getInt("iron_ingots_remaining");
			gold_ingots+=taxSection.getInt("gold_ingots_remaining");
			diamonds+=taxSection.getInt("diamonds_remaining");
			glowstone+=taxSection.getInt("glowstone_blocks_remaining");
			quartz+=taxSection.getInt("quartz_blocks_remaining");
		}
		this.inventory = Bukkit.createInventory(null, InventoryType.CHEST, getName());
		Bukkit.getPluginManager().registerEvents(this, TaxCollector.plugin);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 0.7f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 0.9f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 0.95f);
	}
	
	public void openInventory(){
		player.openInventory(this.inventory);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getPlayer()!=this.player){
			return;
		}
		HandlerList.unregisterAll(this);
		
		int transferred_iron_ingots = 0;
		int transferred_gold_ingots = 0;
		int transferred_diamonds = 0;
		int transferred_glowstones = 0;
		int transferred_quartz = 0;
		
		for(ItemStack itemStack : event.getInventory()){
			if(itemStack==null) continue;
			if(itemStack.getType()==Material.IRON_INGOT){
				transferred_iron_ingots+=1*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.IRON_BLOCK){
				transferred_iron_ingots+=9*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.GOLD_INGOT){
				transferred_gold_ingots+=1*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.GOLD_BLOCK){
				transferred_gold_ingots+=9*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.DIAMOND){
				transferred_diamonds+=1*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.DIAMOND_BLOCK){
				transferred_diamonds+=9*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.GLOWSTONE){
				transferred_glowstones+=1*itemStack.getAmount();
			}
			else if(itemStack.getType()==Material.QUARTZ_BLOCK){
				transferred_quartz+=1*itemStack.getAmount();
			}
			else{
				player.getWorld().dropItem(player.getEyeLocation(), itemStack);
			}
		}

		int remaining_iron_ingots = iron_ingots-transferred_iron_ingots;
		int remaining_gold_ingots = gold_ingots-transferred_gold_ingots;
		int remaining_diamonds = diamonds-transferred_diamonds;
		int remaining_glowstones = glowstone-transferred_glowstones;
		int remaining_quartz = quartz-transferred_quartz;
		
		transferred_iron_ingots = Math.min(iron_ingots, transferred_iron_ingots);
		transferred_gold_ingots = Math.min(gold_ingots, transferred_gold_ingots);
		transferred_diamonds = Math.min(diamonds, transferred_diamonds);
		transferred_glowstones = Math.min(glowstone, transferred_glowstones);
		transferred_quartz = Math.min(quartz, transferred_quartz);
		
		if(remaining_iron_ingots<0)player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.IRON_INGOT, -remaining_iron_ingots));
		if(remaining_gold_ingots<0)player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, -remaining_gold_ingots));
		if(remaining_diamonds<0)player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, -remaining_diamonds));
		if(remaining_glowstones<0)player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GLOWSTONE, -remaining_glowstones));
		if(remaining_quartz<0)player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.QUARTZ_BLOCK, -remaining_quartz));
		
		Chest adminChest = TaxCollector.taxChests.get(0);
		if(adminChest!=null){
			Inventory adminInventory = adminChest.getInventory();
			if(transferred_iron_ingots>0)
				adminInventory.addItem(new ItemStack(Material.IRON_INGOT, transferred_iron_ingots));
			if(transferred_gold_ingots>0)
				adminInventory.addItem(new ItemStack(Material.GOLD_INGOT, transferred_gold_ingots));
			if(transferred_diamonds>0)
				adminInventory.addItem(new ItemStack(Material.DIAMOND, transferred_diamonds));
			if(transferred_glowstones>0)
				adminInventory.addItem(new ItemStack(Material.GLOWSTONE, transferred_glowstones));
			if(transferred_quartz>0)
				adminInventory.addItem(new ItemStack(Material.QUARTZ_BLOCK, transferred_quartz));
		}
		
		YamlConfiguration response = DataSource.getYamlResponse("taxes/pay.php", new String[]{
			"player="+player.getUniqueId(),
			"items[IRON_INGOT]="+transferred_iron_ingots,
			"items[GOLD_INGOT]="+transferred_gold_ingots,
			"items[DIAMOND]="+transferred_diamonds,
			"items[GLOWSTONE]="+transferred_glowstones,
			"items[QUARTZ_BLOCK]="+transferred_quartz,
		});
		
		if(response!=null && response.contains("message")){
			player.sendMessage(response.getString("message"));
		}

		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 1.4f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 1.8f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 1.9f);
	}
	
	private String getName(){
		return
				"Abgabe: "+
				ChatColor.GRAY+iron_ingots+ChatColor.RESET+", "+
				ChatColor.GOLD+gold_ingots+ChatColor.RESET+", "+
				ChatColor.AQUA+diamonds+ChatColor.RESET+", "+
				ChatColor.YELLOW+glowstone+ChatColor.RESET+", "+
				ChatColor.WHITE+quartz;
	}
}
