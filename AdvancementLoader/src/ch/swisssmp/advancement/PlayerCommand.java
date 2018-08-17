package ch.swisssmp.advancement;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.swisssmp.utils.FileUtil;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		World[] worlds = Bukkit.getWorlds().toArray(new World[Bukkit.getWorlds().size()]);
		//i startet bei 2 weil die ersten beiden Welten immer Valinor und Valinor_the_end sind; Diese benötigen die Advancement-Kopien nicht
		File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName()+"/data/advancements");
		for(int i = 2; i < worlds.length; i++){
			File worldAdvancementsFile = new File(Bukkit.getWorldContainer(), worlds[i].getName()+"/data/advancements");
			if(worldAdvancementsFile.exists()){
				FileUtil.deleteRecursive(worldAdvancementsFile);
			}
			FileUtil.copyDirectory(mainWorldAdvancementsFile, worldAdvancementsFile);
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:reload");
		sender.sendMessage("[AdvancementLoader] Advancements aktualisiert.");
		return true;
	}

}
