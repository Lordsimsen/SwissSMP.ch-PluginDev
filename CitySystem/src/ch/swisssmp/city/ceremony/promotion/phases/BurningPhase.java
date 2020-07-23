package ch.swisssmp.city.ceremony.promotion.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.effects.CityCeremonyCircleEffect;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremonyMusic;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.scheduler.BukkitTask;

public class BurningPhase extends Phase implements Listener {

    private final CityPromotionCeremony ceremony;
    private final Block chest;

    private BukkitTask ignitionReminder;
    private BukkitTask failureReminder;
    private BukkitTask autoCancel;

    private boolean fireIgnited;

    public BurningPhase(CityPromotionCeremony ceremony){
        this.ceremony = ceremony;
        this.chest = ceremony.getChest();
    }

    @Override
    public void begin(){
        if(CityPromotionCeremony.getNearbyPlayers(chest.getLocation()).size() < ceremony.getCeremonyParameters().getPromotionPlayercount()) {
            cancel();
        }
        super.begin();

        fireIgnited = false;

        ceremony.setRingEffect(new CityCeremonyCircleEffect(chest.getLocation().add(0.5,0.5,0.5)));
        ceremony.getRingEffect().setRadius(5);
        ceremony.getRingEffect().setRingEffectType(CityCeremonyCircleEffect.RingEffectType.WhirlingBlade);
        for(int i = 0; i < 5; i++){
            if((i+1) % 5 == 0) {
                ceremony.getRingEffect().setColor(i, Color.fromRGB(55, 242, 255));
                continue;
            }
            if((i+1) % 2 == 0) {
                ceremony.getRingEffect().setColor(i, Color.fromRGB(255, 215, 0));
                continue;
            }
            if((i+1) % 2 == 1) {
                ceremony.getRingEffect().setColor(i, Color.fromRGB(210, 219, 210));
                continue;
            }
        }
        ceremony.setRingEffectTask(Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), ceremony.getRingEffect(), 0, 1));

        Location location = chest.getLocation().add(ceremony.random.nextInt(2), 0, ceremony.random.nextInt(2));
        chest.getWorld().strikeLightning(new Location(chest.getWorld(), location.getX(), location.add(0,1,0).getY(), location.getZ()));
        ignitionReminder = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), () ->{
            broadcastTitleParticipants("", ChatColor.GOLD + "Entzündet das Feuer!");
        }, 0, 200);
        failureReminder = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), () ->{
            broadcastTitleParticipants("", ChatColor.RED + "Alles Heu muss verbrennen!");
        }, 1200, 600);
        autoCancel = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ceremony::cancel, 12000);

        startMusic();
    }

    private void startMusic(){
        for(Player player : ceremony.getPlayers()){
            player.stopSound(CityPromotionCeremonyMusic.shaker, SoundCategory.RECORDS);
        }
        ceremony.setMusic(chest.getLocation(), CityPromotionCeremonyMusic.finale, 932);
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () ->{
            ceremony.setMusic(chest.getLocation(), CityPromotionCeremonyMusic.drums, 932);
        }, 48L);
    }

    @Override
    public void run() {
        if(fireIgnited) {
            double randomDouble = ceremony.random.nextDouble();
            if (randomDouble < 0.1) playRandomLocatedFireBurst(chest.getLocation(), 2);
        }
    }

    private void playRandomLocatedFireBurst(Location location, int bound){
        Location hayPileCorner = location.add(-bound,-bound,-bound);
        int x = ceremony.random.nextInt(4);
        int y = ceremony.random.nextInt(3);
        int z = ceremony.random.nextInt(4);
        Location effectLocation = hayPileCorner.add(x,-y,z);
        FireBurstEffect.play(CitySystemPlugin.getInstance(), effectLocation.getBlock()
                , 5, Color.fromRGB(255, 150, 0), Color.fromRGB(255, 100, 0));
    }

    @Override
    public void finish(){
        super.finish();
        if(ignitionReminder != null || !ignitionReminder.isCancelled()) ignitionReminder.cancel();
        if(failureReminder != null || !failureReminder.isCancelled()) failureReminder.cancel();
        if(autoCancel != null || !autoCancel.isCancelled()) autoCancel.cancel();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void cancel(){
        super.cancel();
        this.broadcastTitleParticipants(ChatColor.RED + "Abbruch!", ChatColor.YELLOW + "Ihr habt versagt.");
        ceremony.cancel();
    }

    private void broadcastTitleParticipants(String title, String subtitle){
        for(Player player : ceremony.getPlayers()){
            SwissSMPler.get(player).sendTitle(title, subtitle);
        }
    }

    @EventHandler
    private void onIgnition(BlockIgniteEvent event){
        Bukkit.getLogger().info("BlockigniteEvent");
        if(!ceremony.getPlayers().contains(event.getPlayer())) return;
        if(ignitionReminder != null || !ignitionReminder.isCancelled()) ignitionReminder.cancel();
        fireIgnited = true;
    }

    @EventHandler
    private void onHayBurn(BlockBurnEvent event){
        Block block = event.getBlock();
        if(!block.getType().equals(CityPromotionCeremony.baseMaterial) && !block.getType().equals(Material.CHEST)) {
            return;
        }
        if(!block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).equals(chest) && !block.equals(chest)) {
            return;
        }
        setCompleted();
    }
}
