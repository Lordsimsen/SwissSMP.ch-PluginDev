package ch.swisssmp.adventuredungeons.mmocamp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class MmoCampEditor implements Listener {
	public static HashMap<Player, MmoCampEditor> editors = new HashMap<Player, MmoCampEditor>();
	
	public final Player player;
	public ItemStack itemStack;
	public int mmo_spawnpoint_id;
	
	public MmoCampEditor(Player player, ItemStack itemStack, int mmo_spawnpoint_id){
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		this.player = player;
		this.itemStack = itemStack;
		this.mmo_spawnpoint_id = mmo_spawnpoint_id;
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		itemMeta.setDisplayName("Spawnpunkt "+this.mmo_spawnpoint_id+" Editor");
		this.itemStack.setItemMeta(itemMeta);
		editors.put(this.player, this);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockPlace(BlockPlaceEvent event){
		ItemStack itemStack = event.getItemInHand();
		if(itemStack==null)
			return;
		if(!itemStack.isSimilar(itemStack)){
			return;
		}
		this.itemStack = itemStack;
		event.setCancelled(true);
		Block block = event.getBlock();
		String spawnpoint = "spawnpoint="+this.mmo_spawnpoint_id;
		String x = "x="+block.getX();
		String y = "y="+block.getY();
		String z = "z="+block.getZ();
		YamlConfiguration response = DataSource.getYamlResponse("campeditor.php", new String[]{spawnpoint, x, y, z});
		ConfigurationSection spawnpoints = response.getConfigurationSection("spawnpoints");
		if(spawnpoints==null){
			player.sendMessage(ChatColor.RED+"Beim bearbeiten der Punkte ist ein Fehler aufgetreten.");
			return;
		}
		int current_count = spawnpoints.getKeys(false).size();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("Spawnpunkt "+this.mmo_spawnpoint_id+" Editor ("+current_count+" Spawnpunkte)");
		this.itemStack.setItemMeta(itemMeta);
		for(String spawnpointStringID : spawnpoints.getKeys(false)){
			ConfigurationSection spawnpointSection = spawnpoints.getConfigurationSection(spawnpointStringID);
			int spawnpoint_x = spawnpointSection.getInt("x");
			int spawnpoint_y = spawnpointSection.getInt("y");
			int spawnpoint_z = spawnpointSection.getInt("z");
			player.spawnParticle(Particle.NOTE, new Location(player.getWorld(), spawnpoint_x, spawnpoint_y, spawnpoint_z).add(0.5, 0.5, 0.5), 1);
		}
	}
	
	public void quit(){
		editors.remove(this.player);
		HandlerList.unregisterAll(this);
		if(this.itemStack!=null){
			this.itemStack.setItemMeta(new ItemStack(Material.GOLD_BLOCK).getItemMeta());
		}
	}
	
	public static MmoCampEditor get(Player player){
		return editors.get(player);
	}
}
