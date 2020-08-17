package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.swisssmp.utils.nbt.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerData;

public class SigilRingInfo {

    private final UUID cityId;
    private final SigilRingType type;
    private PlayerData owner;
    private CitizenRank rank;

    private boolean invalid = false;

    public SigilRingInfo(UUID cityId, SigilRingType type) {
        this.cityId = cityId;
        this.type = type;
    }

    public City getCity() {
        return CitySystem.getCity(cityId).orElse(null);
    }

    public Optional<Citizenship> getCitizenship() {
        return CitySystem.getCitizenship(cityId, owner.getUniqueId());
    }

    public void apply(ItemStack itemStack) {
        if (itemStack == null) return;
        CompoundTag nbtTag = ItemUtil.getData(itemStack);
        if (nbtTag == null) nbtTag = new CompoundTag();
        nbtTag.putString("city_tool", "sigil_ring");
        nbtTag.putString("ring_type", type.toString());
        nbtTag.remove("customEnum");
        if (cityId != null) NBTUtil.set("city_id", cityId, nbtTag);
        else if (nbtTag.containsKey("city_id")) nbtTag.remove("city_id");
        if (owner != null) {
            nbtTag.putString("owner", owner.getUniqueId().toString());
            nbtTag.putString("owner_name", owner.getName());
        }
        if (rank != null) nbtTag.putString("citizen_rank", rank.toString());
        ItemUtil.setData(itemStack, nbtTag);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        if (invalid) {
            itemMeta.setDisplayName(ChatColor.GRAY + "Alter Siegelring");
        } else {
            CustomItemBuilder templateBuilder = CustomItems.getCustomItemBuilder(type.toString());
            ItemStack template = templateBuilder.build();
            itemMeta.setDisplayName(template.getItemMeta().getDisplayName());
        }
        if (rank == CitizenRank.MAYOR && !itemMeta.hasEnchant(Enchantment.DURABILITY)) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        } else if (rank != CitizenRank.MAYOR && itemMeta.hasEnchant(Enchantment.DURABILITY)) {
            itemMeta.removeEnchant(Enchantment.DURABILITY);
        }
        itemMeta.setLore(getRingDescription());
        itemStack.setItemMeta(itemMeta);
    }

    public List<String> getRingDescription() {
        List<String> result = new ArrayList<String>();
        if (owner != null) {
            result.add(ChatColor.GRAY + "Eigentum von " + this.owner.getDisplayName() + (rank != null ? "," : ""));
            if (rank != null) {
                String display_rank = (invalid ? "Ehemal. " : "") + rank.getDisplayName();
                City city = getCity();
                if (city != null) result.add(ChatColor.GRAY + display_rank + " von " + city.getName());
            }
        }
        return result;
    }

    public void setOwner(PlayerData playerData) {
        this.owner = playerData;
    }

    public PlayerData getOwner() {
        return owner;
    }

    public void setRank(CitizenRank rank) {
        this.rank = rank;
    }

    public CitizenRank getCitizenRrank() {
        return rank;
    }

    public void invalidate() {
        this.invalid = true;
    }

    public static Optional<SigilRingInfo> get(ItemStack ring) {
        if (ring == null) return Optional.empty();

        CompoundTag nbtTag = ItemUtil.getData(ring);
        if (nbtTag == null) return Optional.empty();

        CityToolType toolType = CityToolType.of(nbtTag.getString("city_tool"));
        if (toolType != CityToolType.SIGIL_RING) return Optional.empty();

        if(!nbtTag.containsKey("city_id")) return Optional.empty();

        UUID cityId = NBTUtil.getUUID("city_id", nbtTag);
        City city = CitySystem.getCity(cityId).orElse(null);
        if (city == null){
            int legacyCityId = nbtTag.getInt("city_id");
            //noinspection deprecation
            city = CitySystem.getCity(legacyCityId).orElse(null);
            if(city==null) return Optional.empty();
        }

        SigilRingType type = SigilRingType.of(nbtTag.getString("ring_type"));
        SigilRingInfo result = new SigilRingInfo(cityId, type);
        if (nbtTag.containsKey("owner") && nbtTag.containsKey("owner_name")) {
            UUID owner_uuid = UUID.fromString(nbtTag.getString("owner"));
            String owner_name = nbtTag.getString("owner_name");
            result.setOwner(new PlayerData(owner_uuid, owner_name, owner_name));
        }

        if (nbtTag.containsKey("citizen_rank")) {
            result.setRank(CitizenRank.get(nbtTag.getString("citizen_rank")));
        }

        return Optional.of(result);
    }
}
