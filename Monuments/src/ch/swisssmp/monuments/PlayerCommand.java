package ch.swisssmp.monuments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
                             String[] args) {
        switch (args[0].toLowerCase()) {
            case "reloadCache": {
                sender.sendMessage("Monumente werden neu geladen...");
                MonumentEntries.reload((msg) -> sender.sendMessage(msg));
                return true;
            }
        }
        return true;
    }
}
