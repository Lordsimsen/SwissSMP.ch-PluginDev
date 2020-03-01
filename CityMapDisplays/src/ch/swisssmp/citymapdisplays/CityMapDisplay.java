package ch.swisssmp.citymapdisplays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.util.BlockVector;

import ch.swisssmp.city.Cities;
import ch.swisssmp.city.CitizenInfo;
import ch.swisssmp.city.City;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.livemap_render_api.LivemapView;
import ch.swisssmp.mapimageloader.MapImage;
import ch.swisssmp.mapimageloader.MapImages;
import ch.swisssmp.mapimageloader.MapViewComposition;
import ch.swisssmp.mapimageloader.MapViewCompositions;
import ch.swisssmp.text.RawTextObject;
import ch.swisssmp.text.properties.ClickEventProperty;
import ch.swisssmp.text.properties.ColorProperty;
import ch.swisssmp.text.properties.ColorProperty.Color;
import ch.swisssmp.text.properties.HoverEventProperty;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.world.WorldManager;
import net.md_5.bungee.api.chat.BaseComponent;

public class CityMapDisplay {
	
	public final static String DisplayUidProperty = "city_handbook_id";
	
	private final UUID uid;
	private final MapImage[][] images;
	private final MapViewComposition[][] mapViews;
	private int currentCityId;
	
	/**
	 * Creates a new instance of CityMapdisplay
	 * @param uid: The UUID of this display
	 * @param imageUids: The image UUIDs that make up the display. These correspond to MapImages of the plugin MapImageLoader. Dimensions: String[height][width]
	 */
	public CityMapDisplay(UUID uid, MapImage[][] imageUids, MapViewComposition[][] mapViews) {
		this.uid = uid;
		this.images = imageUids;
		this.mapViews = mapViews;
	}
	
	public UUID getUid() {
		return this.uid;
	}
	
	public MapViewComposition[][] getMapViews(){
		return mapViews;
	}
	
	public ItemStack getItemStack() {
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.WRITTEN_BOOK);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(ChatColor.AQUA+"Städte-Handbuch");
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, DisplayUidProperty, uid.toString());
		updateItemStack(result);
		return result;
	}
	
	public void applyCity(City city) {
		HTTPRequest request = DataSource.getResponse(CityMapDisplaysPlugin.getInstance(), "city_region.php", new String[] {
				"city_id="+city.getId()
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("city")) return;
			applyCity(city, yamlConfiguration);
		});
	}
	
	private void applyCity(City city, YamlConfiguration yamlConfiguration) {
		ConfigurationSection citySection = yamlConfiguration.getConfigurationSection("city");
		BlockVector min = citySection.getConfigurationSection("region_min").getVector().toBlockVector();
		BlockVector max = citySection.getConfigurationSection("region_max").getVector().toBlockVector();

		int width = mapViews[0].length;
		int height = mapViews.length;
		LivemapView view = new LivemapView(width, height, CityMapDisplaysPlugin.getLivemapInfo());
		String[][] urls = view.getUrls(min, max);
		for(int y = 0; y < urls.length; y++) {
			String[] row = urls[y];
			for(int x = 0; x < row.length; x++) {
				String url = row[x];
				MapImage image = images[y][x];
				image.replace(url);
			}
		}
		//int left = 0;
		//int top = 0;
		//MapViewComposition topCenterView = mapViews[top][left];
		//topCenterView.addText("city_shadow", "§"+(MapPalette.VanillaColor.COLOR_GRAY.id * 4 + 2)+";"+city.getName(), 11, 1);
		//topCenterView.addText("city", "§"+(MapPalette.VanillaColor.SNOW.id * 4 + 2)+";"+city.getName(), 10, 0);
		//MapViewCompositions.save();
		this.currentCityId = city.getId();
		MapImages.save();
		CityMapDisplays.save();
	}
	
	public void updateItemStack(ItemStack itemStack) {
		if(itemStack.getType()!=Material.WRITTEN_BOOK) return;
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		this.updateContents(bookMeta);
		itemStack.setItemMeta(bookMeta);
	}
	
	public void updateContents(BookMeta bookMeta) {
		Bukkit.getLogger().info("Update Book contents");
		bookMeta.setTitle("Städte");
		bookMeta.setGeneration(Generation.ORIGINAL);
		bookMeta.setAuthor("");
		Collection<City> cities = Cities.getAll();
		ArrayList<BaseComponent> currentPage = new ArrayList<BaseComponent>();
		List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();
		
		RawTextObject title = new RawTextObject(
				"Städte von\n"+
				ChatColor.BOLD+ChatColor.UNDERLINE+WorldManager.getDisplayName(Bukkit.getWorlds().get(0))+ChatColor.RESET+"\n"+
				ChatColor.RESET+"\n");
		currentPage.add(title.toSpigot());
		
		RawTextObject helpText = new RawTextObject(
				""+ChatColor.ITALIC+ChatColor.GRAY+"Wähle eine Stadt aus,"+ChatColor.RESET+"\n"+
				ChatColor.ITALIC+ChatColor.GRAY+"um sie zu betrachten."+ChatColor.RESET+"\n"+
				ChatColor.RESET+"\n");
		currentPage.add(helpText.toSpigot());
		
		int remainingLines = 7;
		int maxLinesPerPage = 14;
		for(City city : cities) {
			Collection<CitizenInfo> citizens = city.getCitizens();
			Optional<CitizenInfo> mayor = citizens.stream().filter(c->c.getUniqueId().equals(city.getMayor())).findAny();
			String cityNameString = city.getName();
			if(cityNameString.length()>19) {
				cityNameString = cityNameString.substring(0,17)+"..";
			}
			if(this.currentCityId==city.getId()) cityNameString = ChatColor.DARK_RED+"> "+cityNameString+" <";
			RawTextObject cityEntry = new RawTextObject(cityNameString+"\n");
			cityEntry.add(new ColorProperty(Color.BLACK));
			cityEntry.add(new HoverEventProperty(new RawTextObject(
					ChatColor.AQUA+city.getName()+ChatColor.RESET+"\n"+
					ChatColor.GRAY+citizens.size()+" Bürger\n"+
					ChatColor.GRAY+"Bürgermeister:\n"+ChatColor.RESET+(mayor.isPresent()?mayor.get().getDisplayName():"unbekannt")+ChatColor.RESET+"\n")));
			cityEntry.add(new ClickEventProperty(ClickEventProperty.Action.RUN_COMMAND, "/citymapdisplay show "+this.uid+" "+city.getId()));
			currentPage.add(cityEntry.toSpigot());
			
			remainingLines--;
			if(remainingLines<=0) {
				BaseComponent[] currentPageArray = new BaseComponent[currentPage.size()];
				currentPage.toArray(currentPageArray);
				pages.add(currentPageArray);
				currentPage.clear();
				currentPage.add(helpText.toSpigot());
				remainingLines = maxLinesPerPage-3;
			}
		}
		if(currentPage.size()>0) {
			BaseComponent[] currentPageArray = new BaseComponent[currentPage.size()];
			currentPage.toArray(currentPageArray);
			pages.add(currentPageArray);
		}
		bookMeta.spigot().setPages(pages);
	}
	
	protected void save(ConfigurationSection dataSection) {
		dataSection.set("width", this.images[0].length);
		dataSection.set("height", this.images.length);
		dataSection.set("current_city", this.currentCityId);
		ConfigurationSection imagesSection = dataSection.createSection("images");
		ConfigurationSection viewsSection = dataSection.createSection("views");
		for(int y = 0; y < images.length; y++) {
			MapImage[] row = images[y];
			MapViewComposition[] viewRow = this.mapViews[y];
			for(int x = 0; x < row.length; x++) {
				MapImage image = row[x];
				ConfigurationSection imageSection = imagesSection.createSection(image.getUid().toString());
				imageSection.set("x", x);
				imageSection.set("y", y);
				MapViewComposition composition = viewRow[x];
				ConfigurationSection viewSection = viewsSection.createSection(String.valueOf(composition.getMapId()));
				viewSection.set("x", x);
				viewSection.set("y", y);
			}
		}
	}
	
	public void remove() {
		CityMapDisplays.remove(this);
		for(MapImage[] row : this.images) {
			for(MapImage image : row) {
				if(image==null) continue;
				image.remove();
			}
		}
		for(MapViewComposition[] row : this.mapViews) {
			for(MapViewComposition view : row) {
				view.remove();
			}
		}
		MapImages.save();
		MapViewCompositions.save();
	}
	
	protected void unload() {
		// do nothing at this point
	}
	
	public static CityMapDisplay load(UUID displayUid, ConfigurationSection dataSection) {
		if(displayUid==null) throw new NullPointerException("Display UUID must not be empty.");
		int width = dataSection.getInt("width");
		int height = dataSection.getInt("height");
		MapImage[][] images = new MapImage[height][width];
		MapViewComposition[][] mapViews = new MapViewComposition[height][width];
		ConfigurationSection imagesSection = dataSection.getConfigurationSection("images");
		ConfigurationSection viewsSection = dataSection.getConfigurationSection("views");
		for(String key : imagesSection.getKeys(false)) {
			UUID imageUid;
			try {
				imageUid = UUID.fromString(key);
			}
			catch(Exception e) {
				continue;
			}
			Optional<MapImage> imageQuery = MapImage.get(imageUid);
			if(!imageQuery.isPresent()) continue;
			MapImage image = imageQuery.get();
			ConfigurationSection imageSection = imagesSection.getConfigurationSection(key);
			int posX = imageSection.getInt("x");
			int posY = imageSection.getInt("y");
			if(posY>=0 && posY < height && posX>=0 && posX<width) images[posY][posX] = image;
		}
		for(String key : viewsSection.getKeys(false)) {
			int mapId;
			try {
				mapId = Integer.parseInt(key);
			}
			catch(Exception e) {
				Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Invalid map-id encountered when loading CityMapDisplay: "+key);
				continue;
			}
			Optional<MapViewComposition> view = MapViewComposition.get(mapId);
			if(!view.isPresent()) {
				Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Missing MapViewComposition encountered when loading CityMapDisplay: "+mapId);
				continue;
			}
			ConfigurationSection viewSection = viewsSection.getConfigurationSection(key);
			int posX = viewSection.getInt("x");
			int posY = viewSection.getInt("y");
			if(posY>=0 && posY < height && posX>=0 && posX<width) mapViews[posY][posX] = view.get();
		}
		CityMapDisplay result = new CityMapDisplay(displayUid, images, mapViews);
		result.currentCityId = dataSection.getInt("current_city");
		return result;
	}
	
	public static Optional<CityMapDisplay> get(UUID displayUid) {
		return CityMapDisplays.get(displayUid);
	}
	
	public static Optional<CityMapDisplay> get(ItemStack itemStack) {
		String displayUidString = ItemUtil.getString(itemStack, DisplayUidProperty);
		UUID displayUid;
		try {
			displayUid = UUID.fromString(displayUidString);
		}
		catch(Exception e) {
			Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Invalid UUID: "+displayUidString);
			return Optional.empty();
		}
		Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" UUID: "+displayUid);
		return CityMapDisplay.get(displayUid);
	}
	
	public static CityMapDisplay create(int width, int height) {
		UUID displayUid = UUID.randomUUID();
		MapImage[][] images = new MapImage[height][width];
		MapViewComposition[][] mapViews = new MapViewComposition[height][width];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				MapImage mapImage = MapImage.create(null, false);
				if(mapImage==null) {
					new NullPointerException("There seems to be an error with MapImageLoader!").printStackTrace();
					continue;
				}
				MapViewComposition composition = MapViewComposition.create();
				composition.addImage(mapImage);
				images[y][x] = mapImage;
				mapViews[y][x] = composition;
			}
		}
		MapImages.save();
		MapViewCompositions.save();
		CityMapDisplay result = new CityMapDisplay(displayUid, images, mapViews);
		CityMapDisplays.add(result);
		return result;
	}
}
