package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TargetSelector {

    private static final Random random = new Random();

    public static Collection<Entity> query(CommandSender sender, String selector) {
        // this pattern checks what target selector
        return query(sender, selector, Bukkit.getWorlds().stream().flatMap(w -> w.getEntities().stream()).collect(Collectors.toSet()));
    }

    public static Collection<Player> queryPlayers(CommandSender sender, String selector) {
        if (!selector.startsWith("@")) {
            return Collections.singletonList(Bukkit.getPlayer(selector));
        }
        return query(sender, selector, Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).collect(Collectors.toSet()));
    }

    private static <T extends Entity> Collection<T> query(CommandSender sender, String selector, Collection<T> entities) {
        // this pattern checks what target selector
        Pattern pattern = Pattern.compile("^@([a-z])(?:\\[(.*)])?$");
        Matcher matcher = pattern.matcher(selector);
        if (!matcher.matches()) return Collections.emptyList();
        String group = matcher.group(1);
        String argumentsString = matcher.groupCount() > 2 ? matcher.group(2) : "";
        switch (group) {
            case "p":
                return queryNearestPlayer(sender, argumentsString, entities);
            case "r":
                return queryRandomPlayer(sender, argumentsString, entities);
            case "a":
                return queryAllPlayers(sender, argumentsString, entities);
            case "e":
                return queryAllEntities(sender, argumentsString, entities);
            case "s": {
                try {
                    return querySelf(sender, argumentsString, Collections.singletonList((T) sender));
                } catch (Exception ignored) {
                    return Collections.emptyList();
                }
            }
            default:
                Bukkit.getLogger().warning("[TargetSelector] Unknown capture group " + group + "! Returning empty collection.");
                return Collections.emptyList();
        }
    }

    private static <T extends Entity> List<T> queryNearestPlayer(CommandSender sender, String argumentsString, Collection<T> entities) {
        Location from = getLocation(sender).orElse(null);
        if (from == null) return Collections.emptyList();
        Collection<T> players = queryCollection(sender, argumentsString, entities.stream().filter(e -> e.getType() == EntityType.PLAYER).collect(Collectors.toSet()));
        double closestDistance = Double.MAX_VALUE;
        T closest = null;
        for (T player : players) {
            double distance = player.getLocation().distanceSquared(from);
            if (distance >= closestDistance) continue;
            closestDistance = distance;
            closest = player;
        }
        return closest != null ? Collections.singletonList(closest) : Collections.emptyList();
    }

    private static <T extends Entity> Set<T> queryRandomPlayer(CommandSender sender, String argumentsString, Collection<T> entities) {
        List<T> players = new ArrayList<>(queryCollection(sender, argumentsString, entities.stream().filter(e -> e.getType() == EntityType.PLAYER).collect(Collectors.toSet())));
        return players.size() > 0 ? Collections.singleton(players.get(random.nextInt(players.size()))) : Collections.emptySet();
    }

    private static <T extends Entity> Set<T> queryAllPlayers(CommandSender sender, String argumentsString, Collection<T> entities) {
        return queryCollection(sender, argumentsString, entities.stream().filter(e -> e.getType() == EntityType.PLAYER).collect(Collectors.toSet()));
    }

    private static <T extends Entity> Set<T> queryAllEntities(CommandSender sender, String argumentsString, Collection<T> entities) {
        return queryCollection(sender, argumentsString, entities);
    }

    private static <T extends Entity> Set<T> querySelf(CommandSender sender, String argumentsString, Collection<T> entities) {
        return queryCollection(sender, argumentsString, entities);
    }

    private static <T extends Entity> Set<T> queryCollection(CommandSender sender, String argumentsString, Collection<T> entities) {
        Pattern pattern = Pattern.compile("\\G(?:([a-zA-Z0-9_-]+)=([a-zA-Z0-9_.<>&|$§:-]+)|([0-9.-]+))");
        Matcher matcher = pattern.matcher(argumentsString);
        Set<Argument> arguments = new HashSet<>();
        int lastMatchPos = 0;
        int argumentPosition = 0;
        while (matcher.find()) {
            Argument argument = compileArgument(argumentPosition, matcher);
            if (argument == null) return Collections.emptySet();
            arguments.add(argument);
            lastMatchPos = matcher.end();
            argumentPosition++;
        }
        if (lastMatchPos != argumentsString.length()) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>(entities);
        for (Argument argument : arguments) {
            argument.apply(sender, result);
        }

        return result;
    }

    private static Optional<Location> getLocation(CommandSender sender) {
        if (sender instanceof Entity) {
            return Optional.of(((Entity) sender).getLocation());
        }
        if (sender instanceof BlockCommandSender) {
            return Optional.of(((BlockCommandSender) sender).getBlock().getLocation());
        }
        return Optional.empty();
    }

    protected static Argument compileArgument(int argumentPosition, Matcher matcher) {
        ArgumentType type = ArgumentType.of(matcher.group(1));
        if (type == null) return null;
        String value = matcher.group(2);
        return new Argument(type, value);
    }

    private static class Argument {

        private final ArgumentType type;
        private final String value;

        public Argument(ArgumentType type, String value) {
            this.type = type;
            this.value = value;
        }

        public <T extends Entity> void apply(CommandSender sender, Collection<T> entities) {
            switch (type) {
                case TYPE: {
                    applyType(sender, value, entities);
                    break;
                }
                case DISTANCE:
                    applyDistance(sender, value, entities);
                    break;
                case X:
                case Y:
                case Z:
                case DX:
                case DY:
                case DZ:
                default: {
                    Bukkit.getLogger().warning("[TargetSelector] ArgumentType " + type + " nicht implementiert!");
                }
            }
        }

        private <T extends Entity> void applyType(CommandSender sender, String value, Collection<T> entities) {
            EntityType type = EntityType.valueOf(value.replace("minecraft:", "").toUpperCase());
            if (type == null) {
                entities.clear();
                sender.sendMessage(ChatColor.RED + "Unbekannter Typ " + value + "!");
                return;
            }
            entities.removeAll(entities.stream().filter(e -> e.getType() != type).collect(Collectors.toList()));
        }

        private <T extends Entity> void applyDistance(CommandSender sender, String value, Collection<T> entities) {
            Location center = getLocation(sender).orElse(null);
            if (center == null) {
                entities.clear();
                return;
            }
            World world = center.getWorld();
            entities.removeAll(entities.stream().filter(e -> e.getWorld() != world).collect(Collectors.toList()));
            Pattern pattern = Pattern.compile("^([0-9]+)?(?:\\.\\.([0-9+]))$");
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                entities.clear();
                sender.sendMessage(ChatColor.RED + "Ungültige Distanz " + value + "!");
                return;
            }

            double minDistance;
            double maxDistance;
            try {
                if (matcher.groupCount() > 1) {
                    minDistance = Double.parseDouble(matcher.group(1));
                    maxDistance = Double.parseDouble(matcher.group(2));
                } else {
                    minDistance = 0;
                    maxDistance = Double.parseDouble(matcher.group(1));
                }
            } catch (Exception ignored) {
                entities.clear();
                sender.sendMessage(ChatColor.RED + "Ungültige Distanz " + value + "!");
                return;
            }

            //square both distances for faster query
            double min = Math.pow(minDistance, 2);
            double max = Math.pow(maxDistance, 2);
            entities.removeAll(entities.stream().filter(e -> inRange(e.getLocation().distanceSquared(center), min, max)).collect(Collectors.toList()));
        }

        private boolean inRange(double value, double min, double max) {
            return value >= min && value <= max;
        }
    }

    private enum ArgumentType {
        TYPE,
        X,
        Y,
        Z,
        DX,
        DY,
        DZ,
        DISTANCE;

        public static ArgumentType of(String s) {
            try {
                return ArgumentType.valueOf(s.toUpperCase());
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
