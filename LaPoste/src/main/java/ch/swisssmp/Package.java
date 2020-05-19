package ch.swisssmp;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Package {

    public static boolean isPackage(ItemStack itemStack){
        try {
            return ItemUtil.getBoolean(itemStack, "la_poste_package");
        } catch (NullPointerException e){
            return false;
        }
    }

    public static void addItem(ItemStack paket, ItemStack item) throws Exception{
        int weight = ItemUtil.getInt(paket, "weight");
        if(weight >= 4) throw new Exception("Zu hohes Gewicht");
        String serializedItem = ItemUtil.serialize(item);
        ItemUtil.setString(paket, "item_" + (weight+1), serializedItem);
        ItemMeta paketMeta = paket.getItemMeta();
        List<String> description = paketMeta.getLore();
        description.add(item.getAmount() + "x " + item.getItemMeta().getDisplayName());
        paket.setItemMeta(paketMeta);
    }

    public static ItemStack removeLastItem(ItemStack paket){
        int weight = ItemUtil.getInt(paket, "weight");
        ItemStack lastItem = ItemUtil.deserialize(ItemUtil.getString(paket, "item_" + weight));
        ItemUtil.setString(paket, "item_" + weight, null);
        ItemMeta paketMeta = paket.getItemMeta();
        List<String> description = paketMeta.getLore();
        description.remove(weight-1);
        paket.setItemMeta(paketMeta);
        ItemUtil.setInt(paket, "weight", weight-1);
        return lastItem;
    }
}
