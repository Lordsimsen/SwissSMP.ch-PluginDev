package ch.swisssmp.zvieriplausch;

import ch.swisssmp.zvieriplausch.game.RecipeDisplay;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RecipeDisplayCommand implements CommandExecutor {

//    /zvierirecipedisplay show arenaId dishCustomEnum

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings != null && strings.length > 0){
            if(!strings[0].equalsIgnoreCase("show")) return true;
            if(strings.length < 3) return true;
            String arenaIdString = strings[1];
            Dish dish = Dish.of(strings[2]);

            UUID arenaId;
            try{
                arenaId = UUID.fromString(arenaIdString);
            } catch (Exception e){
                e.printStackTrace();
                return true;
            }
            ZvieriArena arena = ZvieriArena.get(arenaId);
            if(arena == null) {
                return true;
            }
            RecipeDisplay display = arena.getRecipeDisplay();

            display.applyRecipe(dish);

            if(commandSender instanceof Player){
                Player player = (Player) commandSender;
                player.getWorld().playSound((player.getEyeLocation()), Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 1);
            }
            return true;
        }

        return false;
    }
}
