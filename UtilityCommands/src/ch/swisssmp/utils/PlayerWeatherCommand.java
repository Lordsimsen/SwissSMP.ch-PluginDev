package ch.swisssmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerWeatherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("/pweather kann nur ingame verwendet werden.");
            return true;
        }
        Player player = (Player) sender;
        switch(args[0]){
            case "set":{
                String weather = args[1];
                WeatherType weatherType;
                switch(weather){
                    case "clear":
                    case "sun":
                    {
                        weatherType = WeatherType.CLEAR;
                        break;
                    }
                    case "rain":
                    case "downfall":
                    {
                        weatherType = WeatherType.DOWNFALL;
                        break;
                    }
                    default: weatherType = WeatherType.CLEAR;
                }
                player.setPlayerWeather(weatherType);
                player.sendMessage(ChatColor.AQUA + "Persönliches Wetter auf " + ChatColor.DARK_AQUA + weatherType.toString() + ChatColor.AQUA + " gesetzt.");
                return true;
            }
            case "reset":{
                player.resetPlayerWeather();
                player.sendMessage(ChatColor.AQUA + "Persönliches Wetter mit Serverwetter synchronisiert.");
                return true;
            }
            default: return false;
        }
    }
}
