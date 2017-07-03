package ch.swisssmp.addonabnahme;

import java.net.URLEncoder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ch.swisssmp.utils.YamlConfiguration;

import ch.swisssmp.webcore.DataSource;

public class ConsoleCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		if(args.length<2) return false;
		switch(args[0]){
		case "update":{
			try{
				YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("addons/info.php", new String[]{
					"addon_instance_id="+URLEncoder.encode(args[1], "utf-8")
				});
				if(yamlConfiguration==null) return true;
				if(yamlConfiguration.contains("sign")){
					AddonAbnahme.editSign(yamlConfiguration.getConfigurationSection("sign"));
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			break;
		}
		case "clear":{
			if(args.length<4){
				return true;
			}
			int x = Integer.valueOf(args[1]);
			int y = Integer.valueOf(args[2]);
			int z = Integer.valueOf(args[3]);
			String worldName = args[4];
			World world = Bukkit.getWorld(worldName);
			if(world==null){
				sender.sendMessage("[AddonAbnahme] Konnte Addonschild nicht leeren, unbekannte Welt "+worldName+"!");
				return false;
			}
			Block block = world.getBlockAt(x, y, z);
			Material material = block.getType();
			if(material != Material.SIGN_POST && material != Material.WALL_SIGN)
				return true;
			BlockState state = block.getState();
			if(!(state instanceof Sign))
				return true;
			Sign sign = (Sign) state;
			sign.setLine(0, "");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
			break;
		}
		}
		return true;
	}

}
