package ch.swisssmp.fortressassault;

import java.util.HashMap;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
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
				if(ItemUtil.isHelmet(itemStack)){
					playerInventory.setHelmet(itemStack);
				}
				else if(ItemUtil.isChestplate(itemStack)){
					playerInventory.setChestplate(itemStack);
				}
				else if(ItemUtil.isLeggings(itemStack)){
					playerInventory.setLeggings(itemStack);
				}
				else if(ItemUtil.isBoots(itemStack)){
					playerInventory.setBoots(itemStack);
				}
				else{
					playerInventory.addItem(itemStack);
				}
			}
			break;
		case BUILD:
			playerInventory.addItem(new ItemStack(Material.STONE_BRICKS, 64));
			playerInventory.addItem(new ItemStack(Material.STONE_BRICKS, 64));
			break;
		}
	}
	
	public static void loadClasses() throws Exception{
		playerClasses.clear();

		HTTPRequest request = DataSource.getResponse(FortressAssault.getInstance(), "classes.php");
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			for(String key : yamlConfiguration.getKeys(false)){
				ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(key);
				new PlayerClass(dataSection);
			}
		});
	}
	protected static PlayerClass get(int class_id){
		return playerClasses.get(class_id);
	}
}
