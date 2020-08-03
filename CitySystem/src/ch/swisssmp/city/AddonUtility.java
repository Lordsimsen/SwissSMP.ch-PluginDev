package ch.swisssmp.city;

import ch.swisssmp.city.guides.AddonGuide;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.SwissSMPler;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

class AddonUtility {

    protected static Optional<Addon> findAddon(String cityKey, String addonKey, boolean createIfMissing) {
        City city = CitySystem.findCity(cityKey).orElse(null);
        if (city == null) return Optional.empty();
        Techtree techtree = CitySystem.getTechtree(city.getTechtreeId()).orElse(null);
        if (techtree == null) return Optional.empty();
        AddonType type = techtree.findAddonType(addonKey).orElse(null);
        if (type == null) return Optional.empty();
        Addon result = city.getAddon(type).orElse(null);
        if(result==null && createIfMissing){
            result = city.createAddon(type);
            techtree.updateAddonState(result);
            result.save();
        }
        return Optional.of(result);
    }

    protected static Optional<Addon> findAddon(Block block, boolean createIfMissing) {
        if (!(block.getState() instanceof Sign)) return Optional.empty();
        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        return findAddon(lines[1],lines[2], createIfMissing);
    }

    protected static Optional<Addon> getAddon(NPCInstance npc) {
        String identifier = npc.getIdentifier();
        if (identifier == null || !identifier.equals("addon_instance_guide")) return Optional.empty();
        JsonObject json = npc.getJsonData();
        if (json == null || !json.has("city_id") || !json.has("addon_id")) return Optional.empty();
        UUID cityId = JsonUtil.getUUID("city_id", json);
        String addonId = json.get("addon_id").getAsString();
        return CitySystem.getAddon(cityId, addonId);
    }

    protected static AddonGuide createAddonGuide(Player player, Sign sign, Addon addon){
        if(addon.hasGuideActive()){
            SwissSMPler.get(player).sendMessage(CitySystemPlugin.getPrefix()+ ChatColor.RED+" Dieses Addon hat bereits einen Addon Guide.");
            SwissSMPler.get(player).sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+" Entferne zuerst diesen und versuche es dann nochmals.");
            return null;
        }
        Location location = sign.getLocation();
        location.setYaw(player.getLocation().getYaw()+180);
        sign.getBlock().setType(Material.AIR);
        return AddonGuide.create(location.add(0.5, 0, 0.5), addon);
    }
}
