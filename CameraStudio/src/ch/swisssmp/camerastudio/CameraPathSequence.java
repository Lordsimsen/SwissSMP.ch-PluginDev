package ch.swisssmp.camerastudio;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.URLEncoder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;
import java.util.regex.Pattern;

public class CameraPathSequence extends CameraPathElement {

	private final List<UUID> sequence;
	private final List<Integer> timings;

	private ItemStack tourBookTemplate;

	protected CameraPathSequence(CameraStudioWorld world, UUID sequenceUid, String name){
		this(world, sequenceUid, name,new ArrayList<>(),new ArrayList<>(), null);
	}

	protected CameraPathSequence(CameraStudioWorld world, UUID sequenceUid, String name, List<UUID> sequence, List<Integer> timings, ItemStack tourBook){
		super(world, sequenceUid, name);
		this.sequence = sequence;
		this.timings = timings;
		this.tourBookTemplate = tourBook;
	}

	public List<UUID> getPathSequence(){return sequence;}
	public List<Integer> getTimings(){return timings;}

	public void setPathSequence(List<UUID> pathSequence, List<Integer> timings){
		this.sequence.clear();
		this.timings.clear();
		this.sequence.addAll(pathSequence);
		this.timings.addAll(timings);
	}

	@Override
	public ItemStack getItemStack(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(CameraStudioMaterial.PATH_SEQUENCE);
		itemBuilder.setAmount(1);
		itemBuilder.setDisplayName(ChatColor.AQUA+getName());
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, UID_PROPERTY, getUniqueId().toString());
		return itemStack;
	}

	public ItemStack getTourBookTemplate(){
		return tourBookTemplate;
	}

	public void setTourBookTemplate(ItemStack itemStack){
		tourBookTemplate = itemStack;
	}

	public ItemStack createTourBook(){
		if(tourBookTemplate ==null) return null;
		ItemStack tourBook = tourBookTemplate.clone();
		BookMeta bookMeta = (BookMeta) tourBook.getItemMeta();
		List<String> pageTexts = bookMeta.getPages();
		List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();
		for(String pageText : pageTexts){
			List<BaseComponent> pageComponents = new ArrayList<>();
			String[] parts = pageText.split(Pattern.quote("{start}"));
			pageComponents.add(new TextComponent(parts[0]));
			if(parts.length>1 || parts[0].length()<pageText.length()){
				TextComponent startSequenceComponent = new TextComponent("["+(bookMeta.hasDisplayName() ? bookMeta.getDisplayName() : "Sequenz starten")+"]");
				startSequenceComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
				startSequenceComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke, um zu starten").create()));
				startSequenceComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cam sequence "+getUniqueId()));
				pageComponents.add(startSequenceComponent);
				if(parts.length>1) pageComponents.add(new TextComponent(parts[1]));
			}
			BaseComponent[] componentsArray = new BaseComponent[pageComponents.size()];
			pageComponents.toArray(componentsArray);
			pages.add(componentsArray);
		}
		bookMeta.spigot().setPages(pages);
		tourBook.setItemMeta(bookMeta);
		return tourBook;
	}

	public boolean isSetupComplete(){
		return !(getName()==null || getName().isEmpty() || sequence.size()==0 || timings.size()<sequence.size());
	}

	public void run(Player player){
		run(player, null);
	}

	public void run(Player player, Runnable callback){
		if(player==null) return;
		CameraPathSequenceRunnable runnable = new CameraPathSequenceRunnable(this, player, callback);
		runnable.start();
	}

	protected JsonObject save(){
		JsonObject result = new JsonObject();
		result.addProperty("uid", this.getUniqueId().toString());
		result.addProperty("name", this.getName());
		JsonArray sequenceArray = new JsonArray();
		for(UUID entryUid : sequence){
			sequenceArray.add(entryUid.toString());
		}
		result.add("sequence", sequenceArray);
		JsonArray timingsMap = new JsonArray();
		for(Integer entry : timings){
			timingsMap.add(entry);
		}
		result.add("timings", timingsMap);
		if(tourBookTemplate !=null) JsonUtil.set("tour_book", ItemUtil.serialize(tourBookTemplate), result);
		return result;
	}

	@Override
	public void remove() {
		this.getWorld().remove(this);
	}
	
	public static CameraPathSequence load(CameraStudioWorld world, JsonObject json){
		UUID sequenceUid;
		try{
			String sequenceUidString = json.get("uid").getAsString();
			if(sequenceUidString==null) return null;
			sequenceUid = UUID.fromString(sequenceUidString);
		}
		catch(Exception e){
			return null;
		}
		String name = JsonUtil.getString("name", json);
		List<UUID> sequence = new ArrayList<>();
		if(json.has("sequence")){
			for(JsonElement element : json.getAsJsonArray("sequence")){
				if(!element.isJsonPrimitive()) continue;
				UUID pathUid;
				try{
					pathUid = UUID.fromString(element.getAsString());
				} catch(Exception e) { continue;}
				sequence.add(pathUid);
			}
		}

		List<Integer> timings = new ArrayList<>();
		if(json.has("timings")){
			for(JsonElement element : json.getAsJsonArray("timings")){
				if(!element.isJsonPrimitive()) continue;
				timings.add(element.getAsInt());
			}
		}

		ItemStack tourBook = json.has("tour_book") ? ItemUtil.deserialize(json.get("tour_book").getAsString()) : null;
		return new CameraPathSequence(world, sequenceUid, name, sequence, timings, tourBook);
	}
}
