package ch.swisssmp.shops;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.VectorKey;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.WebCore;

@SuppressWarnings("unused")
public class ShopManager extends JavaPlugin{
	protected PluginDescriptionFile pdfFile;
	protected static ShopManager plugin;
	protected boolean debug;
	
	protected CustomItemBuilder shopContractBuilder;
	
	protected HashMap<World,ShoppingWorld> worlds = new HashMap<World,ShoppingWorld>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		//Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		this.getCommand("shop").setExecutor(new PlayerCommand());
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);

		Bukkit.getScheduler().runTaskLater(ShopManager.plugin, new Runnable(){
			public void run(){
				loadWorlds();
				loadShopContract();
			}
		}, 1L);
	}
	
	protected static boolean isShopContract(ItemStack itemStack){
		if(itemStack==null) return false;
		if(!itemStack.hasItemMeta()) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasDisplayName()) return false;
		return itemMeta.getDisplayName().equals("§EHändler-Vertrag");
	}
	
	private void loadShopContract(){
		CustomItemBuilder shopContract = CustomItems.getCustomItemBuilder("CONTRACT");
		if(shopContract==null){
			//Bukkit.getLogger().info("[ShopManager] Händler-Vertrag konnte nicht geladen werden.");
			return;
		}
		shopContract.setAmount(1);
		shopContract.setDisplayName("§EHändler-Vertrag");
		List<String> lore = new ArrayList<String>();
		lore.add("§7Platziert einen Händler.");
		lore.add("§7Rechtsklick auf Kiste, danach");
		lore.add("§7auf Stelle für den Händler.");
		shopContract.setLore(lore);
		this.shopContractBuilder = shopContract;
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(ShopManager.plugin, "Händler-Vertrag"), shopContract.build());
		recipe.shape(
				"eee",
				"drd",
				"eee"
				);
		recipe.setIngredient('e', Material.EMERALD);
		recipe.setIngredient('d', Material.DIAMOND);
		recipe.setIngredient('r', Material.ROTTEN_FLESH);
		Bukkit.getServer().addRecipe(recipe);
	}
	
	protected void loadWorlds(){
		worlds.clear();
		for(World world : Bukkit.getWorlds()){
			worlds.put(world, ShoppingWorld.load(world));
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		//Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
