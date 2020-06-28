package ch.swisssmp.chatmanager;

import ch.swisssmp.text.RawText;
import ch.swisssmp.utils.TargetSelector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TellCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<2) return false;
        UUID senderUid = (sender instanceof Entity) ? ((Entity) sender).getUniqueId() : null;
        String senderName = (sender instanceof Player)
                ? ((Player) sender).getDisplayName()
                : sender instanceof Entity && ((Entity) sender).getCustomName()!=null
                    ? ((Entity) sender).getCustomName()
                    : sender.getName();
        String worldName = (sender instanceof Entity)
                ? ((Entity) sender).getWorld().getName()
                : sender instanceof BlockCommandSender
                    ? ((BlockCommandSender) sender).getBlock().getWorld().getName()
                    : null;
        String target = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if(target.startsWith("@")){
            if(!sender.hasPermission("chat.tell.targetselector")){
                sender.sendMessage(ChatColor.RED+"Keine Berechtigung");
                return true;
            }

            Collection<Player> targets = TargetSelector.queryPlayers(sender, target);
            for(Player recipient : targets){
                RawText formattedMessage = getFormattedMessage(senderName, ChatColor.GRAY+"ich", message);
                recipient.spigot().sendMessage(formattedMessage.spigot());
                ChatManager.log(senderUid, senderName, recipient.getName(), worldName, message);
                if(senderUid==null) continue;
                PlayerConversations.setConversationPartner(recipient.getUniqueId(), senderUid);
            }

            sender.sendMessage("Nachricht an "+targets.size()+" Spieler versendet.");
            return true;
        }
        Player recipient = Bukkit.getPlayer(target);
        if(recipient==null){
            sender.sendMessage(ChatColor.RED+target+" nicht gefunden.");
            return true;
        }

        RawText recipientMessage = getFormattedMessage(senderName, ChatColor.GRAY+"ich", message);
        RawText senderMessage = getFormattedMessage(ChatColor.GRAY+"ich", recipient.getDisplayName(), message);
        sender.spigot().sendMessage(senderMessage.spigot());
        recipient.spigot().sendMessage(recipientMessage.spigot());
        ChatManager.log(senderUid, senderName, recipient.getName(), worldName, message);

        Bukkit.getLogger().info(recipientMessage.toString());

        if(senderUid!=null){
            PlayerConversations.setConversationPartner(senderUid, recipient.getUniqueId());
            PlayerConversations.setConversationPartner(recipient.getUniqueId(), senderUid);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String current = args.length>0 ? args[args.length-1] : "";
        List<String> options = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
    }

    public static RawText getFormattedMessage(String sender, String recipient, String message){
        return new RawText(
                new RawText("[").color(ChatColor.DARK_GRAY),
                new RawText(sender),
                new RawText(" >> ").color(ChatColor.DARK_GRAY),
                new RawText(recipient),
                new RawText("] ").color(ChatColor.DARK_GRAY),
                new RawText(message).color(ChatColor.GOLD));
    }
}
