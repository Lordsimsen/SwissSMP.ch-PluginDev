package ch.swisssmp.world;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import com.wimbli.WorldBorder.cmd.CmdTrim;

public class WorldBorderPluginHandler {
	
	private WorldBorderPluginHandler() {
		
	}
	
	private void initialize() {
		Bukkit.getPluginCommand("wb").setExecutor(null);
	}
	
	public void trim(Player player, World world, ch.swisssmp.world.border.WorldBorder borderData, List<String> params) {
		BorderData border = convert(borderData);
		Config.setBorder(world.getName(), border);
		
		CmdTrim command = new CmdTrim();
		command.execute(player, player, params, world.getName());
		
		Config.removeBorder(world.getName());
	}
	
	private BorderData convert(ch.swisssmp.world.border.WorldBorder borderData) {
		int centerX = borderData.getCenterX();
		int centerZ = borderData.getCenterZ();
		int radius = borderData.getRadius();
		int margin = borderData.getMargin();
		// double x, double z, int radiusX, int radiusZ, Boolean shapeRound, boolean wrap
		return new BorderData(centerX, centerZ, radius, radius + margin, false, true);
	}
	
	protected static WorldBorderPluginHandler create() {
		WorldBorderPluginHandler result = new WorldBorderPluginHandler();
		result.initialize();
		return result;
	}
}
