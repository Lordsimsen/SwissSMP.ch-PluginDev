package ch.swisssmp.utils;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeenCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length < 1) return false;
        HTTPRequest request = DataSource.getResponse(UtilityCommandsPlugin.getInstance(), "seen.php", new String[]{"player=" + URLEncoder.encode(args[0])});
        request.onFinish(() -> {
            YamlConfiguration yamlConfiguration = request.getYamlResponse();
            if (yamlConfiguration == null || !yamlConfiguration.contains("message")) return;
            for (String line : yamlConfiguration.getStringList("message"))
                sender.sendMessage(line);
        });
        return true;
    }
}
