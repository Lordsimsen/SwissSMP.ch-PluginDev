package ch.swisssmp.ignitabletorches;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;
import org.bukkit.material.Directional;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class IgnitableTorches extends JavaPlugin{

	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static IgnitableTorches plugin;
	
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@SuppressWarnings("deprecation")
	public static void igniteTorch(Block block){
		BlockFace direction = BlockFace.UP;
		if(block.getState().getData() instanceof Directional){
			Directional directional = (Directional) block.getState().getData();
			direction = directional.getFacing();
		}
		block.setBlockData(Bukkit.createBlockData(Material.TORCH), false);
	}
	
	private static byte getFacingByte(BlockFace face){
        switch (face) {
        case EAST:
            return 0x1;

        case WEST:
            return 0x2;

        case SOUTH:
            return 0x3;

        case NORTH:
            return 0x4;

        case UP:
        default:
            return 0x5;
        }
	}
}
