package ch.swisssmp.mapimageloader;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

class MapImageLoader {
	
	public static void load(String url, ItemStack itemStack){
		if(itemStack==null) return;
		itemStack.setType(Material.FILLED_MAP);
		ItemMeta itemMeta = itemStack.getItemMeta();
		MapMeta mapMeta = (MapMeta) itemMeta;
		if(!mapMeta.hasMapView()) mapMeta.setMapView(Bukkit.createMap(Bukkit.getWorlds().get(0)));
		MapView mapView = mapMeta.getMapView();
		List<MapRenderer> renderers = mapView.getRenderers();
		for(MapRenderer renderer : renderers) {
			mapView.removeRenderer(renderer);
		}
		MapImageUrlRenderer imageRenderer = new MapImageUrlRenderer(url);
		imageRenderer.load();
		mapView.addRenderer(imageRenderer);
		mapView.setLocked(true);
		itemStack.setItemMeta(itemMeta);
	}
}
