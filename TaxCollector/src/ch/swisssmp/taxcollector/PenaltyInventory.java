package ch.swisssmp.taxcollector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PenaltyInventory extends TaxInventory{

	public PenaltyInventory(Player player, ConfigurationSection dataSection) {
		super(player, Bukkit.createInventory(null, InventoryType.CHEST, dataSection.getString("title")), dataSection);
		this.player.openInventory(this.inventory);
	}

	@Override
	public void close() {

		int transferred_iron_ingots = 0;
		int transferred_gold_ingots = 0;
		int transferred_diamonds = 0;
		int transferred_glowstones = 0;
		int transferred_quartz = 0;
		
		for(ItemStack itemStack : this.inventory){
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
		
		TaxChest adminChest = TaxCollector.taxChests.get(0);
		if(adminChest!=null){
			if(transferred_iron_ingots>0)
				adminChest.addItem(new ItemStack(Material.IRON_INGOT, transferred_iron_ingots));
			if(transferred_gold_ingots>0)
				adminChest.addItem(new ItemStack(Material.GOLD_INGOT, transferred_gold_ingots));
			if(transferred_diamonds>0)
				adminChest.addItem(new ItemStack(Material.DIAMOND, transferred_diamonds));
			if(transferred_glowstones>0)
				adminChest.addItem(new ItemStack(Material.GLOWSTONE, transferred_glowstones));
			if(transferred_quartz>0)
				adminChest.addItem(new ItemStack(Material.QUARTZ_BLOCK, transferred_quartz));
		}
		
		YamlConfiguration response = DataSource.getYamlResponse("taxes/pay_penalty.php", new String[]{
			"player="+player.getUniqueId(),
			"items[IRON_INGOT]="+transferred_iron_ingots,
			"items[GOLD_INGOT]="+transferred_gold_ingots,
			"items[DIAMOND]="+transferred_diamonds,
			"items[GLOWSTONE]="+transferred_glowstones,
			"items[QUARTZ_BLOCK]="+transferred_quartz,
		});

		this.finish(response);
	}
	
}
