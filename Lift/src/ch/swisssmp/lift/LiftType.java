package ch.swisssmp.lift;

import ch.swisssmp.lift.effect.LiftTravelEffect;
import org.bukkit.Material;

import java.util.Optional;

public enum LiftType {
    OBSIDIAN(Material.OBSIDIAN, 3.2f),
    LAPIS(Material.LAPIS_BLOCK, 4.0f),
    IRON(Material.IRON_BLOCK, 4.8f),
    REDSTONE(Material.REDSTONE_BLOCK, 5.6f),
    EMERALD(Material.EMERALD_BLOCK, 6.4f),
    GOLD(Material.GOLD_BLOCK, 7.2f),
    DIAMOND(Material.DIAMOND_BLOCK, 8.0f),
    NETHERITE(Material.NETHERITE_BLOCK, 8.8f, LiftTravelEffect.NETHERITE)
    ;

    private final Material baseMaterial;
    private final float speed;
    private final LiftTravelEffect travelEffect;

    LiftType(Material material, float speed){
        this(material, speed, LiftTravelEffect.NONE);
    }

    LiftType(Material material, float speed, LiftTravelEffect travelEffect){
        this.baseMaterial= material;
        this.speed = speed;
        this.travelEffect = travelEffect;
    }

    public Material getBaseMaterial(){
        return baseMaterial;
    }

    public float getSpeed(){
        return speed;
    }

    public LiftTravelEffect getTravelEffect(){
        return travelEffect;
    }

    public static Optional<LiftType> of(Material material){
        for(LiftType type : LiftType.values()){
            if(type.baseMaterial==material) return Optional.of(type);
        }

        return Optional.empty();
    }
}
