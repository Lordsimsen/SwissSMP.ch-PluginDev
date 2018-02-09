package ch.swisssmp.adventuredungeons.camp;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.VectorKey;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CampEditor implements Listener {
	public static HashMap<Player, CampEditor> editors = new HashMap<Player, CampEditor>();
	
	private final String name;
	private final Player player;
	private final int camp_id;
	private final World world;
	private final ArrayList<VectorKey> spawnpoints;
	
	private CampEditor(String name, Player player, int camp_id, World world, ArrayList<VectorKey> spawnpoints){
		this.name = name;
		this.player = player;
		this.camp_id = camp_id;
		this.world = world;
		this.spawnpoints = spawnpoints;
		Bukkit.getPluginManager().registerEvents(this, AdventureDungeons.plugin);
		editors.put(this.player, this);
		player.sendMessage(ChatColor.DARK_AQUA+"Editor für Camp "+this.getName()+" gestartet.");
	}
	
	public static CampEditor initiate(Player player, int camp_id){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("adventure/camp_editor.php", new String[]{
				"camp="+camp_id,
				"action=initiate"
			});
			if(yamlConfiguration==null || !yamlConfiguration.contains("name")){
				player.sendMessage("[AdventureDungeons]"+ChatColor.RED+" Konnte Editor nicht starten, diese Camp-ID ist ungültig.");
				return null;
			}
			String name = yamlConfiguration.getString("name");
			if(!yamlConfiguration.contains("dungeon_id")){
				player.sendMessage("AdventureDungeons]"+ChatColor.RED+" Zuerst im Web-Interface einen Dungeon für das Camp auswählen.");
				return null;
			}
			int dungeon_id = yamlConfiguration.getInt("dungeon_id");
			Dungeon dungeon = Dungeon.get(dungeon_id);
			World world = dungeon.editTemplate();
			if(world==null){
				player.sendMessage("AdventureDungeons]"+ChatColor.RED+" Der Dungeon-Editor konnte nicht gestartet werden.");
				return null;
			}
			ArrayList<VectorKey> spawnpoints = new ArrayList<VectorKey>();
			VectorKey vectorKey;
			Vector vector;
			Block block;
			if(yamlConfiguration.contains("spawnpoints")){
				ConfigurationSection spawnpointsSection = yamlConfiguration.getConfigurationSection("spawnpoints");
				for(String key : spawnpointsSection.getKeys(false)){
					vectorKey = spawnpointsSection.getVectorKey(key);
					vector = vectorKey.getVector();
					block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
					if(block.getType()==Material.AIR || block.getType()==Material.GOLD_BLOCK){
						spawnpoints.add(vectorKey);
						block.setType(Material.GOLD_BLOCK);
					}
				}
			}
			return new CampEditor(name, player, camp_id, world, spawnpoints);
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getCampId(){
		return this.camp_id;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		VectorKey vectorKey = new VectorKey(event.getBlock().getLocation().toVector());
		if(!this.spawnpoints.contains(vectorKey)){
			return;
		}
		if(event.getPlayer()!=this.player){
			event.setCancelled(true);
			return;
		}
		this.spawnpoints.remove(vectorKey);
		DataSource.getResponse("adventure/camp_editor.php", new String[]{
				"camp="+this.camp_id,
				"action=remove_spawnpoint",
				"x="+event.getBlock().getX(),
				"y="+event.getBlock().getY(),
				"z="+event.getBlock().getZ()
		});
		SwissSMPler.get(event.getPlayer()).sendActionBar("Spawnpunkt entfernt. ("+spawnpoints.size()+" verbleiben)");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockPlace(BlockPlaceEvent event){
		if(event.getPlayer()!=this.player) return;
		Block block = event.getBlock();
		if(block.getType()!=Material.GOLD_BLOCK) return;
		DataSource.getResponse("adventure/camp_editor.php", new String[]{
				"camp="+this.camp_id,
				"action=add_spawnpoint",
				"x="+event.getBlock().getX(),
				"y="+event.getBlock().getY(),
				"z="+event.getBlock().getZ()
		});
		this.spawnpoints.add(new VectorKey(block.getLocation().toVector()));
		SwissSMPler.get(event.getPlayer()).sendActionBar("Spawnpunkt hinzugefügt. ("+spawnpoints.size()+" total)");
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		if(event.getFrom()==this.world && event.getPlayer().getWorld()!=this.world){
			this.quit();
		}
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		if(this.player==event.getPlayer()){
			this.quit();
		}
	}
	
	public void quit(){
		Vector vector;
		for(VectorKey vectorKey : this.spawnpoints){
			vector = vectorKey.getVector();
			world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).setType(Material.AIR);
		}
		editors.remove(this.player);
		HandlerList.unregisterAll(this);
		player.sendMessage(ChatColor.DARK_AQUA+"Editor für Camp "+this.getName()+" beendet.");
	}
	
	public static CampEditor get(Player player){
		return editors.get(player);
	}
}
