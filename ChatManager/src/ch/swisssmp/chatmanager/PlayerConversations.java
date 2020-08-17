package ch.swisssmp.chatmanager;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerConversations {
    private static final HashMap<UUID, UUID> playerMap = new HashMap<>();

    public static synchronized void setConversationPartner(UUID playerUid, UUID lastConversationPartner){
        playerMap.put(playerUid, lastConversationPartner);
    }

    public static synchronized Optional<UUID> getConversationPartner(UUID playerUid){
        return playerMap.containsKey(playerUid) ? Optional.of(playerMap.get(playerUid)) : Optional.empty();
    }
}
