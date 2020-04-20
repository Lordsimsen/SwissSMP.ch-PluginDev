package ch.swisssmp.modtools;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public class CommandRegistry {
	protected static void register() {
		
		BanCommand banCommand = new BanCommand();
		PardonCommand pardonCommand = new PardonCommand(); 
		KickCommand kickCommand = new KickCommand();
		
		PluginCommand ban = Bukkit.getPluginCommand("modtools:ban");
		PluginCommand banIp = Bukkit.getPluginCommand("modtools:ban-ip");
		PluginCommand pardon = Bukkit.getPluginCommand("modtools:pardon");
		PluginCommand pardonIp = Bukkit.getPluginCommand("modtools:pardon-ip");
		PluginCommand kick = Bukkit.getPluginCommand("modtools:kick");
		
		ban.setExecutor(banCommand);
		banIp.setExecutor(banCommand);
		pardon.setExecutor(pardonCommand);
		pardonIp.setExecutor(pardonCommand);
		kick.setExecutor(pardonCommand);
		
		ban.setTabCompleter(banCommand);
		banIp.setTabCompleter(banCommand);
		pardon.setTabCompleter(pardonCommand);
		pardonIp.setTabCompleter(pardonCommand);
		kick.setTabCompleter(pardonCommand);

		PluginCommand vanillaBan = Bukkit.getPluginCommand("ban");
		PluginCommand vanillaBanIp = Bukkit.getPluginCommand("ban-ip");
		PluginCommand vanillaPardon = Bukkit.getPluginCommand("pardon");
		PluginCommand vanillaPardonIp = Bukkit.getPluginCommand("pardon-ip");
		PluginCommand vanillaKick = Bukkit.getPluginCommand("kick");
		
		vanillaBan.setExecutor(banCommand);
		vanillaBanIp.setExecutor(banCommand);
		vanillaPardon.setExecutor(pardonCommand);
		vanillaPardonIp.setExecutor(pardonCommand);
		vanillaKick.setExecutor(kickCommand);

		vanillaBan.setTabCompleter(banCommand);
		vanillaBanIp.setTabCompleter(banCommand);
		vanillaPardon.setTabCompleter(pardonCommand);
		vanillaPardonIp.setTabCompleter(pardonCommand);
		vanillaKick.setTabCompleter(kickCommand);
		
		vanillaBan.setPermission(ban.getPermission());
		vanillaBanIp.setPermission(banIp.getPermission());
		vanillaPardon.setPermission(pardon.getPermission());
		vanillaPardonIp.setPermission(pardonIp.getPermission());
		vanillaKick.setPermission(kick.getPermission());
	}
}
