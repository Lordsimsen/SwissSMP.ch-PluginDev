package ch.swisssmp.stalker;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class Search implements Listener{
	protected final UUID player_uuid;
	protected boolean output = false;
	protected Block lastBlock;
	protected YamlConfiguration lastQuery;
	protected int page = 1;
	protected int pages = 0;
	protected int results = 0;
	
	protected Search(Player player){
		this.player_uuid = player.getUniqueId();
		Stalker.searches.put(this.player_uuid, this);
		Bukkit.getPluginManager().registerEvents(this, Stalker.getInstance());
	}
	protected void finish(){
		Stalker.searches.remove(this.player_uuid);
		HandlerList.unregisterAll(this);
	}
	private String[] getArguments(Block block){
		return new String[]{
				"x="+block.getX(),
				"y="+block.getY(),
				"z="+block.getZ(),
				"world="+block.getWorld().getName(),
				"player="+this.player_uuid.toString()
			};
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getHand()!=EquipmentSlot.HAND) return;
		Player player = event.getPlayer();
		if(!player.getUniqueId().equals(this.player_uuid)){
			return;
		}
		Block block;
		if(event.getAction()==Action.LEFT_CLICK_BLOCK)
			block = event.getClickedBlock();
		else if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			block = event.getClickedBlock().getRelative(event.getBlockFace());
		}
		else return;
		event.setCancelled(true);
		this.lastBlock = block;
		this.lastQuery = DataSource.getYamlResponse(Stalker.getInstance(), "search.php", getArguments(block));
		this.page = 1;
		ConfigurationSection metaSection = lastQuery.getConfigurationSection("meta");
		if(metaSection==null){
			player.sendMessage(ChatColor.RED+"Bei der Abfrage ist etwas schiefgelaufen.");
			return;
		}
		this.pages = metaSection.getInt("pages");
		this.results = metaSection.getInt("results");
		player.sendMessage(ChatColor.DARK_AQUA+"Scanne Block "+getBlockString(block));
		if(this.output){
			player.sendMessage(ChatColor.GRAY+"Total "+this.results+" Ereignisse gefunden");
			this.output();
		}
	}
	private String getBlockString(Block block){
		if(block==null) return "";
		return ChatColor.WHITE+block.getWorld().getName()+" "+block.getX()+","+block.getY()+","+block.getZ();
	}
	protected void output(){
		this.output(this.page);
	}
	private void output(int page){
		Player player = Bukkit.getPlayer(player_uuid);
		if(player==null || lastBlock==null || lastQuery==null || page>pages) return;
		player.sendMessage(getBlockString(this.lastBlock)+": Zeige Seite "+ChatColor.WHITE+page+"/"+pages);
		ConfigurationSection pagesSection = lastQuery.getConfigurationSection("pages");
		if(pagesSection==null){
			player.sendMessage(ChatColor.GRAY+"Keine Resultate gefunden.");
			return;
		}
		ConfigurationSection pageSection = pagesSection.getConfigurationSection("page_"+page);
		for(String key : pageSection.getKeys(false)){
			ConfigurationSection dataSection = pageSection.getConfigurationSection(key);
			int id = dataSection.getInt("id");
			String message = ChatColor.RED+"- "+ChatColor.GRAY+"["+id+"] ";
			message += ChatColor.DARK_AQUA+dataSection.getString("who")+" ";
			message += ChatColor.WHITE+dataSection.getString("action")+" ";
			message += ChatColor.DARK_AQUA+dataSection.getString("what")+" ";
			message += ChatColor.GREEN+"x"+String.valueOf(dataSection.getInt("count"))+" ";
			message += ChatColor.WHITE+dataSection.getString("when");
			player.sendMessage(message);
		}
	}
}
