package ch.swisssmp.city.ceremony.promotion.phases;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremonyMusic;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class BeginningPhase extends Phase {

    private final CityPromotionCeremony ceremony;
    private final Block chest;

    private BukkitTask particleTask;

    public BeginningPhase(CityPromotionCeremony ceremony){
        this.ceremony = ceremony;
        this.chest = ceremony.getChest();
    }

    @Override
    public void run() {
        //nothing to run here
    }

    @Override
    public void begin(){
        loadMusic();
        startShaker();
        Player initiator = ceremony.getInitiator();
        initiator.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + "Versammle deine Bürger um den Altar! Die Zeremonie beginnt in einer Stunde!");
        broadcastTitle("", ChatColor.LIGHT_PURPLE + "Die Zeremonie beginnt in einer Stunde!");
        for(Player player : Bukkit.getWorlds().get(0).getPlayers()){
            if(Ceremonies.isParticipantAnywhere(player)) continue;
            if(chest.getWorld()!=player.getWorld() || chest.getLocation().distanceSquared(player.getLocation())<10000) continue;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+player.getName()+" {\"text\":\"\",\"extra\":[{\"text\":\"[\u00A7cStädtesystem\u00A7r] \"},{\"text\":\""+initiator.getDisplayName()+" \u00A7r\u00A7a \u00A7ahat \u00A7aeine \u00A7aAufstiegszeremonie \u00A7agestartet! \u00A7aSchaue \u00A7amit \"},{\"text\":\"\u00A7e/zuschauen\u00A7r\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/zuschauen "+initiator.getName()+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Dieser Zeremonie zuschauen\"}},{\"text\":\"\u00A7a zu.\"}]}");
        }

        particleTask = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), () ->{
            FireBurstEffect.play(CitySystemPlugin.getInstance(), chest, 5, Color.fromRGB(255,215,0), Color.fromRGB(0,255,255));
        }, 1, 100);

        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), this::setCompleted, 999L);
    }

    @Override
    public void cancel(){
        super.cancel();
        ceremony.cancel();
    }

    @Override
    public void finish(){
        super.finish();
        if(particleTask != null || !particleTask.isCancelled()) {
            particleTask.cancel();
        }
    }

    private void loadMusic(){
        chest.getWorld().playSound(chest.getLocation(), CityPromotionCeremonyMusic.drums, SoundCategory.RECORDS, 0.01f, 1);
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
            for(Player player : chest.getWorld().getPlayers()){
                player.stopSound(CityPromotionCeremonyMusic.drums, SoundCategory.RECORDS);
            }
        }, 2L);
    }

    private void startShaker(){
        ceremony.setMusic(chest.getLocation(), CityPromotionCeremonyMusic.shaker, 80L);
    }

    private void broadcastTitle(String title, String subtitle){
        for(Player player : CityPromotionCeremony.getNearbyPlayers(chest.getLocation())){
            SwissSMPler.get(player).sendTitle(title, subtitle);
        }
    }
}
