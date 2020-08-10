package ch.swisssmp.city.ceremony.promotion.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.City;
import ch.swisssmp.city.CityLevel;
import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import ch.swisssmp.city.ceremony.promotion.PromotionCeremonyData;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;


public class EndingPhase extends Phase {

    private final CityPromotionCeremony ceremony;
    private final City city;
    private final PromotionCeremonyData parameters;
    private final Block chest;

    private boolean promotionCompleted = false;
    private long time = 0;

    public EndingPhase(CityPromotionCeremony ceremony){
        this.ceremony = ceremony;
        this.city = ceremony.getCity();
        parameters = ceremony.getCeremonyParameters();
        this.chest = ceremony.getChest();
    }

    @Override
    public void begin(){
        super.begin();
        CityLevel level = parameters.getLevel();
        city.unlockLevel(level, (success)->{
            if(this.isCancelled()) return;
            if(success) announcePromotion();
            else{
                ceremony.broadcast(CitySystemPlugin.getPrefix()+ChatColor.RED+" Etwas ist schiefgelaufen und er Stadtaufstieg konnte nicht vollzogen werden. Bitte kontaktiert die Spielleitung.");
            }
            promotionCompleted = true;
        });
    }

    private void announcePromotion(){
        CityLevel level = parameters.getLevel();
        JsonObject configuration = level.getConfiguration();
        String subtitle = (configuration!=null && configuration.has("promotion_message")
                ? JsonUtil.getString("promotion_message", configuration)
                : "{name} hat die Stadtstufe "+level.getName()+" erreicht!").replace("{name}", city.getName()).replace("{level}", level.getName());
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () ->{
            String title = ChatColor.GREEN + "Gratulation!";
            this.announceTitleLong(title, subtitle);
            this.broadcastMessage(subtitle);
        }, 20L);
        playMusicFinale();
    }

    private void announceTitleLong(String title, String subtitle){
        for(Player player : ceremony.getParticipants()){
            player.sendTitle(title, subtitle, 10, 120, 30);
        }
    }

    private void broadcastMessage(String message){
        Bukkit.getServer().broadcastMessage(CitySystemPlugin.getPrefix() + " " + message);
    }

    @Override
    public void run() {
        if(!promotionCompleted) return;
        time++;
        if(time%15 == 0) this.spawnFireworks();
        if(time>(parameters.getFireworkCycles()*15)){
            setCompleted();
        }
    }

    private void playMusicFinale(){
        for(Player player : ceremony.getPlayers()){
            player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
        }
        chest.getWorld().playSound(chest.getLocation(), "founding_ceremony_finale", 15, 1);
    }

    private void spawnFireworks(){
        Location location = chest.getLocation().add(ceremony.random.nextInt(7)-3, ceremony.random.nextInt(1)-2, ceremony.random.nextInt(7)-3);
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();

        int rt = ceremony.random.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if (rt == 1) type = FireworkEffect.Type.BALL;
        if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
        if (rt == 3) type = FireworkEffect.Type.BURST;
        if (rt == 4) type = FireworkEffect.Type.CREEPER;
        if (rt == 5) type = FireworkEffect.Type.STAR;

        int r1 = ceremony.random.nextInt(255) + 1;
        int g1 = ceremony.random.nextInt(255) + 1;
        int b1 = ceremony.random.nextInt(255) + 1;
        int r2 = ceremony.random.nextInt(255) + 1;
        int g2 = ceremony.random.nextInt(255) + 1;
        int b2 = ceremony.random.nextInt(255) + 1;
        Color c1 = Color.fromRGB(r1,g1,b1);
        Color c2 = Color.fromRGB(r2,g2,b2);

        FireworkEffect effect = FireworkEffect.builder().flicker(ceremony.random.nextBoolean()).withColor(c1)
                .withFade(c2).with(type).trail(ceremony.random.nextBoolean()).build();

        meta.addEffect(effect);

        int rp = ceremony.random.nextInt(2) + 1;
        meta.setPower(rp);

        firework.setFireworkMeta(meta);
    }
}
