package ch.swisssmp.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamageUtil {
    /**
     * Berechnet den theoretischen Schaden inklusive Rüstung
     * @param baseDamage - Der unveränderte Basisschaden
     * @param entity - Die Entity
     * @return Der theoretische Schaden
     */
    public static double calculateDamage(double baseDamage, LivingEntity entity) {
        double damage = baseDamage / 2;
        double armorPoints = entity.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double armorToughness = entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        PotionEffect effect = entity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int resistance = effect == null ? 0 : effect.getAmplifier();
        int epf = getEPF(entity.getEquipment());
        return calculateDamage(damage, armorPoints, armorToughness, resistance, epf);
    }
    /**
     * Berechnet den theoretischen Schaden inklusive Rüstung
     * @param damage - Der unveränderte Basisschaden
     * @param points - Die Rüstung
     * @param toughness - Die Härte der Rüstung
     * @return Der theoretische Schaden
     */
    private static double calculateDamage(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
        double withEnchants = withResistance * (1 - (Math.min(20.0, epf) / 25));
        return withEnchants;
    }

    private static int getEPF(EntityEquipment inventory) {
        if(inventory==null) return 0;
        ItemStack helm = inventory.getHelmet();
        ItemStack chest = inventory.getChestplate();
        ItemStack legs = inventory.getLeggings();
        ItemStack boot = inventory.getBoots();

        return (helm != null ? helm.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (chest != null ? chest.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (legs != null ? legs.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (boot != null ? boot.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0);
    }
}
