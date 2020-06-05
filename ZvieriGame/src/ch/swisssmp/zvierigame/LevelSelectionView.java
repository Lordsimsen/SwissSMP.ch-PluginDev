package ch.swisssmp.zvierigame;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zvierigame.game.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
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

import java.util.ArrayList;
import java.util.List;

public class LevelSelectionView implements Listener {

    private final ZvieriArena arena;
    private final String label = "Levelauswahl";
    private final Player player;
    private final Inventory inventory;
    private InventoryView view;

    private LevelSelectionView(Player player, ZvieriArena arena){
        this.player = player;
        this.arena = arena;

        this.inventory = Bukkit.createInventory(null, 9, label);
        setItems();
        if(arena.isGamePreparing()){
            createStartNowItem();
            createCancelGameItem();
        }
    }

    public static LevelSelectionView open(Player player, ZvieriArena arena){
        LevelSelectionView selection = new LevelSelectionView(player, arena);
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
        int slot = event.getSlot();
        if(slot==8 && this.arena.isGamePreparing()){
            this.arena.getGame().leave(player);
            player.closeInventory();
            return;
        }
        ItemStack itemStack = inventory.getItem(slot);
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        if(!this.arena.isGamePreparing()) {
            Level level = new Level(event.getSlot() + 1);
            if(!arena.canPlayLevel(level, player)) { // && !player.hasPermission("zvierigame.admin")
                SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Du hast dieses Level noch nicht freigeschaltet!");
                return;
            }
            this.arena.prepareGame(level);
            this.arena.getGame().join((Player) event.getWhoClicked());
            player.closeInventory();
            player.sendMessage(ZvieriGamePlugin.getPrefix() + " " + ChatColor.GRAY + level.getName() + " " + arena.getName() + " startet in 30 Sekunden.");
            player.sendMessage(ZvieriGamePlugin.getPrefix() + ChatColor.GRAY + " Andere Spieler können jetzt beitreten.");
        } else{
            if(event.getSlot() == this.arena.getGame().getLevel().getLevelNumber() -1){
                startNow();
            }
        }
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

    private void startNow(){
        Bukkit.getScheduler().runTaskLater(ZvieriGamePlugin.getInstance(), () -> {
            arena.getGame().startNow();
            }, 2L);
    }

    private void setItems(){
        ItemStack[] itemStack = new ItemStack[5];
        for(int i = 0; i < itemStack.length; i++){
//            itemStack[i-1] = CustomItems.getCustomItemBuilder("ZVIERI_LEVEL_"+i).build(); // check enum once items exist
            CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("ZVIERI_ARENA");
            itemBuilder.setDisplayName(ChatColor.YELLOW + "Level " + (i+1));
            itemBuilder.setLore(getDescription(i+1));
            ItemStack result = itemBuilder.build();
            ItemUtil.setString(result, "zvieriarena", arena.getId().toString());
            itemStack[i] = result;
        }
        inventory.setContents(itemStack);
    }

    private List<String> getDescription(int i){
        Configuration config = ZvieriGamePlugin.getInstance().getConfig();
        ConfigurationSection levels = config.getConfigurationSection("levels");
        List<String> description = new ArrayList<String>();
        description.add(levels.getString("level_" + i + ".name"));
        description.add(ChatColor.WHITE + "Score um nächsten");
        description.add(ChatColor.WHITE + "Level freizuschalten: " + ChatColor.AQUA + levels.getInt("level_" + i + ".threshhold"));
        return description;
    }

    private void createCancelGameItem(){
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.BARRIER);
        itemBuilder.setDisplayName(ChatColor.RED + "Spiel verlassen");
        itemBuilder.setAmount(1);
        ItemStack result = itemBuilder.build();
        inventory.setItem(8, result);
    }

    private void createStartNowItem(){
        ItemStack result = arena.getStartNowItem();
        inventory.clear();
        inventory.setItem(arena.getGame().getLevel().getLevelNumber() - 1, result);
    }
}
