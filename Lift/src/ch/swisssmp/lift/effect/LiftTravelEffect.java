package ch.swisssmp.lift.effect;

import ch.swisssmp.utils.Random;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class LiftTravelEffect {

    public static final LiftTravelEffect NONE = new LiftTravelEffect();
    public static final LiftTravelEffect NETHERITE = new LiftTravelEffect(new Particle.DustOptions(Color.fromRGB(50, 20, 10), 1));

    private static final int INTERVAL = 1;
    private static final int AMOUNT = 3;
    private static final float PARTICLE_RADIUS = 0.6f;
    private static final Random random = new Random();

    private final Particle.DustOptions dust;

    private LiftTravelEffect() {
        dust = null;
    }

    private LiftTravelEffect(Particle.DustOptions dust) {
        this.dust = dust;
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
