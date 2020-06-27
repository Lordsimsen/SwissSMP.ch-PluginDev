package ch.swisssmp.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChooseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        if(args==null) return true;
        if(args.length<2) return true;
        Player player = (Player) sender;
        String requestIDString = args[0];
        ChatRequest request = ChatRequest.get(requestIDString);
        if(request==null) return true;
        String key = args[1];
        request.choose(player, key);
        return true;
    }
}
