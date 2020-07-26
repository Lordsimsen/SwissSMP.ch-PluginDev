package ch.swisssmp.citymapdisplays;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.swisssmp.custompaintings.CustomPainting;
import ch.swisssmp.custompaintings.CustomPaintings;
import ch.swisssmp.text.ClickEvent;
import ch.swisssmp.text.HoverEvent;
import ch.swisssmp.text.RawBase;
import ch.swisssmp.text.RawText;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.util.BlockVector;

import ch.swisssmp.city.Cities;
import ch.swisssmp.city.Citizen;
import ch.swisssmp.city.City;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.livemap_render_api.LivemapView;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.world.WorldManager;
import net.md_5.bungee.api.chat.BaseComponent;

public class CityMapDisplay {
	
	public final static String DisplayUidProperty = "city_handbook_id";
	private final static int TILE_WIDTH = 128;
	private final static int TILE_HEIGHT = 128;
	
	private final UUID uid;
	private String name;
	private int currentCityId;
	
	/**
	 * Creates a new instance of CityMapdisplay
	 * @param uid: The UUID of this display
	 */
	public CityMapDisplay(UUID uid, String name) {
		this.uid = uid;
		this.name = name;
	}
	
	public UUID getUid() {
		return this.uid;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
		CustomPainting painting = getPainting();
		if(painting==null) return;
		painting.setName(name);
		painting.save();
	}

	public CustomPainting getPainting(){
		return CustomPainting.get(uid.toString()).orElse(null);
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
		CustomPainting painting = getPainting();
		if(painting==null){
			Bukkit.getLogger().warning(CityMapDisplaysPlugin.getPrefix()+" CustomPainting '"+name+"' mit id "+uid+" nicht gefunden!");
			return;
		}

		ConfigurationSection citySection = yamlConfiguration.getConfigurationSection("city");
		BlockVector min = citySection.getConfigurationSection("region_min").getVector().toBlockVector();
		BlockVector max = citySection.getConfigurationSection("region_max").getVector().toBlockVector();

		int width = painting.getWidth();
		int height = painting.getHeight();
		LivemapView view = new LivemapView(width, height, CityMapDisplaysPlugin.getLivemapInfo());
		String[][] urls = view.getUrls(min, max);
		BufferedImage[][] images = CityMapBuilder.load(urls);
		if(images==null){
			Bukkit.getLogger().warning(CityMapDisplaysPlugin.getPrefix()+" CustomPainting '"+name+"' mit id "+uid+" konnte nicht geladen werden!");
			return;
		}

		BufferedImage image = CityMapBuilder.stitch(images, width*TILE_WIDTH,height*TILE_HEIGHT,TILE_WIDTH,TILE_HEIGHT);
		File file = CityMapBuilder.save(image, uid);
		if(file==null){
			Bukkit.getLogger().warning(CityMapDisplaysPlugin.getPrefix()+" CustomPainting '"+name+"' mit id "+uid+" konnte nicht zwischengespeichert werden!");
			return;
		}

		CustomPaintings.replace(uid.toString(), file);
		this.currentCityId = city.getId();
		CityMapDisplays.save();
	}
	
	public void updateItemStack(ItemStack itemStack) {
		if(itemStack.getType()!=Material.WRITTEN_BOOK) return;
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		this.updateContents(bookMeta);
		itemStack.setItemMeta(bookMeta);
	}
	
	public void updateContents(BookMeta bookMeta) {
		bookMeta.setTitle("Städte");
		bookMeta.setGeneration(Generation.ORIGINAL);
		bookMeta.setAuthor("");
		Collection<City> cities = Cities.getAll();
		ArrayList<BaseComponent> currentPage = new ArrayList<BaseComponent>();
		List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();
		
		RawBase title = new RawText("Städte von\n")
			.extra(
					new RawText(WorldManager.getDisplayName(Bukkit.getWorlds().get(0))).bold(true).underlined(true),
					new RawText("\n\n")
			);
		currentPage.add(title.spigot());

		RawBase helpText = new RawText(
				""+ChatColor.ITALIC+ChatColor.GRAY+"Wähle eine Stadt aus,"+ChatColor.RESET+"\n"+
				ChatColor.ITALIC+ChatColor.GRAY+"um sie zu betrachten."+ChatColor.RESET+"\n"+
				ChatColor.RESET+"\n");
		currentPage.add(helpText.spigot());
		
		int remainingLines = 7;
		int maxLinesPerPage = 14;
		for(City city : cities) {
			Collection<Citizen> citizens = city.getCitizens();
			Optional<Citizen> mayor = citizens.stream().filter(c->c.getUniqueId().equals(city.getMayor())).findAny();
			String cityNameString = city.getName();
			if(cityNameString.length()>19) {
				cityNameString = cityNameString.substring(0,17)+"..";
			}
			if(this.currentCityId==city.getId()) cityNameString = ChatColor.DARK_RED+"> "+cityNameString+" <";
			RawBase cityEntry = new RawText(cityNameString+"\n")
					.color(ChatColor.BLACK)
					.hoverEvent(HoverEvent.showText(
						new RawText(city.getName()+"\n").color(ChatColor.AQUA),
						new RawText(citizens.size()+" Bürger\n").color(ChatColor.GRAY),
						new RawText("Bürgermeister:\n"),
						new RawText(mayor.isPresent() ? mayor.get().getDisplayName() : "unbekannt").color(ChatColor.GRAY)
					))
					.clickEvent(ClickEvent.runCommand("/citymapdisplay show "+this.uid+" "+city.getId()));
			currentPage.add(cityEntry.spigot());
			
			remainingLines--;
			if(remainingLines<=0) {
				BaseComponent[] currentPageArray = new BaseComponent[currentPage.size()];
				currentPage.toArray(currentPageArray);
				pages.add(currentPageArray);
				currentPage.clear();
				currentPage.add(helpText.spigot());
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
	
	protected JsonObject save() {
		JsonObject result = new JsonObject();
		JsonUtil.set("uid", uid, result);
		JsonUtil.set("name", name, result);
		JsonUtil.set("current_city", currentCityId, result);
		return result;
	}
	
	public void remove() {
		CityMapDisplays.remove(this);
		CityMapDisplays.save();
		CustomPainting painting = getPainting();
		if(painting!=null){
			painting.remove();
		}
	}
	
	protected void unload() {
		// do nothing at this point
	}
	
	public static CityMapDisplay load(JsonObject json) {
		UUID uid = JsonUtil.getUUID("uid", json);
		if(uid==null) throw new NullPointerException("Display UUID must not be empty.");
		String name = JsonUtil.getString("name", json);
		int currentCity = JsonUtil.getInt("current_city", json);
		CityMapDisplay result = new CityMapDisplay(uid, name);
		result.currentCityId = currentCity;
		return result;
	}
	
	public static Optional<CityMapDisplay> get(UUID displayUid) {
		return CityMapDisplays.get(displayUid);
	}
	
	public static Optional<CityMapDisplay> get(ItemStack itemStack) {
		String displayUidString = ItemUtil.getString(itemStack, DisplayUidProperty);
		if(displayUidString==null){
			return Optional.empty();
		}

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
	
	public static CityMapDisplay create(String name, int width, int height) {
		UUID displayUid = UUID.randomUUID();
		CustomPainting painting = CustomPaintings.create(displayUid.toString(), name, width, height);
		if(painting==null){
			Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Konnte kein Gemälde erstellen.");
			return null;
		}

		CityMapDisplay result = new CityMapDisplay(displayUid, name);
		CityMapDisplays.add(result);
		return result;
	}
}
