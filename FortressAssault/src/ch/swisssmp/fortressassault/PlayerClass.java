package ch.swisssmp.fortressassault;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class PlayerClass {
	private static HashMap<Integer, PlayerClass> playerClasses = new HashMap<Integer, PlayerClass>();
	
	public final int class_id;
	public final String name;
	public final String description;
	private final Inventory inventory;
	
	public PlayerClass(ConfigurationSection dataSection){
		this.class_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.description = dataSection.getString("description");
		World world = FortressAssault.getLobby();
		Block block = world.getBlockAt(dataSection.getInt("x"), dataSection.getInt("y"), dataSection.getInt("z"));
		if(block.getType()==Material.CHEST)
		{
			Chest chest = (Chest) block.getState();
			this.inventory = chest.getInventory();
		}
		else inventory = null;
		playerClasses.put(this.class_id, this);
	}
	
	protected static void setItems(Player player, PlayerClass playerClass, GameState gameState){
		PlayerInventory playerInventory = player.getInventory();
		playerInventory.clear();
		if(playerClass==null){
			return;
		}
		if(playerClass.inventory==null) return;
		switch(gameState){
		case FINISHED:
		case FIGHT:
		case PREGAME:
			for(ItemStack itemStack : playerClass.inventory){
				if(itemStack==null) continue;
				if(isHelmet(itemStack)){
					playerInventory.setHelmet(itemStack);
				}
				else if(isChestplate(itemStack)){
					playerInventory.setChestplate(itemStack);
				}
				else if(isLeggings(itemStack)){
					playerInventory.setLeggings(itemStack);
				}
				else if(isBoots(itemStack)){
					playerInventory.setBoots(itemStack);
				}
				else{
					playerInventory.addItem(itemStack);
				}
			}
			break;
		case BUILD:
			playerInventory.addItem(new ItemStack(Material.SMOOTH_BRICK, 64));
			playerInventory.addItem(new ItemStack(Material.SMOOTH_BRICK, 64));
			break;
		}
	}
	
	public static void loadClasses() throws Exception{
		playerClasses.clear();
		
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("fortress_assault/classes.php");
		for(String key : yamlConfiguration.getKeys(false)){
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(key);
			new PlayerClass(dataSection);
		}
	}
	protected static PlayerClass get(int class_id){
		return playerClasses.get(class_id);
	}
	
	private static boolean isHelmet(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_HELMET||
				material==Material.CHAINMAIL_HELMET||
				material==Material.IRON_HELMET||
				material==Material.GOLD_HELMET||
				material==Material.DIAMOND_HELMET;
	}
	
	private static boolean isChestplate(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_CHESTPLATE||
				material==Material.CHAINMAIL_CHESTPLATE||
				material==Material.IRON_CHESTPLATE||
				material==Material.GOLD_CHESTPLATE||
				material==Material.DIAMOND_CHESTPLATE;
	}
	
	private static boolean isLeggings(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_LEGGINGS||
				material==Material.CHAINMAIL_LEGGINGS||
				material==Material.IRON_LEGGINGS||
				material==Material.GOLD_LEGGINGS||
				material==Material.DIAMOND_LEGGINGS;
	}
	
	private static boolean isBoots(ItemStack itemStack){
		if(itemStack==null)return false;
		Material material = itemStack.getType();
		return 
				material==Material.LEATHER_BOOTS||
				material==Material.CHAINMAIL_BOOTS||
				material==Material.IRON_BOOTS||
				material==Material.GOLD_BOOTS||
				material==Material.DIAMOND_BOOTS;
	}
}
