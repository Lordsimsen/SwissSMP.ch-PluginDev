package ch.swisssmp.zvierigame.game;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.ZvieriArena;
import ch.swisssmp.zvierigame.ZvieriGamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RestockView implements Listener {

    private final ZvieriArena arena;
    private final GamePhase gamePhase;
    private final String label = "Zutaten nachbestellen";
    private final Player player;
    private final Inventory inventory;
    private ConfigurationSection ingredientsSection;
    private InventoryView view;

    protected RestockView(ZvieriArena arena, GamePhase gamePhase, Player player){
        this.arena = arena;
        this.player = player;
        this.gamePhase = gamePhase;
        try {
            this.ingredientsSection = ZvieriGamePlugin.getInstance().getConfig().getConfigurationSection("ingredients");
        } catch (NullPointerException e){
            Bukkit.getLogger().info("Ingredients not found in config");
            gamePhase.cancel();
        }

        int size = 27;
        inventory = Bukkit.createInventory(null, size, label);
        setItems();
    }

    public static RestockView open(ZvieriArena arena, GamePhase gamePhase, Player player){
        RestockView selection = new RestockView(arena, gamePhase, player);
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

    private void setItems(){
        HashMap<String,ItemStack> ingredients = gamePhase.getLevel().getIngredients();
        ItemStack[] itemStack = new ItemStack[ingredients.size()];
        int i = 0;
        for(Map.Entry<String,ItemStack> entry : ingredients.entrySet()){
            ItemStack item = entry.getValue();
            String ingredientId = entry.getKey();
            item.setAmount(ingredientsSection.getInt(ingredientId + ".buyAmount"));
            ItemMeta itemMeta = item.getItemMeta();
            List<String> description = Arrays.asList(ingredientsSection.getInt(ingredientId + ".price") + " Smaragdmünzen");
            itemMeta.setLore(description);
            item.setItemMeta(itemMeta);
            itemStack[i] = item;
            i++;
        }
        inventory.setContents(itemStack);
    }

    private void order(ItemStack ingredient){
        HashMap<String,ItemStack> ingredients = gamePhase.getLevel().getIngredients();
        Optional<String> ingredientIdQuery = ingredients.entrySet().stream()
                .filter(e -> e.getValue().isSimilar(ingredient))
                .map(Map.Entry::getKey).findAny();
        if(!ingredientIdQuery.isPresent()) return;
        String ingredientId = ingredientIdQuery.get();

        if(!gamePhase.isRestockAllowed()){
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Keine Bestellungen mehr möglich");
            return;
        }
        int price = ingredientsSection.getInt(ingredientId + ".price");
        this.gamePhase.subtractFromScore(price);
        if(gamePhase.getScore() < 0){
            gamePhase.resetScore();
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Zu wenig Smaragdmuenzen");
            return;
        }
        gamePhase.displayScore();
        ItemStack result = ingredient.clone();
        ItemUtil.setBoolean(result, "zvieriGameItem", true);
        ItemMeta meta = result.getItemMeta();
        meta.setLore(new ArrayList<String>());
        result.setItemMeta(meta);
        result.setAmount(ingredientsSection.getInt(ingredientId + ".buyAmount"));

        Inventory storage = this.arena.getStorageChest().getBlockInventory();
        Bukkit.getScheduler().runTaskLater(ZvieriGamePlugin.getInstance(), () -> {
            boolean stackExists = false;
            for(int i = 0; i < storage.getStorageContents().length; i++){
                if(storage.getItem(i) != null && storage.getItem(i).isSimilar(result)){
                    result.setAmount(result.getAmount() + storage.getItem(i).getAmount());
                    storage.setItem(i, result);
                    stackExists = true;
                    break;
                }
            }
            if(!stackExists) storage.setItem(storage.firstEmpty(), result);
        }, new Random().nextInt(400));
        SwissSMPler.get(player).sendActionBar("Bestellung aufgegeben.");
    }
}
