package ch.swisssmp.mapimageloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class MapViewComposition {
	
	private final int mapId;
	private List<MapImageInstance> imageInstances = new ArrayList<MapImageInstance>();
	private HashMap<String,MapTextInstance> textInstances = new HashMap<String,MapTextInstance>();
	
	private MapView mapView;
	private List<MapInstanceRenderer> renderers = new ArrayList<MapInstanceRenderer>();
	
	private MapViewComposition(int mapId) {
		this.mapId = mapId;
	}
	
	private MapViewComposition(ConfigurationSection dataSection) {
		this.mapId = dataSection.getInt("map_id");
		ConfigurationSection imagesSection = dataSection.getConfigurationSection("images");
		if(imagesSection!=null) {
			for(String key : imagesSection.getKeys(false)) {
				UUID imageUid;
				try {
					imageUid = UUID.fromString(key);
				}
				catch(Exception e) {
					continue;
				}
				ConfigurationSection instanceSection = imagesSection.getConfigurationSection(key);
				MapImageInstance instance = new MapImageInstance(imageUid);
				instance.load(instanceSection);
				this.addImage(instance);
			}
		}
		ConfigurationSection textsSection = dataSection.getConfigurationSection("texts");
		if(textsSection!=null) {
			for(String key : textsSection.getKeys(false)) {
				ConfigurationSection instanceSection = textsSection.getConfigurationSection(key);
				MapTextInstance instance = new MapTextInstance();
				instance.load(instanceSection);
				this.addText(key, instance);
			}
		}
		Bukkit.getLogger().info("Composition has "+imageInstances.size()+" images and "+textInstances.size()+" texts.");
	}
	
	public int getMapId() {
		return this.mapId;
	}
	
	public ItemStack createItemStack() {
		ItemStack result = new ItemStack(Material.FILLED_MAP);
		ItemMeta itemMeta = result.getItemMeta();
		MapMeta mapMeta = (MapMeta) itemMeta;
		mapMeta.setMapView(this.mapView);
		result.setItemMeta(mapMeta);
		return result;
	}
	
	private void initialize() {
		@SuppressWarnings("deprecation")
		MapView mapView = Bukkit.getMap(mapId);
		if(mapView==null) return;
		for(MapRenderer renderer : mapView.getRenderers()) {
			mapView.removeRenderer(renderer);
		}
		for(MapInstanceRenderer renderer : this.renderers) {
			mapView.addRenderer(renderer);
		}
		this.mapView = mapView;
	}
	
	public boolean contains(MapImage image) {
		return this.imageInstances.stream().anyMatch(i->i.getImageUid().equals(image.getUid()));
	}
	
	public void setDirty() {
		for(MapInstanceRenderer r : this.renderers) {
			r.setDirty();
		}
	}
	
	public void addImage(MapImage image) {
		addImage(image, 0, 0);
	}
	
	public void addImage(MapImage image, int x, int y) {
		MapImageInstance instance = new MapImageInstance(image.getUid(),x,y);
		this.addImage(instance);
	}
	
	public void addText(String key, String value) {
		addText(key, value, MinecraftFont.Font);
	}
	
	public void addText(String key, String value, int x, int y) {
		addText(key, value, MinecraftFont.Font, x, y);
	}
	
	public void addText(String key, String value, MapFont font) {
		addText(key, value, font, 0, 0);
	}
	
	public void addText(String key, String value, MapFont font, int x, int y) {
		MapTextInstance instance = new MapTextInstance(value, font, x, y);
		this.addText(key, instance);
	}
	
	public void removeImage(MapImage image) {
		Optional<MapImageInstance> instanceQuery = this.imageInstances.stream().filter(i->i.getImageUid().equals(image.getUid())).findAny();
		if(!instanceQuery.isPresent()) return;
		MapImageInstance instance = instanceQuery.get();
		this.imageInstances.remove(instance);
		Optional<MapImageInstanceRenderer> rendererQuery = this.renderers.stream().filter(r->r instanceof MapImageInstanceRenderer).map(r->(MapImageInstanceRenderer)r).filter(r->r.getInstance()==instance).findAny();
		if(!rendererQuery.isPresent()) return;
		MapImageInstanceRenderer renderer = rendererQuery.get();
		this.renderers.remove(renderer);
		mapView.removeRenderer(renderer);
	}
	
	private void addImage(MapImageInstance instance) {
		imageInstances.add(instance);
		MapImageInstanceRenderer renderer = new MapImageInstanceRenderer(instance);
		renderers.add(renderer);
		if(mapView!=null) mapView.addRenderer(renderer);
	}
	
	private void addText(String key, MapTextInstance instance) {
		if(textInstances.containsKey(key) && mapView!=null) {
			MapTextInstance existing = textInstances.get(key);
			Optional<MapTextInstanceRenderer> existingRenderer = renderers.stream().filter(r->r instanceof MapTextInstanceRenderer).map(r->(MapTextInstanceRenderer)r).filter(r->r.getInstance()==existing).findAny();
			if(existingRenderer.isPresent()) {
				renderers.remove(existingRenderer.get());
				mapView.removeRenderer(existingRenderer.get());
			}
		}
		textInstances.put(key, instance);
		MapTextInstanceRenderer renderer = new MapTextInstanceRenderer(instance);
		renderers.add(renderer);
		if(mapView!=null) mapView.addRenderer(renderer);
	}
	
	public void unload() {
		// do nothing at this point
	}
	
	public void remove() {
		MapViewCompositions.remove(this);
		if(mapView==null) return;
		for(MapRenderer renderer : this.mapView.getRenderers()) {
			this.mapView.removeRenderer(renderer);
		}
	}
	
	protected void save(ConfigurationSection dataSection) {
		dataSection.set("map_id", this.mapId);
		ConfigurationSection instancesSection = dataSection.createSection("images");
		for(MapImageInstance i : imageInstances) {
			ConfigurationSection instanceSection = instancesSection.createSection(i.getImageUid().toString());
			i.save(instanceSection);
		}
		ConfigurationSection textsSection = dataSection.createSection("texts");
		for(Entry<String,MapTextInstance> entry : textInstances.entrySet()) {
			String key = entry.getKey();
			MapTextInstance i = entry.getValue();
			ConfigurationSection instanceSection = textsSection.createSection(key);
			i.save(instanceSection);
		}
		//TODO save cursors
	}
	
	public static MapViewComposition create() {
		MapView mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));
		return create(mapView);
	}
	
	public static MapViewComposition create(MapView mapView) {
		int mapId = mapView.getId();
		MapViewComposition result = new MapViewComposition(mapId);
		result.initialize();
		if(result.mapView==null) return null;
		MapViewCompositions.add(result);
		return result;
	}
	
	protected static MapViewComposition load(ConfigurationSection dataSection) {
		MapViewComposition result = new MapViewComposition(dataSection);
		result.initialize();
		if(result.mapView==null) return null;
		return result;
	}
	
	public static Optional<MapViewComposition> get(int mapId) {
		return MapViewCompositions.get(mapId);
	}
}
