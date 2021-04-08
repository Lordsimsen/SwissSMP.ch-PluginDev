package ch.swisssmp.city.commands;

import ch.swisssmp.city.*;
import ch.swisssmp.utils.BlockUtil;
import ch.swisssmp.utils.PlayerData;
import ch.swisssmp.utils.SwissSMPler;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CityCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) return false;
        String key = args[1];
        if (key.length() < 2) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + " Name muss mindestens zwei Zeichen lang sein.");
            return true;
        }
        City city = CitySystem.findCity(key).orElse(null);
        if (city == null) {
            sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Stadt " + key + " nicht gefunden.");
            return true;
        }
        switch (args[0]) {
            case "remove":
            case "delete": {
                city.delete((success) -> {
                    if (success) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Stadt " + city.getName() + " gelöscht.");
                    } else {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Konnte Stadt " + city.getName() + " nicht löschen.");
                    }
                });
                return true;
            }
            case "ring": {

                // /city ring <Stadt> [<Besitzer>] <Typ>

                if (!(sender instanceof Player)) {
                    sender.sendMessage(CitySystemPlugin.getPrefix() + " Befehl kann nur ngame verwendet werden.");
                    return true;
                }
                if (args.length < 3) return false;
                Player player = (Player) sender;
                PlayerData owner;
                SigilRingType ringType;
                if (args.length > 3) {
                    ringType = SigilRingType.of(args[3]);
                    Citizenship citizenship = city.getCitizenship(args[2]).orElse(null);
                    if (citizenship == null) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Bürger " + args[2] + " nicht gefunden.");
                        return true;
                    }
                    owner = citizenship.getPlayerData();
                } else {
                    ringType = SigilRingType.of(args[2]);
                    owner = PlayerData.get(player);
                }

                ItemStack itemStack = ringType.createItemStack(city, owner);
                player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                return true;
            }
            case "reload": {
                city.reload((success) -> {
                    if (success) {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Stadt " + city.getName() + " aktualisiert.");
                    } else {
                        sender.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Konnte Stadt " + city.getName() + " nicht aktualisieren.");
                    }
                });
                return true;
            }
            case "create": {
                Location location = Bukkit.getPlayer(sender.getName()).getLocation();
                Block origin = BlockUtil.getClosest(location, 2, (current) -> current.getType() == Material.FIRE);
                long time = origin.getWorld().getTime();

                CitySystem.createCity(args[1], Bukkit.getPlayer(args[2]), Lists.newArrayList(Bukkit.getPlayer(args[2])), SigilRingType.EMERALD_DAISY_RING, origin,time, this::sendMessage);

            }
            default:
                return false;
        }
    }

    private void sendMessage(City city) {

    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            Collection<String> options = Arrays.asList("remove", "ring", "reload");
            String current = args.length > 0 ? args[0] : null;
            return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
        }
        if(args.length==2){
            Collection<String> cities = CitySystem.getCities().stream().map(City::getName).collect(Collectors.toList());
            String current = args[1];
            return StringUtil.copyPartialMatches(current, cities, new ArrayList<>());
        }
        switch(args[0]){
            case "ring":{
                City city = CitySystem.findCity(args[1]).orElse(null);
                if(city==null) return null;
                if(args.length==3){
                    List<String> options = Stream.concat(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName),Arrays.stream(SigilRingType.values()).map(t->t.toString().toLowerCase())).collect(Collectors.toList());
                    String current = args[2];
                    return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
                }
                if(args.length==4){
                    List<String> options = Arrays.stream(SigilRingType.values()).map(t->t.toString().toLowerCase()).collect(Collectors.toList());
                    String current = args[3];
                    return StringUtil.copyPartialMatches(current, options, new ArrayList<>());
                }
            }
            default: return null;
        }
    }
}
