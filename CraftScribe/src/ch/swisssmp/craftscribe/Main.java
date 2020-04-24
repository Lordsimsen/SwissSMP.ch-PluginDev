package ch.swisssmp.craftscribe;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	private static Logger logger;
	protected static File dataFolder;
	protected static File configFile;
	protected static YamlConfiguration config;
	protected static PluginDescriptionFile pdfFile;
	protected static Main plugin;
	protected static boolean debug;

	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onAnvilInsert(PrepareAnvilEvent event){
		HumanEntity player = event.getView().getPlayer();
		if(player.hasPermission("craftscribe.use")){
			AnvilInventory inventory = event.getInventory();
			ItemStack[] items = inventory.getContents();
			boolean containsBookAndQuill = false;
			Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
			for(ItemStack itemStack : items){
				if(itemStack==null) continue;
				if(itemStack.getType()==Material.WRITABLE_BOOK){
					containsBookAndQuill = true;
					continue;
				}
				else if(itemStack.getEnchantments()!=null){
					if(itemStack.getEnchantments().size()<1) continue;
					for(Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()){
						enchantments.put(entry.getKey(), entry.getValue());
					}
					ItemMeta itemMeta = itemStack.getItemMeta();
					if(itemMeta instanceof Damageable){
						Damageable damageable = (Damageable) itemMeta;
						float remainingDurability = 1 - ((float) damageable.getDamage()/(float)itemStack.getType().getMaxDurability());
						if(remainingDurability<0.95f){
							enchantments.put(Enchantment.VANISHING_CURSE, 1);
						}
					}
					continue;
				}
			}
			if(containsBookAndQuill && enchantments.size()>0){
				ItemStack result = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta )  result.getItemMeta();
				for(Entry<Enchantment, Integer> entry : enchantments.entrySet()){
					itemMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
				}
				result.setItemMeta(itemMeta);
				event.setResult(result);
				inventory.setRepairCost(100);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onInventoryClick(InventoryClickEvent event){
		if(!(event.getClickedInventory() instanceof AnvilInventory)) return;
		if(event.getSlotType()!=SlotType.RESULT) return;
		if(event.getView().getCursor()!=null && event.getView().getCursor().getType()!=Material.AIR) return;
		AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();
		ItemStack result = inventory.getItem(event.getRawSlot());
		if(result==null) return;
		if(result.getType()!=Material.ENCHANTED_BOOK) return;
		boolean containsBookAndQuill = false;
		for(ItemStack itemStack : inventory.getContents()){
			if(itemStack==null) continue;
			if(itemStack.getType()==Material.WRITABLE_BOOK) containsBookAndQuill = true;
		}
		if(!containsBookAndQuill){
			return;
		}
		event.setCancelled(true);
		event.getView().setCursor(result);
		inventory.setContents(new ItemStack[inventory.getContents().length]);
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1, 1);
	}
}
