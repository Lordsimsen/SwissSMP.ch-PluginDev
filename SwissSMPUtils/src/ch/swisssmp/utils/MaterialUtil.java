package ch.swisssmp.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Optional;

public class MaterialUtil {
    public static Optional<Material> getMaterial(NamespacedKey key){
        Material[] materials = Material.values();
        for (Material material : materials) {
            if (material.getKey().equals(key)) return Optional.of(material);
        }
        return Optional.empty();
    }
}
