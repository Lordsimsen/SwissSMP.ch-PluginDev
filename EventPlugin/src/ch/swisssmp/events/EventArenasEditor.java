package ch.swisssmp.events;

import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EventArenasEditor {

    private final EventPlugin eventPlugin;
    private final Player player;
    private final Inventory inventory;
    private EventArenas arenas;

    private InventoryView view;

    private EventArenasEditor(EventPlugin plugin, Player player, List<EventArena> arenen, String label) {
        this.eventPlugin = plugin;
        this.player = player;

        int cellCount = Mathf.ceilToInt(arenen.size() / 9f)*9;
        this.inventory = Bukkit.createInventory(null,  cellCount, label);
        this.createItems(arenen);
    }

    private void createItems(List<EventArena> arenen) {
        for(EventArena arena : arenen) {
            ItemStack tokenStack = arena.getTokenStack();
            if(tokenStack == null) {
                continue;
            }
            this.inventory.addItem(tokenStack);
        }
    }

    public HumanEntity getPlayer() {
        return this.player;
    }

    private void open() {
        this.view = this.getPlayer().openInventory(this.inventory);
    }

    public EventArenasEditor open(EventPlugin plugin, Player player, boolean showAll) {
        EventArenasEditor editor;
        if(showAll) {
            if(arenas.getArenasList().size() == 0) {
                player.sendMessage(eventPlugin.getPrefix() + " Es wurde noch keine Arena erstellt. "
                        + "Verwende /zvieriarena create [Name] um eine Arena zu erstellen");
                return null;
            }
            editor = new EventArenasEditor(plugin, player, arenas.getArenasList(), "Alle Arenen");
        } else {
            List<EventArena> arenen = EventArenas.getArenas(player.getWorld());
            if(arenen.size() == 0) {
                player.sendMessage(eventPlugin.getPrefix() + " In dieser Welt gibt es keine Arenen. "
                        + "Verwende /zvieriarenen um alle geladenen Arenen anzuzeigen");
                return null;
            }
            editor = new EventArenasEditor(plugin, player, arenen, "Arenen in " + player.getWorld().getName());
        }
        editor.open();
        return editor;
    }

}
