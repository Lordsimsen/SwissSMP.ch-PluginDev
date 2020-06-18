package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class CustomPaintings {

    public static boolean replace(String id, String sourceUrl){
        if(!load(id, sourceUrl)) return false;
        CustomPainting painting = CustomPaintingContainer.getPainting(id).orElse(null);
        if(painting==null) return false;
        painting.render();
        return true;
    }

    public static boolean replace(String id, File file){
        if(!load(id, file)) return false;
        CustomPainting painting = CustomPaintingContainer.getPainting(id).orElse(null);
        if(painting==null) return false;
        painting.render();
        return true;
    }

    public static CustomPainting create(String id, int width, int height){
        return create(id, null, width, height);
    }

    public static CustomPainting create(String id, String name, int width, int height){
        CustomPainting existing = CustomPaintingContainer.getPainting(id).orElse(null);
        if(existing!=null){
            return existing;
        }

        // reserve minecraft map ids
        World world = Bukkit.getWorlds().get(0);
        int[][] reservedMapIds = reserveMapIds(world, width, height);

        // create the painting data
        return CustomPaintingContainer.createPainting(id, name, width, height, reservedMapIds);
    }

    public static CustomPainting create(String id, int width, int height, String sourceUrl){
        return create(id, null, width, height, sourceUrl);
    }

    public static CustomPainting create(String id, String name, int width, int height, String sourceUrl){
        CustomPainting existing = CustomPaintingContainer.getPainting(id).orElse(null);
        if(existing!=null){
            replace(id, sourceUrl);
            return existing;
        }

        if(!load(id, sourceUrl)) return null;

        // reserve minecraft map ids
        World world = Bukkit.getWorlds().get(0);
        int[][] reservedMapIds = reserveMapIds(world, width, height);

        // create the painting data
        CustomPainting result = CustomPaintingContainer.createPainting(id, name, width, height, reservedMapIds);
        if(result==null) return null;

        // render the maps
        result.render();
        return result;
    }

    public static boolean place(CustomPainting painting, PlayerInteractEvent event){
        BlockFace face = event.getBlockFace();
        Block block = event.getClickedBlock().getRelative(face);
        if(block.getType()!= Material.AIR){
            return false;
        }

        BlockFace right;
        BlockFace up;
        if(face==BlockFace.UP || face==BlockFace.DOWN){
            BlockFace direction = event.getPlayer().getFacing();
            right = getRightFace(direction);
            up = direction;
            if(face==BlockFace.UP) right = right.getOppositeFace();
        }
        else{
            right = getRightFace(face);
            up = BlockFace.UP;
        }

        return PaintingPlacer.place(painting, block, right, up, face);
    }

    protected static void unlink(String id){
        File imageFile = getImageFile(id);

        if(imageFile.exists()){
            imageFile.delete();
        }
    }

    private static File getImagesDirectory(){
        return new File(CustomPaintingsPlugin.getInstance().getDataFolder(), "images");
    }

    private static File getImageFile(String id){
        return new File(getImagesDirectory(), id+".png");
    }

    private static int[][] reserveMapIds(World world, int width, int height){
        int[][] result = new int[height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                MapView view = MapPool.createMap();
                result[y][x] = view.getId();
                if(view.getId()>30000){
                    Bukkit.getLogger().warning(CustomPaintingsPlugin.getPrefix()+" ACHTUNG: Es sind mittlerweile "+(view.getId()+1)+" von 32768 Maps besetzt!");
                }
            }
        }
        return result;
    }

    private static boolean load(String id, String sourceUrl){
        // download the image
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(sourceUrl)); //should be initialized outside the render method
            // Bukkit.getLogger().info(CustomPaintingsPlugin.getPrefix()+" Das Bild ist "+image.getWidth()+", "+image.getHeight()+" Pixel gross.");
        } catch (IOException e) {
            Bukkit.getLogger().info(ChatColor.RED + "[CustomPaintings] Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+sourceUrl);
            e.printStackTrace();
            return false;
        }

        return load(id, image);
    }

    private static boolean load(String id, File file){
        // download the image
        BufferedImage image;
        try {
            image = ImageIO.read(file); //should be initialized outside the render method
            // Bukkit.getLogger().info(CustomPaintingsPlugin.getPrefix()+" Das Bild ist "+image.getWidth()+", "+image.getHeight()+" Pixel gross.");
        } catch (IOException e) {
            Bukkit.getLogger().info(ChatColor.RED + "[CustomPaintings] Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+file.getAbsoluteFile());
            e.printStackTrace();
            return false;
        }

        return load(id, image);
    }

    private static boolean load(String id, BufferedImage image){
        // save the image locally
        File directory = getImagesDirectory();
        if(!directory.exists()){
            directory.mkdirs();
        }

        try {
            ImageIO.write(image, "png", getImageFile(id));
        } catch (IOException e) {
            Bukkit.getLogger().info(ChatColor.RED + "[CustomPaintings] Das Bild konnte nicht gespeichert werden.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static BlockFace getRightFace(BlockFace face){
        switch (face){
            case EAST: return BlockFace.NORTH;
            case NORTH: return BlockFace.WEST;
            case WEST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.EAST;
            default: return null;
        }
    }
}
