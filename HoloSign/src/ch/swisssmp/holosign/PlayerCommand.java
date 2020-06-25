package ch.swisssmp.holosign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class PlayerCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Can only be used within the game.");
            return true;
        }
        if (args == null) return false;
        if (args.length < 1) return false;
        switch (args[0]) {
            case "spawn":
            case "place":
            case "create": {
                if (args.length < 3) {
                    sender.sendMessage("/sign create [color] [text]");
                    break;
                }
                ChatColor color;
                try {
                    String colorString = args[1];
                    color = ChatColor.valueOf(colorString.toUpperCase());
                } catch (Exception e) {
                    color = ChatColor.WHITE;
                }
                String[] textParts = Arrays.copyOfRange(args, 2, args.length);
                String text = String.join(" ", Arrays.asList(textParts));
                Player player = (Player) sender;
                ChatColor finalColor = color;
                player.getWorld().spawn(player.getEyeLocation(), ArmorStand.class, (spawned)->{
                    spawned.setVisible(false);
                    spawned.setCustomName(finalColor + text);
                    spawned.setCustomNameVisible(true);
                    spawned.setGravity(false);
                    spawned.setMarker(true);
                });
                break;
            }
            case "remove":
            case "destroy":
            case "delete": {
                int range = args.length > 1 ? Integer.parseInt(args[1]) : 5;
                String containsText = args.length > 2 ? args[2] : "";
                Player player = (Player) sender;
                List<Entity> entities = player.getNearbyEntities(range, range, range);
                int count = 0;
                for (Entity entity : entities) {
                    if (entity == null) continue;
                    if (entity.getType() != EntityType.ARMOR_STAND) continue;
                    if (entity.getCustomName() == null) continue;
                    if (!entity.isCustomNameVisible() || entity.hasGravity()) continue;
                    if (entity.getCustomName().toLowerCase().contains(containsText.toLowerCase())) {
                        entity.remove();
                        count++;
                    }
                }
                sender.sendMessage(count + " Objekt"+(count!=1?"e":"")+" entfernt.");
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            List<String> options = Arrays.asList("create", "remove");
            String current = args.length > 0 ? args[0] : "";
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }

        switch (args[0]) {
            case "create": {
                if (args.length <= 2) {
                    String current = args.length>1 ? args[1] : "";
                    List<String> options = !current.startsWith("#") ? Arrays.stream(ChatColor.values()).map(c->c.name().toLowerCase()).collect(Collectors.toList()) : Collections.emptyList();
                    return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
                }
                return Collections.singletonList("<Text>");
            }
            case "remove": {
                if (args.length <= 2) {
                    return Collections.singletonList("<Distanz>");
                }
                return Collections.singletonList("<Text>");
            }
            default:
                return Collections.emptyList();
        }
    }
}
