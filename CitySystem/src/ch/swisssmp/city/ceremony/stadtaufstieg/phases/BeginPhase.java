package ch.swisssmp.city.ceremony.stadtaufstieg.phases;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.stadtaufstieg.CityRankCeremony;
import ch.swisssmp.city.ceremony.stadtaufstieg.CityRankCeremonyMusic;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BeginPhase extends Phase {

    private final CityRankCeremony ceremony;
    private final Block banner;

    public BeginPhase(CityRankCeremony ceremony){
        this.ceremony = ceremony;
        this.banner = ceremony.getBanner();
    }

    @Override
    public void run() {
        //nothing to run here
    }

    @Override
    public void begin(){
        playFX();
        loadMusic();
        startShaker();
        Player initiator = ceremony.getInitiator();
        for(Player player : Bukkit.getWorlds().get(0).getPlayers()){
            if(Ceremonies.isParticipantAnywhere(player)) continue;
            if(banner.getWorld()!=player.getWorld() || banner.getLocation().distanceSquared(player.getLocation())<10000) continue;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+player.getName()+" {\"text\":\"\",\"extra\":[{\"text\":\"[\u00A7cStädtesystem\u00A7r] \"},{\"text\":\""+initiator.getDisplayName()+" \u00A7r\u00A7a \u00A7ahat \u00A7aeine \u00A7aAufstiegszeremonie \u00A7agestartet! \u00A7aSchaue \u00A7amit \"},{\"text\":\"\u00A7e/zuschauen\u00A7r\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/zuschauen "+initiator.getName()+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Dieser Zeremonie zuschauen\"}},{\"text\":\"\u00A7a zu.\"}]}");
        }
    }


    /**
     * Plays the firebursteffect with as maincolor the banner's main/original color and as seoncdary color something fitting
     */
    private void playFX(){
        Color primaryColor;
        Color secondaryColor;
        switch(banner.getType()){
            case BLACK_BANNER: primaryColor = Color.fromRGB(0,0,0); secondaryColor = Color.fromRGB(40,40,40); break;
            case GRAY_BANNER: primaryColor = Color.fromRGB(40, 40, 40); secondaryColor = Color.fromRGB(0,0,0); break;
            case LIGHT_GRAY_BANNER: primaryColor = Color.fromRGB(130, 130, 130); secondaryColor = Color.fromRGB(40,40,40); break;
            case BLUE_BANNER: primaryColor = Color.fromRGB(0,0, 200); secondaryColor = Color.fromRGB(0,100,200); break;
            case LIGHT_BLUE_BANNER: primaryColor = Color.fromRGB(0, 100, 200); secondaryColor = Color.fromRGB(0,0,200); break;
            case CYAN_BANNER: primaryColor = Color.fromRGB(0, 200, 100); secondaryColor = Color.fromRGB(0,100,200); break;
            case BROWN_BANNER: primaryColor = Color.fromRGB(100, 50, 0); secondaryColor = Color.fromRGB(250,130,0); break;
            case RED_BANNER: primaryColor = Color.fromRGB(200, 0, 0); secondaryColor = Color.fromRGB(250,130,0); break;
            case ORANGE_BANNER: primaryColor = Color.fromRGB(250, 130, 0); secondaryColor = Color.fromRGB(250,250,5); break;
            case YELLOW_BANNER: primaryColor = Color.fromRGB(250, 250, 5); secondaryColor = Color.fromRGB(200,0,0); break;
            case GREEN_BANNER: primaryColor = Color.fromRGB(75, 150, 0); secondaryColor = Color.fromRGB(120,255,0); break;
            case LIME_BANNER: primaryColor = Color.fromRGB(120, 255, 0); secondaryColor = Color.fromRGB(75,150,0); break;
            case PURPLE_BANNER: primaryColor = Color.fromRGB(150, 0, 150); secondaryColor = Color.fromRGB(200,0,100); break;
            case MAGENTA_BANNER: primaryColor = Color.fromRGB(200, 0, 100); secondaryColor = Color.fromRGB(150,0,150); break;
            case PINK_BANNER: primaryColor = Color.fromRGB(250, 100, 160); secondaryColor = Color.fromRGB(150,0,150); break;
            default: primaryColor = Color.fromRGB(255,0,0); secondaryColor = Color.fromRGB(240,240,0);
        }
        FireBurstEffect fx = FireBurstEffect.play(CitySystemPlugin.getInstance(), banner, 5, primaryColor, secondaryColor);
        fx.addOnFinishListener(() ->{
            setCompleted();
        });
    }

    private void loadMusic(){
        banner.getWorld().playSound(banner.getLocation(), CityRankCeremonyMusic.drums, SoundCategory.RECORDS, 0.01f, 1);
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
            for(Player player : banner.getWorld().getPlayers()){
                player.stopSound(CityRankCeremonyMusic.drums, SoundCategory.RECORDS);
            }
        }, 2L);
    }

    private void startShaker(){
        ceremony.setMusic(banner.getLocation(), CityRankCeremonyMusic.shaker, 80L);
    }
}
