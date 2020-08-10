package ch.swisssmp.city;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.PlayerData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum SigilRingType {
    METAL_RING,
    GOLD_DIAMOND_RING,
    GOLD_EMERALD_RING,
    GOLD_NETHER_STAR_RING,
    GOLD_REDSTONE_RING,
    GOLD_LAPIS_RING,
    GOLD_GLOWSTONE_RING,
    GOLD_DAISY_RING,
    GOLD_ORCHID_RING,
    EMERALD_DIAMOND_RING,
    EMERALD_EMERALD_RING,
    EMERALD_NETHER_STAR_RING,
    EMERALD_REDSTONE_RING,
    EMERALD_LAPIS_RING,
    EMERALD_GLOWSTONE_RING,
    EMERALD_DAISY_RING,
    EMERALD_ORCHID_RING,
    IRON_DIAMOND_RING,
    IRON_EMERALD_RING,
    IRON_NETHER_STAR_RING,
    IRON_REDSTONE_RING,
    IRON_LAPIS_RING,
    IRON_GLOWSTONE_RING,
    IRON_DAISY_RING,
    IRON_ORCHID_RING,
    OBSIDIAN_DIAMOND_RING,
    OBSIDIAN_EMERALD_RING,
    OBSIDIAN_NETHER_STAR_RING,
    OBSIDIAN_REDSTONE_RING,
    OBSIDIAN_LAPIS_RING,
    OBSIDIAN_GLOWSTONE_RING,
    OBSIDIAN_DAISY_RING,
    OBSIDIAN_ORCHID_RING,
    PRISMARINE_DIAMOND_RING,
    PRISMARINE_EMERALD_RING,
    PRISMARINE_NETHER_STAR_RING,
    PRISMARINE_REDSTONE_RING,
    PRISMARINE_LAPIS_RING,
    PRISMARINE_GLOWSTONE_RING,
    PRISMARINE_DAISY_RING,
    PRISMARINE_ORCHID_RING,
    QUARTZ_DIAMOND_RING,
    QUARTZ_EMERALD_RING,
    QUARTZ_NETHER_STAR_RING,
    QUARTZ_REDSTONE_RING,
    QUARTZ_LAPIS_RING,
    QUARTZ_GLOWSTONE_RING,
    QUARTZ_DAISY_RING,
    QUARTZ_ORCHID_RING,
    REDSTONE_DIAMOND_RING,
    REDSTONE_EMERALD_RING,
    REDSTONE_NETHER_STAR_RING,
    REDSTONE_REDSTONE_RING,
    REDSTONE_LAPIS_RING,
    REDSTONE_GLOWSTONE_RING,
    REDSTONE_DAISY_RING,
    REDSTONE_ORCHID_RING,
    LAPIS_DIAMOND_RING,
    LAPIS_EMERALD_RING,
    LAPIS_NETHER_STAR_RING,
    LAPIS_REDSTONE_RING,
    LAPIS_LAPIS_RING,
    LAPIS_GLOWSTONE_RING,
    LAPIS_DAISY_RING,
    LAPIS_ORCHID_RING
    ;

    public ItemStack createItemStack(){
        return createItemStack(null);
    }

    public ItemStack createItemStack(City city){
        return createItemStack(city, null);
    }

    public ItemStack createItemStack(City city, PlayerData playerData){
        boolean isMayor = (city != null && playerData != null) && playerData.getUniqueId().equals(city.getMayor());
        CustomItemBuilder ringBuilder = CustomItems.getCustomItemBuilder(this.toString());
        ringBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ringBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ringBuilder.setAttackDamage(0);
        ItemStack ring = ringBuilder.build();
        SigilRingInfo ringInfo = new SigilRingInfo(city!=null ? city.getUniqueId() : null, this.toString());
        ringInfo.setOwner(playerData);
        ringInfo.setRank(isMayor ? CitizenRank.MAYOR : CitizenRank.FOUNDER);
        ringInfo.apply(ring);
        return ring;
    }

    public static SigilRingType of(String s){
        try{
            return SigilRingType.valueOf(s.toUpperCase());
        }
        catch(Exception ignored){
            return SigilRingType.METAL_RING;
        }
    }

    public static SigilRingType of(Material frame, Material core){
        if(frame==null || core==null) return null;
        switch(frame){
            case GOLD_BLOCK:
                switch(core){
                    case DIAMOND: return GOLD_DIAMOND_RING;
                    case EMERALD: return GOLD_EMERALD_RING;
                    case NETHER_STAR: return GOLD_NETHER_STAR_RING;
                    case REDSTONE: return GOLD_REDSTONE_RING;
                    case LAPIS_LAZULI: return GOLD_LAPIS_RING;
                    case GLOWSTONE_DUST: return GOLD_GLOWSTONE_RING;
                    case OXEYE_DAISY: return GOLD_DAISY_RING;
                    case BLUE_ORCHID: return GOLD_ORCHID_RING;
                }
            case EMERALD_BLOCK:
                switch(core){
                    case DIAMOND: return EMERALD_DIAMOND_RING;
                    case EMERALD: return EMERALD_EMERALD_RING;
                    case NETHER_STAR: return EMERALD_NETHER_STAR_RING;
                    case REDSTONE: return EMERALD_REDSTONE_RING;
                    case LAPIS_LAZULI: return EMERALD_LAPIS_RING;
                    case GLOWSTONE_DUST: return EMERALD_GLOWSTONE_RING;
                    case OXEYE_DAISY: return EMERALD_DAISY_RING;
                    case BLUE_ORCHID: return EMERALD_ORCHID_RING;
                }
            case IRON_BLOCK:
                switch(core){
                    case DIAMOND: return IRON_DIAMOND_RING;
                    case EMERALD: return IRON_EMERALD_RING;
                    case NETHER_STAR: return IRON_NETHER_STAR_RING;
                    case REDSTONE: return IRON_REDSTONE_RING;
                    case LAPIS_LAZULI: return IRON_LAPIS_RING;
                    case GLOWSTONE_DUST: return IRON_GLOWSTONE_RING;
                    case OXEYE_DAISY: return IRON_DAISY_RING;
                    case BLUE_ORCHID: return IRON_ORCHID_RING;
                }
            case OBSIDIAN:
                switch(core){
                    case DIAMOND: return OBSIDIAN_DIAMOND_RING;
                    case EMERALD: return OBSIDIAN_EMERALD_RING;
                    case NETHER_STAR: return OBSIDIAN_NETHER_STAR_RING;
                    case REDSTONE: return OBSIDIAN_REDSTONE_RING;
                    case LAPIS_LAZULI: return OBSIDIAN_LAPIS_RING;
                    case GLOWSTONE_DUST: return OBSIDIAN_GLOWSTONE_RING;
                    case OXEYE_DAISY: return OBSIDIAN_DAISY_RING;
                    case BLUE_ORCHID: return OBSIDIAN_ORCHID_RING;
                }
            case PRISMARINE_BRICKS:
                switch(core){
                    case DIAMOND: return PRISMARINE_DIAMOND_RING;
                    case EMERALD: return PRISMARINE_EMERALD_RING;
                    case NETHER_STAR: return PRISMARINE_NETHER_STAR_RING;
                    case REDSTONE: return PRISMARINE_REDSTONE_RING;
                    case LAPIS_LAZULI: return PRISMARINE_LAPIS_RING;
                    case GLOWSTONE_DUST: return PRISMARINE_GLOWSTONE_RING;
                    case OXEYE_DAISY: return PRISMARINE_DAISY_RING;
                    case BLUE_ORCHID: return PRISMARINE_ORCHID_RING;
                }
            case QUARTZ_BLOCK:
                switch(core){
                    case DIAMOND: return QUARTZ_DIAMOND_RING;
                    case EMERALD: return QUARTZ_EMERALD_RING;
                    case NETHER_STAR: return QUARTZ_NETHER_STAR_RING;
                    case REDSTONE: return QUARTZ_REDSTONE_RING;
                    case LAPIS_LAZULI: return QUARTZ_LAPIS_RING;
                    case GLOWSTONE_DUST: return QUARTZ_GLOWSTONE_RING;
                    case OXEYE_DAISY: return QUARTZ_DAISY_RING;
                    case BLUE_ORCHID: return QUARTZ_ORCHID_RING;
                }
            case REDSTONE_BLOCK:
                switch(core){
                    case DIAMOND: return REDSTONE_DIAMOND_RING;
                    case EMERALD: return REDSTONE_EMERALD_RING;
                    case NETHER_STAR: return REDSTONE_NETHER_STAR_RING;
                    case REDSTONE: return REDSTONE_REDSTONE_RING;
                    case LAPIS_LAZULI: return REDSTONE_LAPIS_RING;
                    case GLOWSTONE_DUST: return REDSTONE_GLOWSTONE_RING;
                    case OXEYE_DAISY: return REDSTONE_DAISY_RING;
                    case BLUE_ORCHID: return REDSTONE_ORCHID_RING;
                }
            case LAPIS_BLOCK:
                switch(core){
                    case DIAMOND: return LAPIS_DIAMOND_RING;
                    case EMERALD: return LAPIS_EMERALD_RING;
                    case NETHER_STAR: return LAPIS_NETHER_STAR_RING;
                    case REDSTONE: return LAPIS_REDSTONE_RING;
                    case LAPIS_LAZULI: return LAPIS_LAPIS_RING;
                    case GLOWSTONE_DUST: return LAPIS_GLOWSTONE_RING;
                    case OXEYE_DAISY: return LAPIS_DAISY_RING;
                    case BLUE_ORCHID: return LAPIS_ORCHID_RING;
                }

            default: return METAL_RING;
        }
    }
}
