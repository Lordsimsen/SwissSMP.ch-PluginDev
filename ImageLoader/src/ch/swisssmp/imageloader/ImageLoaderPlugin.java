package ch.swisssmp.imageloader;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ImageLoaderPlugin extends JavaPlugin {
	protected static PluginDescriptionFile pdfFile;
	protected static ImageLoaderPlugin plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		
		Bukkit.getPluginCommand("imageloader").setExecutor(new PlayerCommand());
		
		Bukkit.getLogger().info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@SuppressWarnings("deprecation")
	public static void load(String url, ItemStack itemStack){
		if(itemStack==null) return;
		itemStack.setType(Material.FILLED_MAP);
		ItemMeta itemMeta = itemStack.getItemMeta();
		MapMeta mapMeta = (MapMeta) itemMeta;
		MapView mapView = Bukkit.getMap((short) mapMeta.getMapId());
		List<MapRenderer> renderers = mapView.getRenderers();
		renderers.clear();
		ImageRenderer imageRenderer = new ImageRenderer(url);
		renderers.add(imageRenderer);
		imageRenderer.load();
	}
}
