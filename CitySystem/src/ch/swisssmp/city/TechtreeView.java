package ch.swisssmp.city;

import ch.swisssmp.city.editor.AddonSlot;
import ch.swisssmp.city.editor.AddonTypeSlot;
import ch.swisssmp.city.editor.CityLevelSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.PaginatedView;
import ch.swisssmp.editor.slot.ChangePageSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TechtreeView extends CustomEditorView implements PaginatedView {
    private static final int COLUMNS = 7;

    private final Techtree techtree;
    private final City city;

    private int pageSize;
    private int maxPage;

    private int page = 0;

    private AddonType[][] typeMap;
    private Addon[][] addonCache;
    private CityLevel[] levelMap;

    protected TechtreeView(Player player, Techtree techtree, City city) {
        super(player);
        this.techtree = techtree;
        this.city = city;
    }

    private void initialize() {
        Techtree techtree = this.techtree;
        Collection<CityLevel> levels = techtree.getLevels();
        List<CityLevel> levelMap = new ArrayList<>();
        List<Addon[]> addonCache = new ArrayList<>();
        List<AddonType[]> typeMap = new ArrayList<>();
        for (CityLevel level : levels) {
            levelMap.add(level);
            int currentColumn = 0;
            Addon[] addons = new Addon[7];
            AddonType[] types = new AddonType[7];
            int columns = level.getColumnCount();
            for (AddonType type : level.getAddonTypes()
                    .stream()
                    .sorted(Comparator.comparingInt(t -> t.getSlotY() * columns + t.getSlotX()))
                    .collect(Collectors.toList())) {
                types[currentColumn] = type;
                if (city != null){
                    Addon addon = city.getAddon(type).orElse(city.createAddon(type));
                    if(addon!=null) techtree.updateAddonState(addon);
                    addons[currentColumn] = addon;
                }
                currentColumn++;
                if (currentColumn >= 7) {
                    addonCache.add(addons);
                    typeMap.add(types);
                    levelMap.add(null);
                    addons = new Addon[7];
                    types = new AddonType[7];
                    currentColumn = 0;
                }
            }
            if (currentColumn > 0) {
                addonCache.add(addons);
                typeMap.add(types);
            } else {
                levelMap.remove(levelMap.size() - 1);
            }
        }

        // Bukkit.getLogger().info("Level-Zeilen: " + levelMap.size() + ", AddonType-Zeilen: " + typeMap.size() + ", Addon-Zeilen: " + addonCache.size());

        this.levelMap = levelMap.toArray(new CityLevel[0]);
        this.typeMap = typeMap.toArray(new AddonType[0][]);
        this.addonCache = addonCache.toArray(new Addon[0][]);

        this.pageSize = Math.max(18, Math.min(54, levelMap.size() * 9));
        this.maxPage = Math.max(0, Mathf.ceilToInt(levelMap.size() - 6));
    }

    @Override
    protected int getInventorySize() {
        return pageSize;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> slots = new ArrayList<>();
        if (page > 0) slots.add(new ChangePageSlot(this, 8, false, "Nach oben"));
        if (page < maxPage) slots.add(new ChangePageSlot(this, pageSize - 1, true, "Nach unten"));
        int topRow = this.page;
        int maxRow = Math.min(topRow + 5, this.levelMap.length - 1);
        for (int row = topRow; row <= maxRow; row++) {
            int inventoryRow = row - topRow;
            CityLevel level = levelMap[row];
            LevelStateInfo state = (city!=null) ? techtree.getLevelState(level, city) : null;
            AddonType[] types = typeMap[row];
            Addon[] addons = addonCache[row];
            if (level != null) slots.add(new CityLevelSlot(this, inventoryRow * 9, level, state));
            for (int x = 0; x < 7; x++) {
                AddonType type = types[x];
                if (type == null) continue;
                Addon addon = addons[x];
                int slot = inventoryRow * 9 + x + 1;
                if (addon != null) slots.add(new AddonSlot(this, slot, techtree, addon));
                else slots.add(new AddonTypeSlot(this, slot, type));
            }
        }
        return slots;
    }

    @Override
    public String getTitle() {
        return (city != null ? city.getName() : techtree.getName());
    }

    public void pageUp() {
        if (page <= 0) return;
        page--;
        this.recreateSlots();
    }

    public void pageDown() {
        if (page >= maxPage) return;
        page++;
        this.recreateSlots();
    }

    public void setPage(int page) {
        this.page = page;
        this.recreateSlots();
    }

    public static TechtreeView open(Player player, Techtree techtree) {
        return open(player, techtree, null);
    }

    public static TechtreeView open(Player player, Techtree techtree, City city) {
        TechtreeView result = new TechtreeView(player, techtree, city);
        result.initialize();
        result.open();
        return result;
    }
}
