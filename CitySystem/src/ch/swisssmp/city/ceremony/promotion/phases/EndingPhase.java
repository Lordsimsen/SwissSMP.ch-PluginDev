package ch.swisssmp.city.ceremony.promotion.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;


public class EndingPhase extends Phase {

    private final CityPromotionCeremony ceremony;
    private final Block chest;

    private long time = 0;

    public EndingPhase(CityPromotionCeremony ceremony){
        this.ceremony = ceremony;
        this.chest = ceremony.getChest();
    }

    @Override
    public void begin(){
        super.begin();
        this.updateCity();
        String title = "Gratulation!";
        String subtitle = "" + ChatColor.LIGHT_PURPLE + ceremony.getCityName() + ChatColor.YELLOW + " ist nun eine " + ChatColor.LIGHT_PURPLE + " (Stufe)";
        this.announceTitleLong(title, subtitle);

        playMusicFinale();
    }

    private void announceTitleLong(String title, String subtitle){
        for(Player player : ceremony.getPlayers()){
            player.sendTitle(title, subtitle, 10, 120, 30);
        }
    }

    @Override
    public void run() {
        time++;
        if(time%15 == 0) this.spawnFireworks();
        if(time>=100){
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

    private void updateCity(){
        //Todo this, that and this and uh
        Bukkit.getLogger().info("city " + ceremony.getCityName() + " has been promoted!");
    }
}
