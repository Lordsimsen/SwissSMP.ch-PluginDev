package ch.swisssmp.camerastudio.editor.path;

import ch.swisssmp.camerastudio.CameraPath;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ValueSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsSlot extends ValueSlot {

    private final CameraPath path;
    private final List<String> value = new ArrayList<String>();

    public CommandsSlot(CustomEditorView view, int slot, CameraPath path) {
        super(view, slot);
        this.path = path;
        Collection<String> commands = path.getCommands();
        if(commands!=null) {
            this.value.addAll(commands.stream().map(c->c.startsWith("/") ? c : "/"+c).collect(Collectors.toList()));
        }
    }

    @Override
    protected boolean applyValue(ItemStack itemStack) {
        if(itemStack==null || itemStack.getType()!= Material.WRITABLE_BOOK) return false;
        List<String> pages = ((BookMeta) itemStack.getItemMeta()).getPages();
        List<String> value = new ArrayList<>();
        for(String p : pages){
            value.addAll(Arrays.stream(p.split("\n")).map(c->c.startsWith("/") ? c.substring(1) : c).collect(Collectors.toList()));
        }
        this.value.clear();
        this.value.addAll(value);
        path.setCommands(value);
        this.setItem(this.createSlot());
        path.getWorld().save();
        return true;
    }

    @Override
    protected ItemStack createPick() {
        ItemStack itemStack = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setPages(value);
        itemStack.setItemMeta(bookMeta);
        return itemStack;
    }

    @Override
    protected CustomItemBuilder createSlotBase(){
        CustomItemBuilder itemBuilder = new CustomItemBuilder();
        itemBuilder.setMaterial(this.value.size() > 0 ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK);
        return itemBuilder;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Befehle";
    }

    @Override
    protected List<String> getValueDisplay() {
        return Arrays.asList(value.size()+" Befehle");
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Geschriebenes Buch","einsetzen",ChatColor.ITALIC+"Befehle mit Zeilenumbruch trennen");
    }

    @Override
    protected boolean isComplete() {
        return true;
    }
}
