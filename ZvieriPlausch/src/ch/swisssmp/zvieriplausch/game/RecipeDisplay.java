package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.custompaintings.CustomPainting;
import ch.swisssmp.custompaintings.CustomPaintings;
import ch.swisssmp.text.RawTextObject;
import ch.swisssmp.text.properties.ClickEventProperty;
import ch.swisssmp.text.properties.ColorProperty;
import ch.swisssmp.text.properties.HoverEventProperty;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.zvieriplausch.Dish;
import ch.swisssmp.zvieriplausch.RecipePaintings;
import ch.swisssmp.zvieriplausch.ZvieriArena;
import ch.swisssmp.zvieriplausch.ZvieriGamePlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class RecipeDisplay {

    public final static String DisplayIdProperty = "zvieriRecipeHandbook";
    private final static int TILE_WIDTH = 128;
    private final static int TILE_HEIGHT = 128;

    private final ZvieriArena arena;
    private final CustomPainting painting;
    private Dish currentRecipe;
    private Level level;

    public RecipeDisplay(ZvieriArena arena, CustomPainting painting){
        this.arena = arena;
        this.painting = painting;
    }

    public UUID getId(){
        return arena.getId();
    }

    public void setLevel(Level level){
        this.level = level;
    }

    public CustomPainting getPainting(){
        return painting;
    }

    public ItemStack getItemStack(){
        ItemStack result = new ItemStack(Material.WRITTEN_BOOK);
        updateItemStack(result);
        ItemUtil.setString(result, DisplayIdProperty, arena.getId().toString());
        ItemUtil.setBoolean(result, "zvieriGameItem", true);
        return result;
    }

    public void applyRecipe(Dish dish) {
        CustomPainting painting = getPainting();
        if(painting == null){
            Bukkit.getLogger().info(ZvieriGamePlugin.getPrefix() + " CustomPaiting für " + dish.toString() + " nicht gefunden");
            return;
        }
        File file = RecipePaintings.getLocalFile(dish);
        CustomPaintings.replace(arena.getId().toString(), file);
        this.currentRecipe = dish;
    }

    public void updateItemStack(ItemStack itemStack){
        if(itemStack.getType() != Material.WRITTEN_BOOK) return;
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        this.updateContents(bookMeta);
        itemStack.setItemMeta(bookMeta);
    }

    public void updateContents(BookMeta bookMeta){
        bookMeta.setTitle("Rezepte");
        bookMeta.setAuthor("Küchenchef");
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

        ArrayList<BaseComponent> currentPage = new ArrayList<BaseComponent>();
        List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();

        RawTextObject title = new RawTextObject(level.getName() + "\n", new ColorProperty(ColorProperty.Color.DARK_PURPLE));
        currentPage.add(title.toSpigot());

        RawTextObject helpText = new RawTextObject(
                "" + ChatColor.ITALIC + ChatColor.GRAY + "Wähle ein Rezept aus," + ChatColor.RESET + "\n" +
                        ChatColor.ITALIC + ChatColor.GRAY + "um es zu betrachten." + ChatColor.RESET + "\n" + "\n");
        currentPage.add(helpText.toSpigot());

        int line = 5;
        for(Dish dish : level.getDishes()){
            ItemStack dishStack = dish.getItemStack();
            RawTextObject recipeEntry = new RawTextObject(dishStack.getItemMeta().getDisplayName()
                    .replaceAll(Pattern.quote(ChatColor.COLOR_CHAR + "[a-z0-9]"), "") + "\n");
            recipeEntry.add(new ColorProperty(ColorProperty.Color.BLACK));
            recipeEntry.add(new HoverEventProperty(HoverEventProperty.Action.SHOW_TEXT, "Klicke um dieses Rezept zu betrachten"));
            recipeEntry.add(new ClickEventProperty(ClickEventProperty.Action.RUN_COMMAND, "/zvierirecipedisplay show " + arena.getId() + " " + dish.getCustomEnum()));
            currentPage.add(recipeEntry.toSpigot());
            line++;
            if(line >= 8) {
                pages.add(buildPage(currentPage));
                currentPage.clear();
                line = 0;
            }
        }
        if(currentPage.size() > 0) {
            pages.add(buildPage(currentPage));
        }
        bookMeta.spigot().setPages(pages);
    }

    private BaseComponent[] buildPage(List<BaseComponent> page){
        BaseComponent[] result = new BaseComponent[page.size()];
        page.toArray(result);
        return result;
    }

    public void remove(){
        CustomPainting painting = getPainting();
        if(painting != null){
            painting.remove();
        }
    }
}
