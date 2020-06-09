package ch.swisssmp.custompaintings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PaintingCreator {

    public static boolean replace(String id, String sourceUrl){
        if(!load(id, sourceUrl)) return false;
        PaintingData data = PaintingDataContainer.getPainting(id).orElse(null);
        if(data==null) return false;
        data.render();
        return true;
    }

    public static PaintingData create(String id, String sourceUrl, int width, int height){
        PaintingData existing = PaintingDataContainer.getPainting(id).orElse(null);
        if(existing!=null){
            replace(id, sourceUrl);
            return existing;
        }

        if(!load(id, sourceUrl)) return null;

        // reserve minecraft map ids
        World world = Bukkit.getWorlds().get(0);
        int[][] reservedMapIds = new int[height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                MapView view = Bukkit.createMap(world);
                reservedMapIds[y][x] = view.getId();
                if(view.getId()>30000){
                    Bukkit.getLogger().warning(CustomPaintingsPlugin.getPrefix()+" ACHTUNG: Es sind mittlerweile "+(view.getId()+1)+" von 32768 Maps besetzt!");
                }
            }
        }

        // create the painting data
        PaintingData result = PaintingDataContainer.createPainting(id, width, height, reservedMapIds);
        if(result==null) return null;

        // render the maps
        result.render();
        return result;
    }

    private static boolean load(String id, String sourceUrl){
        // download the image
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(sourceUrl)); //should be initialized outside the render method
            Bukkit.getLogger().info(CustomPaintingsPlugin.getPrefix()+" Das Bild ist "+image.getWidth()+", "+image.getHeight()+" Pixel gross.");
        } catch (IOException e) {
            Bukkit.getLogger().info(ChatColor.RED + "[CustomPaintings] Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+sourceUrl);
            e.printStackTrace();
            return false;
        }

        // save the image locally
        File directory = new File(CustomPaintingsPlugin.getInstance().getDataFolder(), "images");
        if(!directory.exists()){
            directory.mkdirs();
        }

        try {
            ImageIO.write(image, "png", new File(directory, id+".png"));
        } catch (IOException e) {
            Bukkit.getLogger().info(ChatColor.RED + "[CustomPaintings] Das Bild konnte nicht gespeichert werden.");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
