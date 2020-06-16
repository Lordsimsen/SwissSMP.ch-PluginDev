package ch.swisssmp.deathmessages;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.Random;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DeathMessages {
    private static final ArrayList<VanillaDeathMessage> vanillaDethMessages = new ArrayList<>();
    private static final ArrayList<CustomDeathMessage> customDeathMessages = new ArrayList<>();
    private static final String using = "using";

    public static void reload() {
        reload(null);
    }

    public static void reload(Consumer<String> sendResult) {
        HTTPRequest request = DataSource.getResponse(DeathMessagesPlugin.getInstance(), "messages.php");
        request.onFinish(() -> {
            JsonObject jsonResponse = request.getJsonResponse();
            if (jsonResponse == null || jsonResponse.isJsonNull()) {
                if (sendResult != null) {
                    sendResult.accept("Aktualisierung der Todesnachrichten fehlgeschlagen.");
                }
                return;
            }
            reload(sendResult, jsonResponse);
        });
    }

    public String GetCustomDeathMessage(Player player, String oldMessage, String entity, String block, String killer) {
        Optional<VanillaDeathMessage> vanillaDeathMessageOpt = vanillaDethMessages.stream()
                .filter(msg -> oldMessage.contains(msg.signature))
                .filter(msg -> oldMessage.contains(using) == msg.message.contains(using))
                .sorted(Comparator.comparingInt(msg -> msg.message.length()))
                .findFirst();

        if (!vanillaDeathMessageOpt.isPresent())
            return oldMessage;
        VanillaDeathMessage vanillaDeathMessage = vanillaDeathMessageOpt.get();

        Random r = new Random();
        List<CustomDeathMessage> matchingCustomDeathMessages = customDeathMessages.stream()
                .filter(msg -> msg.vanillaId == vanillaDeathMessage.id)
                .filter(msg -> msg.entity.equals(entity))
                .filter(msg -> msg.block.equals(block))
                .collect(Collectors.toList());
        CustomDeathMessage randomMessage = matchingCustomDeathMessages.get(r.nextInt(matchingCustomDeathMessages.size() - 1));

        JsonObject mask = JsonUtil.parse(vanillaDeathMessage.mask);
        JsonArray maskJsonArray = mask.getAsJsonArray();
        String vanillaMessage = vanillaDeathMessage.message;
        String customMessage = randomMessage.message;
        ArrayList<String> masks = new ArrayList<>();
        for (JsonElement maskElement : maskJsonArray) {
            masks.add(maskElement.getAsString());
        }
        for (String maskString : masks) {
            vanillaMessage = vanillaMessage.replace(maskString, ",");
        }
        String oldCopy = new StringBuffer(oldMessage).toString();
        String[] splitVanillaMessage = vanillaMessage.split(";");

        for(String splitVanillaMessagePart : splitVanillaMessage){
            oldCopy = oldCopy.replace(splitVanillaMessagePart,"\n");
        }
        String[] splitOldMessage = oldCopy.split("\n");
        for(String maskElement : masks){
            String asdf = new String();
            if("{Player}".equals(maskElement))
                asdf = player.getDisplayName();
            else if (("{Mob}".equals(maskElement) || "{Killer}".equals(maskElement)) && !killer.isEmpty()){
                asdf = killer;
            }
            else{
                asdf = splitOldMessage[masks.indexOf(maskElement)];
            }
            customMessage = customMessage.replace(maskElement, asdf);
        }
        return customMessage;
    }

    private static void reload(Consumer<String> sendResult, JsonObject deathMessageData) {
        JsonArray rawVanillaDeathMessages = deathMessageData.getAsJsonArray("vanilla_messages");
        JsonArray rawCustomDeathMessages = deathMessageData.getAsJsonArray("messages");
        if (rawVanillaDeathMessages == null || rawCustomDeathMessages == null) {
            return;
        }

        vanillaDethMessages.clear();
        for (JsonElement rawVanillaDeathMessage : rawVanillaDeathMessages) {
            if (rawVanillaDeathMessage.isJsonObject()) {
                vanillaDethMessages.add(loadVanillaDeathMessage(rawVanillaDeathMessage.getAsJsonObject()));
            }
        }
        customDeathMessages.clear();
        for (JsonElement rawCustomDeathMessage : rawCustomDeathMessages) {
            if (rawCustomDeathMessage.isJsonObject()) {
                customDeathMessages.add(loadCustomDeathMessage(rawCustomDeathMessage.getAsJsonObject()));
            }
        }
    }

    private static VanillaDeathMessage loadVanillaDeathMessage(JsonObject rawVanillaDeathMessage) {
        return new VanillaDeathMessage(
                JsonUtil.getInt("id", rawVanillaDeathMessage),
                JsonUtil.getString("message", rawVanillaDeathMessage),
                JsonUtil.getString("signature", rawVanillaDeathMessage),
                JsonUtil.getString("mask", rawVanillaDeathMessage));
    }

    private static CustomDeathMessage loadCustomDeathMessage(JsonObject rawCustomDeathMessage) {
        return new CustomDeathMessage(
                JsonUtil.getInt("deathmessage_id", rawCustomDeathMessage),
                JsonUtil.getString("entity", rawCustomDeathMessage),
                JsonUtil.getString("message", rawCustomDeathMessage),
                JsonUtil.getString("cause", rawCustomDeathMessage),
                JsonUtil.getString("block", rawCustomDeathMessage));
    }

    public static class VanillaDeathMessage {
        public int id;
        public String message;
        public String signature;
        public String mask;

        public VanillaDeathMessage(int id, String message, String signature, String mask) {
            this.id = id;
            this.message = message;
            this.signature = signature;
            this.mask = mask;
        }
    }

    public static class CustomDeathMessage {
        public int vanillaId;
        public String entity;
        public String message;
        public String cause;
        public String block;

        public CustomDeathMessage(int vanillaId, String entity, String message, String cause, String block) {
            this.vanillaId = vanillaId;
            this.entity = entity;
            this.message = message;
            this.cause = cause;
            this.block = block;
        }

    }
}
