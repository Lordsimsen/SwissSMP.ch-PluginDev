package ch.swisssmp.utils;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HTTPRequest request = DataSource.getResponse(UtilityCommandsPlugin.getInstance(), "help.php");
        request.onFinish(() -> {
            YamlConfiguration yamlConfiguration = request.getYamlResponse();
            if (yamlConfiguration == null || !yamlConfiguration.contains("message")) {
                sender.sendMessage("[Hilfe] https://swisssmp.ch/forums/serverinfos.1/");
                return;
            }
            for (String line : yamlConfiguration.getStringList("message"))
                sender.sendMessage(line);
        });
        return true;
    }
}
