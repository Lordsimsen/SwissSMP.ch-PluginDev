package ch.swisssmp.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StallCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(args.length>0){
            if(args[0].equalsIgnoreCase("confirm")){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "broadcast Der Server wird gerade zum Absturz gebracht... Rette sich wer kann!");
                commandSender.sendMessage(ChatColor.RED+"Absturz bestätigt. Bye bye!");
                while(true){
                    // woopsie bye bye
                }
            }
            return false;
        }

        if(commandSender instanceof Player){
            JsonObject json = new JsonObject();
            json.addProperty("text", "Bist du sicher? Diese Aktion bringt den Server zum Absturz. Verwende ");
            json.addProperty("color", "red");
            JsonArray extra = new JsonArray();
            JsonObject click = new JsonObject();
            click.addProperty("text", "/stall confirm");
            JsonObject clickProperty = new JsonObject();
            clickProperty.addProperty("action", "run_command");
            clickProperty.addProperty("value", "/stall confirm");
            click.add("clickEvent", clickProperty);
            JsonObject hoverProperty = new JsonObject();
            hoverProperty.addProperty("action", "show_text");
            hoverProperty.addProperty("value", "Klicke, um den Befehl zu bestätigen");
            click.add("hoverEvent", hoverProperty);
            JsonObject extraText = new JsonObject();
            extraText.addProperty("text", " um die Aktion zu bestätigen.");
            extra.add(click);
            extra.add(extraText);
            json.add("extra", extra);
            SwissSMPler.get((Player) commandSender).sendRawMessage(json.toString());
        }
        else{
            commandSender.sendMessage("Bist du sicher? Verwende /stall confirm, um die Aktion zu bestätigen");
        }

        return true;
    }
}
