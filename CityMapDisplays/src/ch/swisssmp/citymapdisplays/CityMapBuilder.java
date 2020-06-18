package ch.swisssmp.citymapdisplays;

import ch.swisssmp.custompaintings.CustomPaintingsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class CityMapBuilder {

    protected static BufferedImage[][] load(String[][] urls){
        BufferedImage[][] result = new BufferedImage[urls.length][urls[0].length];
        for(int y = 0; y < urls.length; y++) {
            String[] row = urls[urls.length-1-y]; // invert vertically
            for(int x = 0; x < row.length; x++) {
                String url = row[x];
                BufferedImage image = load(url);
                if(image==null) return null;
                result[y][x] = image;
            }
        }
        return result;
    }

    private static BufferedImage load(String url){
        // download the image
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(url)); //should be initialized outside the render method
            // Bukkit.getLogger().info(CustomPaintingsPlugin.getPrefix()+" Das Bild ist "+image.getWidth()+", "+image.getHeight()+" Pixel gross.");
        } catch (IOException e) {
            Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+ChatColor.RED + " Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+url);
            e.printStackTrace();
            return null;
        }

        return image;
    }

    protected static File save(BufferedImage image, UUID uid){

        // save the image locally
        File directory = new File(CityMapDisplaysPlugin.getInstance().getDataFolder(), "images");
        if(!directory.exists()){
            directory.mkdirs();
        }

        try {
            File file = new File(directory, uid+".png");
            ImageIO.write(image, "png", file);
            return file;
        } catch (IOException e) {
            Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+ChatColor.RED + " Das Bild konnte nicht gespeichert werden.");
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage stitch(BufferedImage[][] images, int width, int height, int tileWidth, int tileHeight){
        BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < images.length; y++){
            BufferedImage[] row = images[y];
            for(int x = 0; x < row.length; x++){
                blit(images[y][x], result, tileWidth*x, tileHeight*y);
            }
        }
        return result;
    }

    private static void blit(BufferedImage from, BufferedImage to, int posX, int posY){
        Graphics2D g2d = to.createGraphics();
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g2d.drawImage(from, posX, posY, null);
        g2d.dispose();
    }
}
