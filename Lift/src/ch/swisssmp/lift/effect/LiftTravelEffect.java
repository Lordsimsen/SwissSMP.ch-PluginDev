package ch.swisssmp.lift.effect;

import ch.swisssmp.utils.Random;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LiftTravelEffect {

    public static final LiftTravelEffect NONE = new LiftTravelEffect();
    public static final LiftTravelEffect NETHERITE = new LiftTravelEffect(
            new Particle.DustOptions(Color.fromRGB(50, 20, 10), 1),
            Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE,
            0.8f,
            Sound.BLOCK_ANVIL_LAND,
            0.8f);

    private static final int INTERVAL = 1;
    private static final int AMOUNT = 3;
    private static final float PARTICLE_RADIUS = 0.6f;
    private static final Random random = new Random();

    private final String startSoundId;
    private final Sound startSound;
    private final float startSoundPitch;
    private final String endSoundId;
    private final Sound endSound;
    private final float endSoundPitch;
    private final Particle.DustOptions dust;

    private LiftTravelEffect() {
        dust = null;
        startSoundId = null;
        startSound = null;
        startSoundPitch = 1;
        endSoundId = null;
        endSound = Sound.BLOCK_NOTE_BLOCK_CHIME;
        endSoundPitch = 1.5f;
    }

    private LiftTravelEffect(Particle.DustOptions dust, Sound startSound, float startSoundPitch, Sound endSound, float endSoundPitch) {
        this.dust = dust;
        this.startSoundId = null;
        this.startSound = startSound;
        this.startSoundPitch = startSoundPitch;
        this.endSoundId = null;
        this.endSound = endSound;
        this.endSoundPitch = endSoundPitch;
    }

    private LiftTravelEffect(Particle.DustOptions dust, String startSoundId, float startSoundPitch, String endSoundId, float endSoundPitch) {
        this.dust = dust;
        this.startSoundId = startSoundId;
        this.startSound = null;
        this.startSoundPitch = startSoundPitch;
        this.endSoundId = endSoundId;
        this.endSound = null;
        this.endSoundPitch = endSoundPitch;
    }

    public void playStart(Entity entity){
        if(entity instanceof Player){
            Player player = (Player) entity;
            if(startSound!=null){
                player.playSound(player.getLocation(), startSound, SoundCategory.BLOCKS, 16, startSoundPitch);
            }
            if(startSoundId!=null){
                player.playSound(player.getLocation(), startSoundId, SoundCategory.BLOCKS, 16, startSoundPitch);
            }
        }
    }

    public void playEnd(Entity entity){
        if(entity instanceof Player){
            Player player = (Player) entity;
            if(endSound!=null) {
                player.playSound(player.getLocation().add(0,4,0), endSound, SoundCategory.BLOCKS, 4, endSoundPitch);
            }
            if(endSoundId!=null) {
                player.playSound(player.getLocation().add(0,4,0), endSoundId, SoundCategory.BLOCKS, 4, endSoundPitch);
            }
        }
    }

    public void play(Entity entity, Vector motion, long tick) {
        if (dust == null || tick % INTERVAL != 0 || motion.lengthSquared() <= 0) return;
        Location baseLocation = entity.getLocation().add(0, entity.getHeight() / 3 * 2, 0).add(motion.normalize().multiply(entity.getHeight() * 0.8f));
        for (int i = 0; i < AMOUNT; i++) {
            double randomX = (random.nextDouble() * 2 - 1) * PARTICLE_RADIUS;
            double randomY = (random.nextDouble() * 2 - 1) * PARTICLE_RADIUS;
            double randomZ = (random.nextDouble() * 2 - 1) * PARTICLE_RADIUS;
            Location location = baseLocation.clone().add(randomX, randomY, randomZ);
            World world = entity.getWorld();
            world.spawnParticle(Particle.REDSTONE, location, 1, dust);
        }
    }
}
