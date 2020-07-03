package ch.swisssmp.stairchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ChairInstances {
    private static final Set<ChairInstance> instances = new HashSet<>();

    protected static void add(ChairInstance instance){
        instances.add(instance);
    }

    protected static void remove(ChairInstance instance){
        instances.remove(instance);
    }

    protected static Optional<ChairInstance> getInstance(Player player){
        return instances.stream().filter(i->i.getPlayer()==player).findAny();
    }

    protected static Optional<ChairInstance> getInstance(Block block){
        return instances.stream().filter(i->i.getBlock()==block).findAny();
    }

    protected static Optional<ChairInstance> getInstance(ArmorStand armorStand){
        return instances.stream().filter(i->i.getArmorStand()==armorStand).findAny();
    }

    public static Collection<ChairInstance> getAll(){
        return instances;
    }
}
