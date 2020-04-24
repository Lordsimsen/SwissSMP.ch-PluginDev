package ch.swisssmp.adventuredungeons;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.InventoryUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class DungeonsView extends InventoryView implements Listener{
	private final Player player;
	private final Inventory inventory;
	
	private DungeonsView(Player player){
		this.player = player;
		this.inventory = Bukkit.createInventory(null, 36, "Dungeons");
		this.createDungeonTokens();
	}
	
	private void createDungeonTokens(){
		HTTPRequest request = DataSource.getResponse(AdventureDungeons.getInstance(), "get_dungeons.php");
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("dungeons")){
				this.player.sendMessage("[AdventureDungeons] "+ChatColor.RED+"Konnte Dungeons nicht anzeigen.");
				return;
			}
			ConfigurationSection dungeonsSection = yamlConfiguration.getConfigurationSection("dungeons");
			ConfigurationSection dungeonSection;
			int dungeon_id;
			String dungeon_name;
			String custom_enum;
			ItemStack tokenStack;
			for(String key : dungeonsSection.getKeys(false)){
				dungeonSection = dungeonsSection.getConfigurationSection(key);
				dungeon_id = dungeonSection.getInt("dungeon_id");
				dungeon_name = dungeonSection.getString("name");
				custom_enum = dungeonSection.getString("custom_enum");
				tokenStack = ItemManager.getDungeonToken(dungeon_id, dungeon_name, custom_enum);
				this.inventory.addItem(tokenStack);
			}
		});
	}

	@Override
	public String getTitle() {
		return "Dungeons";
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || event.getClickedInventory()!=this.inventory) return;
		ItemStack itemStack = this.inventory.getItem(event.getSlot());
		if(itemStack == null || (event.getCursor()!=null && event.getCursor().isSimilar(itemStack))){
			event.setCancelled(true);
			return;
		}
		InventoryUtil.refillInventorySlot(inventory, event.getSlot(), itemStack.clone());
		
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		HandlerList.unregisterAll(this);
	}

	@Override
	public Inventory getBottomInventory() {
		return this.player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return this.player;
	}

	@Override
	public Inventory getTopInventory() {
		return this.inventory;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	protected static DungeonsView open(Player player){
		DungeonsView result = new DungeonsView(player);
		Bukkit.getPluginManager().registerEvents(result, AdventureDungeons.getInstance());
		player.openInventory(result);
		return result;
	}
}
