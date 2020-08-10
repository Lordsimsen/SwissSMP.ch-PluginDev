package ch.swisssmp.city.guides;

import ch.swisssmp.city.*;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.conversations.NPCConversation;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import org.bukkit.entity.Villager;

import java.util.Optional;
import java.util.UUID;

public class AddonGuide {

    private final Addon addon;
    private final NPCInstance npc;

    private AddonGuide(Addon addon, NPCInstance npc){
        this.addon = addon;
        this.npc = npc;
    }

    public Addon getAddon(){
        return addon;
    }

    public NPCInstance getNPC(){
        return npc;
    }

    public void remove(){
        npc.remove();
        addon.save();
    }

    public static Optional<AddonGuide> get(Entity entity){
        NPCInstance npc = NPCInstance.get(entity);
        if(npc==null) return Optional.empty();
        return get(npc);
    }

    public static Optional<AddonGuide> get(NPCInstance npc){
        JsonObject json = npc.getJsonData();
        if(json==null || !json.has("city_id") || !json.has("addon_id")) return Optional.empty();
        UUID cityId = JsonUtil.getUUID("city_id", json);
        String addonId = JsonUtil.getString("addon_id", json);
        Addon addon = CitySystem.getAddon(cityId, addonId).orElse(null);
        return addon!=null ? Optional.of(new AddonGuide(addon, npc)) : Optional.empty();
    }

    public static AddonGuide create(Location location, Addon addon) {
        NPCInstance npc = NPCInstance.create(EntityType.VILLAGER, location);
        AddonGuide result = new AddonGuide(addon, npc);
        result.update();
        addon.save();
        return result;
    }

    public void update() {
        npc.setIdentifier("addon_instance_guide");
        npc.setSilent(true);
        npc.setNameVisible(false);
        JsonObject json = npc.getJsonData();
        if (json == null) json = new JsonObject();
        JsonUtil.set("city_id", addon.getCityId(), json);
        JsonUtil.set("addon_id", addon.getAddonId(), json);
        npc.setJsonData(json);

        AddonType type = addon.getType();
        if (type == null) {
            Bukkit.getLogger().warning(CitySystemPlugin.getPrefix() + " AddonType " + addon.getAddonId() + " für Stadt " + addon.getCityId() + " nicht gefunden!");
            return;
        }
        if (addon.getState() != null) {
            npc.setName(addon.getState().getColor() + type.getName());
        } else {
            npc.setName(type.getName());
        }
        updateVillager(npc.getEntity(), type);
    }

    private static void updateVillager(Entity entity, AddonType type) {
        if (!(entity instanceof Villager)) return;
        Villager villager = (Villager) entity;
        Villager.Profession profession;
        switch (type.getCityLevel()) {
            case 1:
                profession = Villager.Profession.TOOLSMITH;
                break;
            case 2:
                profession = Villager.Profession.NITWIT;
                break;
            case 3:
                profession = Villager.Profession.LIBRARIAN;
                break;
            case 4:
                profession = Villager.Profession.CLERIC;
                break;
            default:
                profession = Villager.Profession.FARMER;
        }
        villager.setProfession(profession);
    }

    public void openGuideView(Player player) {
        AddonGuideView.open(player, this);
    }

    public void startConversation(Player player) {
        City city = addon.getCity();
        if (city.isCitizen(player.getUniqueId())) {
            startCitizenConversation(player);
        } else {
            startVisitorConversation(player);
        }
    }

    public void openUnlockView(Player player){
        AddonUnlockView.open(player, this);
    }

    private void startCitizenConversation(Player player) {
        NPCConversation conversation = NPCConversation.start(npc, player, 200);
        AddonState state = addon.getState();
        AddonType type = addon.getType();
        AddonStateReason reason = addon.getStateReason();
        String reasonMessage = addon.getStateReasonMessage();
        switch (state) {
            case ACCEPTED: {
                conversation.addLine("Dieses Addon ist " + state.getColor() + "einsatzbereit" + ChatColor.RESET + ".");
                if (reasonMessage == null) {
                    conversation.addLine("Kontaktiere ein Mitglied vom " + ChatColor.GREEN + "Staff MC" + ChatColor.RESET + "...");
                    conversation.addLine("... um es " + AddonState.ACTIVATED.getColor() + "aktivieren" + ChatColor.RESET + " zu lassen.");
                } else {
                    for (String line : reasonMessage.split("\n")) {
                        conversation.addLine(line);
                    }
                }
                break;
            }
            case ACTIVATED: {
                conversation.addLine("Dieses Addon ist " + state.getColor() + "aktiviert" + ChatColor.RESET + ".");
                conversation.addLine("Möchtest du eine Änderung vornehmen?");
                conversation.onComplete(() -> {
                    AddonGuideView.open(player, this);
                });
                break;
            }
            case AVAILABLE: {
                conversation.addLine("Dieses Addon ist " + state.getColor() + "verfügbar" + ChatColor.RESET + ".");
                if (type.getUnlockTrades().length > 0) {
                    conversation.addLine("Möchtest du es " + AddonState.ACTIVATED.getColor() + "freischalten" + ChatColor.RESET + "?");
                    conversation.onFinish(() -> {
                        this.openUnlockView(player);
                    });
                } else {
                    conversation.addLine("Leider hab ich grad keine Papiere für die Aktivierung da.");
                    conversation.addLine("Komm doch später nochmals vorbei.");
                }
                break;
            }
            case BLOCKED: {
                conversation.addLine("Dieses Addon ist momentan " + state.getColor() + "blockiert" + ChatColor.RESET + ".");
                if (reasonMessage == null) {
                    conversation.addLine("Mir ist zwar nicht bekannt weshalb,...");
                    conversation.addLine("...aber komm doch später nochmals vorbei.");
                } else {
                    for (String line : reasonMessage.split("\n")) {
                        conversation.addLine(line);
                    }
                }
                break;
            }
            case UNAVAILABLE: {
                conversation.addLine("Dieses Addon ist momentan " + state.getColor() + "nicht verfügbar" + ChatColor.RESET + ".");
                if (reasonMessage != null) {
                    switch(reason){
                        case CITY_LEVEL: reasonMessage = reasonMessage.replace("\n", " ");break;
                        case REQUIRED_ADDONS: reasonMessage = reasonMessage.replace("\n", ",").replace(",- ", ", ");break;
                        default:break;
                    }
                    for (String line : reasonMessage.split("\n")) {
                        conversation.addLine(line);
                    }
                    break;
                }
                break;
            }
            default: {
                conversation.addLine("Dieses Addon ist momentan " + state.getColor() + state.getDisplayName() + ChatColor.RESET + ".");
                if (reasonMessage != null) {
                    for (String line : reasonMessage.split("\n")) {
                        conversation.addLine(line);
                    }
                }
                break;
            }
        }
    }

    private void startVisitorConversation(Player player) {
        NPCConversation conversation = NPCConversation.start(npc, player, 200);
        if (addon.getState() == AddonState.ACCEPTED || addon.getState() == AddonState.ACTIVATED) {
            City city = addon.getCity();
            conversation.addLine("Dieses Bauwerk gehört zu " + city.getName() + ".");
        } else {
            conversation.addLine("Dieses Addon ist noch nicht aktiviert.");
            conversation.addLine("Wie wärs, wenn du später wieder auf Besuch kommst?");
            conversation.addLine("Schönen Tag noch!");
        }
    }
}
