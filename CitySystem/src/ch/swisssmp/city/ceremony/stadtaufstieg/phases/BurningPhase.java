package ch.swisssmp.city.ceremony.stadtaufstieg.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.stadtaufstieg.CityRankCeremony;
import ch.swisssmp.city.ceremony.stadtaufstieg.CityRankCeremonyMusic;
import ch.swisssmp.utils.Random;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class BurningPhase extends Phase implements Listener {

    private final CityRankCeremony ceremony;
    private final Block banner;

    private final Random random = new Random();

    public BurningPhase(CityRankCeremony ceremony){
        this.ceremony = ceremony;
        this.banner = ceremony.getBanner();
    }

    @Override
    public void begin(){
        super.begin();
        startMusic();
    }

    private void startMusic(){
        for(Player player : ceremony.getPlayers()){
            player.stopSound(CityRankCeremonyMusic.shaker, SoundCategory.RECORDS);
        }
        ceremony.setMusic(banner.getLocation(), CityRankCeremonyMusic.finale, 932);
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () ->{
            ceremony.setMusic(banner.getLocation(), CityRankCeremonyMusic.drums, 932);
        }, 48L);
    }

    @Override
    public void run() {
        double randomDouble = random.nextDouble();
        if(randomDouble < 0.125) playRandomLocatedFireBurst();
    }

    private void playRandomLocatedFireBurst(){
        Location hayPileCorner = banner.getLocation().add(-2,-2,-2);
        int x = random.nextInt(4);
        int y = random.nextInt(3);
        int z = random.nextInt(4);
        Location effectLocation = hayPileCorner.add(x,y,z);
        FireBurstEffect.play(CitySystemPlugin.getInstance(), effectLocation.getBlock()
                , 5, Color.fromRGB(255, 150, 0), Color.fromRGB(255, 100, 0));
        banner.getWorld().playSound(effectLocation, Sound.ITEM_FIRECHARGE_USE, 1, 1);
    }

    @Override
    public void finish(){
        super.finish();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onHayBurn(BlockBurnEvent event){
        Block block = event.getBlock();
        if(!block.getType().equals(CityRankCeremony.baseMaterial)) return;
        if(!block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).equals(banner)) return;
        banner.setType(Material.AIR); //TODO this okay? not needed in ClimaxPhase? Bannerintel should have been saved upon placement ergo start of the ceremony, right?
        setCompleted();
    }

}
