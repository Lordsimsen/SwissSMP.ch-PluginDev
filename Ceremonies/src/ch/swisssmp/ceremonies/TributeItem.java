package ch.swisssmp.ceremonies;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class TributeItem {
    public static final String OWNER_PROPERTY = "CeremonyOwner";

    public static void setOwner(ItemStack itemStack, UUID owner){
        ItemUtil.setString(itemStack, OWNER_PROPERTY, owner.toString());
    }

    public static Optional<UUID> getOwner(ItemStack itemStack){
        String ownerUidString = ItemUtil.getString(itemStack, OWNER_PROPERTY);
        try{
            return Optional.of(UUID.fromString(ownerUidString));
        }
        catch(Exception e){
            return Optional.empty();
        }
    }
}
