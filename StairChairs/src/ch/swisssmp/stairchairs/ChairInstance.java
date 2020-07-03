package ch.swisssmp.stairchairs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class ChairInstance {

    private final Player player;
    private final ArmorStand armorStand;
    private final Block block;
    private final Location before;

    private ChairInstance(Player player, ArmorStand armorStand, Block block, Location before){
        this.player = player;
        this.armorStand= armorStand;
        this.block = block;
        this.before = before;
    }

    public Player getPlayer(){
        return player;
    }

    public ArmorStand getArmorStand(){
        return armorStand;
    }

    public Block getBlock(){
        return block;
    }

    public Location getBefore(){
        return before;
    }

    public void unsit(){
        if(armorStand.isValid() && armorStand.getPassengers().size()>0) armorStand.eject();
        Bukkit.getScheduler().runTaskLater(StairChairsPlugin.getInstance(), ()->{
            before.setDirection(player.getLocation().getDirection());
            player.teleport(before);
        }, 1L);
        armorStand.remove();

        ChairInstances.remove(this);
    }

    protected static ChairInstance create(Player player, Block block, Location chairLocation, Location before){
        final Location soundLocation = chairLocation.clone().add(0, 1, 0);
        ArmorStand armorStand = chairLocation.getWorld().spawn(chairLocation, ArmorStand.class, (spawned)->{
            spawned.setInvulnerable(true);
            spawned.setVisible(false);
            spawned.setGravity(false);
            spawned.setCustomName("§cStairChair");
            spawned.setCustomNameVisible(false);
            spawned.setMarker(true);
        });
        armorStand.addPassenger(player);
        soundLocation.getWorld().playSound(soundLocation, BlockChecker.isWood(block) ? Sound.BLOCK_WOOD_STEP : Sound.BLOCK_STONE_STEP, SoundCategory.BLOCKS, 1, 2f);
        ChairInstance result = new ChairInstance(player, armorStand, block, before);
        ChairInstances.add(result);
        return result;
    }
}
