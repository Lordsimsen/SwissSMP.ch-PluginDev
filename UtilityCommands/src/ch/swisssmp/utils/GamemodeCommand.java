package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public final class GamemodeCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player;
		if (args.length > 0) {
			player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				if (sender instanceof Player) {
					player = (Player) sender;
					player.setGameMode(commandArgumentToGamemode(args[0]));
					return true;
				} else {
					sender.sendMessage("/gm kann nur ingame verwendet werden.");
					return true;
				}
			} else{
				player.setGameMode(commandArgumentToGamemode(args[1]));
				return true;
			}
		} else{
			if(!(sender instanceof Player)){
				sender.sendMessage("/gm kann nur ingame verwendet werden.");
				return true;
			} else{
				player = (Player) sender;
				switch(player.getGameMode()){
					case SURVIVAL:{
						player.setGameMode(GameMode.CREATIVE);
						return true;
					}
					case CREATIVE:
					case ADVENTURE:
					case SPECTATOR:
						{
						player.setGameMode(GameMode.SURVIVAL);
						return true;
					}
				}
			}
		}
		return false;
	}

	private static GameMode commandArgumentToGamemode(String string){
		switch (string) {
			case "0":
			case "survival": {
				return GameMode.SURVIVAL;
			}
			case "1":
			case "creative": {
				return GameMode.CREATIVE;
			}
			case "2":
			case "adventure": {
				return GameMode.ADVENTURE;
			}
			case "3":
			case "spectator": {
				return GameMode.SPECTATOR;
			}
			default: return GameMode.SURVIVAL;
		}
	}
}
