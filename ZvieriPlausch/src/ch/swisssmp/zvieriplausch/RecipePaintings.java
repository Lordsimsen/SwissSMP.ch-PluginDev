package ch.swisssmp.zvieriplausch;

import org.bukkit.Bukkit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
Saves paintings of recipes locally instead of downloading at each interaction.
 */
public class RecipePaintings {

    protected static void download(){
        File directory = getPaintingsDirectory();
        if(!directory.exists()){
            directory.mkdirs();
        }
        for(Dish dish : Dish.values()){
            File file = getLocalFile(dish);
            if(file.exists()) continue;
            String url = getUrl(dish);
            try {
                BufferedImage image = ImageIO.read(new URL(url));
                ImageIO.write(image, "png", file);
            } catch (Exception e){
                Bukkit.getLogger().info(ZvieriPlauschPlugin.getPrefix() + " Couldn't load image for " + dish.toString());
                e.printStackTrace();
            }
        }
    }

    private static String getUrl(Dish dish){
        return "https://minecraft.swisssmp.ch/public/recipes/zvieri_" + dish.toString().toLowerCase() + ".png";
    }

    public static File getLocalFile(Dish dish){
        return new File(getPaintingsDirectory(), dish.toString().toLowerCase() + ".png");
    }

    private static File getPaintingsDirectory(){
        return new File(ZvieriPlauschPlugin.getInstance().getDataFolder(), "paintings");
    }
}
