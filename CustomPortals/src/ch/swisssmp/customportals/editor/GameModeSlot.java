package ch.swisssmp.customportals.editor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customportals.CustomPortal;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GameModeSlot extends SelectSlot {

    private final CustomPortal portal;

    public GameModeSlot(CustomEditorView view, int slot, CustomPortal portal) {
        super(view, slot);
        this.portal = portal;
    }

    @Override
    protected int getInitialValue() {
        Collection<GameMode> gameModes = portal.getAllowedGameModes();
        if(gameModes.size()==0){
            gameModes.addAll(Arrays.asList(GameMode.SURVIVAL,GameMode.CREATIVE,GameMode.ADVENTURE,GameMode.SPECTATOR));
            portal.setAllowedGameModes(gameModes);
            portal.getContainer().save();
        }
        return (gameModes.contains(GameMode.SURVIVAL) ? 1 : 0)
                + (gameModes.contains(GameMode.CREATIVE) ? 2 : 0)
                + (gameModes.contains(GameMode.ADVENTURE) ? 4 : 0)
                + (gameModes.contains(GameMode.SPECTATOR) ? 8 : 0) - 1;
    }

    @Override
    protected int getOptionsLength() {
        return 15;
    }

    @Override
    protected void onValueChanged(int value) {
        Collection<GameMode> gameModes = getGameModes(value+1);
        this.portal.setAllowedGameModes(gameModes);
        this.portal.getContainer().save();
        this.portal.updateTokens();
        setItem(createSlot());
    }

    @Override
    public String getName() {
        return ChatColor.RESET+"Zulässige GameModes";
    }

    @Override
    protected List<String> getNormalDescription() {
        Collection<GameMode> gameModes = getGameModes(getValue()+1);
        List<String> lines = new ArrayList<>();
        for(GameMode gameMode : gameModes){
            lines.add(ChatColor.GREEN+gameMode.name());
        }
        return lines;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(Material.BOOK);
        itemBuilder.setAmount(1);
        return itemBuilder;
    }

    private Collection<GameMode> getGameModes(int value){
        Collection<GameMode> result = new ArrayList<>();
        if((value & 1) != 0) result.add(GameMode.SURVIVAL);
        if((value & 2) != 0) result.add(GameMode.CREATIVE);
        if((value & 4) != 0) result.add(GameMode.ADVENTURE);
        if((value & 8) != 0) result.add(GameMode.SPECTATOR);
        return result;
    }
}
