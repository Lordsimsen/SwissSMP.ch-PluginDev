package ch.swisssmp.city.ceremony.promotion.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremonyMusic;
import ch.swisssmp.utils.Random;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.Blocks;
import net.minecraft.server.v1_16_R1.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ClimaxPhase extends Phase {

    private static final int maxItemPops = 30;

    private final CityPromotionCeremony ceremony;
    private final Block chest;
    private final Random random;

    private BukkitTask musicTask;

    private ItemStack[] tribute;
    private boolean awaitingCompletion;
    private int itemPops;

    public ClimaxPhase(CityPromotionCeremony ceremony){
        this.ceremony = ceremony;
        this.chest = ceremony.getChest();
        this.random = ceremony.random;
    }

    @Override
    public void begin(){
        super.begin();
        tribute = ((Chest) chest.getState()).getBlockInventory().getContents();
        itemPops = 0;
        startMusic();
    }

    private void startMusic(){
        for(Player player : ceremony.getPlayers()){
            player.stopSound(CityPromotionCeremonyMusic.shaker, SoundCategory.RECORDS);
            player.stopSound(CityPromotionCeremonyMusic.drums, SoundCategory.RECORDS);
        }
        musicTask = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () ->{
            ceremony.setMusic(chest.getLocation(), CityPromotionCeremonyMusic.finale, 932);
        }, 0L);
    }

    @Override
    public void run() {
        if(!isEmpty(tribute)){
            int i = random.nextInt(tribute.length);
            if(tribute[i] == null || tribute[i].getType() == Material.AIR) return;
            Item droppedItem = chest.getWorld().dropItemNaturally(chest.getLocation().add(0,0.5,0), new ItemStack(tribute[i].getType()));
            openChest(chest.getLocation());

            droppedItem.setPickupDelay(Integer.MAX_VALUE);
            droppedItem.setGravity(false);
            double randomDouble = random.nextDouble();
            double x = random.nextInt(21)*0.01;
            double y = random.nextInt(21)*0.01;
            double z = random.nextInt(21)*0.01;
            if(randomDouble < 0.25) {
                x = -1 * x;
            }
            if(randomDouble < 0.5 && randomDouble >= 0.25) {
                z = -1 * z;
            }
            if(randomDouble < 0.75 && randomDouble >= 0.5) {
                x = -1 * x; z = -1 * z;}
            droppedItem.setVelocity(new Vector(x, y, z));

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(!droppedItem.isValid()){
                        this.cancel();
                        return;
                    }
                    droppedItem.setVelocity(droppedItem.getVelocity().add(new Vector(0, 0.2, 0)));
                }
            };
            runnable.runTaskTimer(CitySystemPlugin.getInstance(), 10L, 5L);

            Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), droppedItem::remove, 60L);

            tribute[i].setAmount(tribute[i].getAmount() - 1);
//            itemPops++;

            if(randomDouble < 0.5){
                Color color;
                int randomInt = random.nextInt(3);
                switch(randomInt){
                    case 0: {
                        color = Color.fromRGB(192, 192, 192);
                        break;
                    }
                    case 1: {
                        color = Color.fromRGB(255, 215, 0);
                        break;
                    }
                    case 2: {
                        color = Color.fromRGB(185, 242, 255);
                        break;
                    }
                    default: color = Color.fromRGB(255,115,0);
                }
                playFireBurst(chest.getLocation(), color);
                chest.getWorld().playSound(chest.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
            }
        } else {
            this.setCompleted();
        }
    }

    @Override
    public void finish(){
        super.finish();
        spawnExplosions(chest.getLocation());
        if(musicTask != null || !musicTask.isCancelled()) musicTask.cancel();
    }

    public void completeLater(long l){
        awaitingCompletion = true;
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), this::setCompleted, l);
    }

    private boolean isEmpty(ItemStack[] content){
        for(int i = 0; i < content.length; i++){
            if(content[i] == null || content[i].getType() == Material.AIR) continue;
            if(content[i].getAmount() > 0) return false;
        }
        return true;
    }

    /**
     * Uses suboptimal imports in order to open make the chest appear as opening when an item is thrown out.
     */
    private void openChest(Location loc){
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.CHEST, (byte) 1, (byte) 1);
        for(Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void playFireBurst(Location location, Color color){
        Location effectLocation = location.add(0,0.5,0);
        FireBurstEffect.play(CitySystemPlugin.getInstance(), effectLocation.getBlock(), 5, Color.OLIVE, color);
        chest.getWorld().playSound(effectLocation, Sound.ITEM_FIRECHARGE_USE, 1, 1);
    }

    private void spawnExplosions(Location location){
        BukkitTask explosions = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), () ->{
            Location loc = location.add(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5);
            loc.getWorld().strikeLightning(loc);
            loc.getWorld().createExplosion(loc, 1, false);
        }, 0L, 4L);
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), explosions::cancel, 21L);
    }
}
