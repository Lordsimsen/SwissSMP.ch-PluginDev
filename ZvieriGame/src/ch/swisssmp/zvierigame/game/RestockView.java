package ch.swisssmp.zvierigame.game;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.zvierigame.ZvieriArena;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RestockView implements Listener {

    private final ZvieriArena arena;
    private final GamePhase gamePhase;
    private final String label = "Zutaten nachbestellen";
    private final Player player;
    private final Inventory inventory;
    private InventoryView view;

    protected RestockView(ZvieriArena arena, GamePhase gamePhase, ItemStack[] ingredients, Player player){
        this.arena = arena;
        this.player = player;
        this.gamePhase = gamePhase;

        int size = Mathf.ceilToInt(ingredients.length/9 + 1)*9;
        inventory = Bukkit.createInventory(null, size, label);
        setItems(ingredients);
    }

    public static RestockView open(ZvieriArena arena, GamePhase gamePhase, ItemStack[] ingredients, Player player){
        RestockView selection = new RestockView(arena, gamePhase, ingredients, player);
        Bukkit.getPluginManager().registerEvents(selection, ZvieriGamePlugin.getInstance());
        selection.open();
        return selection;
    }

    private void open(){
        this.view = this.player.openInventory(this.inventory);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event){
        if(event.getView()!=this.view || event.getClickedInventory() != this.inventory) return;
        event.setCancelled(true);
        ItemStack ingredient = inventory.getItem(event.getSlot());
        order(ingredient);
        player.closeInventory();
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event){
        if(event.getView()!=this.view) return;
        HandlerList.unregisterAll(this);
    }

    private void setItems(ItemStack[] ingredients){ //Brucht äuä die CustomItems bzw dene eri enums odr?
        ItemStack[] itemStack = new ItemStack[ingredients.length];
        for(int i = 0; i < itemStack.length; i++){
//          itemStack[i-1] = CustomItems.getCustomItemBuilder("ZVIERI_ZUTAT_"+i).build(); // check enum once items exist
            CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(ingredients[i].getItemMeta().getDisplayName());

//          itemBuilder.setLore(getDescription(i)); Preis und Anzahl aus enum holen?
            List<String> description = new ArrayList<String>();
            description.add("5 Stück für 5 Francs bestellen");
            itemBuilder.setLore(description);

            ItemStack result = itemBuilder.build();
            itemStack[i] = result;
        }
        inventory.setContents(itemStack);
    }

    private void order(ItemStack ingredient){
        //TODO Preis und Aznahl je nach Item ändern(?)
        int ingredientPrice = 5;
        this.gamePhase.subtractFromScore(ingredientPrice);

        ItemStack result = ingredient;
        result.setAmount(5); // variiert nach Zutat?
        //TODO add them after random time up to 20 seconds ..
        long delay = new Random().nextInt(20);
        this.arena.getStorageChest().getBlockInventory().setItem(this.arena.getStorageChest().getBlockInventory().firstEmpty(), result);

        player.sendMessage(ZvieriGamePlugin.getPrefix() + " " + ingredient.getItemMeta().getDisplayName() + " wurden bestellt.");
    }
}
