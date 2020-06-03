package ch.swisssmp.laposte;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Package {

    public static void addItem(ItemStack paket, ItemStack item) throws Exception{
        if(ItemUtil.getBoolean(item, "la_poste_package") || ItemUtil.getBoolean(item, "la_poste_letter")) throw new Exception("Dieses Item kann nicht verpackt werden!");
        if(item.getType() == Material.SHULKER_BOX){
            BlockStateMeta shulkerBlockMeta = (BlockStateMeta) item.getItemMeta();
            ShulkerBox box = (ShulkerBox) shulkerBlockMeta.getBlockState();
            Inventory boxInventory = box.getInventory();
            for(ItemStack itemStack : boxInventory){
                if(itemStack != null){
                    if(itemStack.getType() != Material.AIR){
                        throw new Exception("Nur leere Shulkerboxen können verpackt werden!");
                    }
                }
            }
        }
        int weight = ItemUtil.getInt(paket, "weight");
        if(weight >= 4) throw new Exception("Kapazität voll");
        String serializedItem = ItemUtil.serialize(item);
        ItemUtil.setString(paket, "item_" + (weight+1), serializedItem);
        ItemUtil.setInt(paket, "weight", weight+1);
        ItemMeta paketMeta = paket.getItemMeta();
        List<String> description = new ArrayList<String>();
        description.add("Füllstand: " + (weight+1) + "/4");
        paketMeta.setLore(description);
        paket.setItemMeta(paketMeta);
    }

    public static ItemStack removeLastItem(ItemStack paket) throws Exception{
        int weight = ItemUtil.getInt(paket, "weight");
        if(weight == 0) throw new Exception("Nichts zu entpacken");
        ItemStack lastItem = ItemUtil.deserialize(ItemUtil.getString(paket, "item_" + weight));

        ItemUtil.setString(paket, "item_" + weight, "");
        ItemUtil.setInt(paket, "weight", weight-1);

        ItemMeta paketMeta = paket.getItemMeta();
        List<String> description = new ArrayList<String>();
        description.add("Füllstand: " + (weight-1) + "/4");
        paketMeta.setLore(description);
        paket.setItemMeta(paketMeta);
        if(ItemUtil.getBoolean(paket, "received")){
            CustomItems.getCustomItemBuilder("LA_POSTE_PACKAGE_OPENED").update(paket);
        }
        return lastItem;
    }
}
