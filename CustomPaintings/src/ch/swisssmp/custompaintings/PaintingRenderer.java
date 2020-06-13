package ch.swisssmp.custompaintings;

import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PaintingRenderer extends MapRenderer {

    private static Ditherer ditherer;

    private byte[][] pixels;
    private boolean rendered = false;

    private PaintingRenderer(byte[][] pixels){
        this.pixels = pixels;
    }

    protected static void reloadDitherer(){
        ditherer = new Ditherer(MapPalette.getVanillaMap());
    }

    protected static void render(CustomPainting data){
        File directory = new File(CustomPaintingsPlugin.getInstance().getDataFolder(), "images");
        File imageFile = new File(directory, data.getId()+".png");
        if(!imageFile.exists()){
            Bukkit.getLogger().warning(CustomPaintingsPlugin.getPrefix()+" Das CustomPainting "+data.getId()+" konnte nicht gerendert werden, da die Bilddatei fehlt.");
            return;
        }

        BufferedImage image;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            Bukkit.getLogger().warning(CustomPaintingsPlugin.getPrefix()+" Das CustomPainting "+data.getId()+" konnte nicht gerendert werden, da die Bilddatei ungültig ist.");
            e.printStackTrace();
            return;
        }

        double aspectRatio = image.getWidth() / (double) image.getHeight();
        int fullWidth = data.getWidth() * 128;
        int fullHeight = data.getHeight() * 128;

        int croppedPixelHeight = Math.min(Mathf.roundToInt(fullWidth * aspectRatio), fullHeight);
        image = resize(image, fullWidth, croppedPixelHeight);

        // Bukkit.getLogger().info("Resized image: "+image.getWidth()+"x"+image.getHeight());
        if(image.getWidth()>fullWidth || image.getHeight()>fullHeight){
            Bukkit.getLogger().warning("Resized image has incorrect size, skipping "+data.getId());
            return;
        }

        byte[][] pixels = new byte[fullHeight][fullWidth];
        ditherer.ditherOnto(image, pixels);

        int[][] reservedMapIds = data.getReservedMapIds();
        for(int y = 0; y < data.getHeight(); y++){
            for(int x = 0; x < data.getWidth(); x++){
                int mapId = reservedMapIds[y][x];
                MapView view = Bukkit.getMap(mapId);
                if(view==null){
                    Bukkit.getLogger().warning(CustomPaintingsPlugin.getPrefix()+" Fehler beim rendern vom CustomPainting mit der id "+data.getId()+": Map mit id "+mapId+" nicht gefunden!");
                    continue;
                }

                render(view, pixels, x, y);
            }
        }
    }

    private static BufferedImage resize(BufferedImage before, int width, int height){
        Image tmp = before.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static void render(MapView view, byte[][] pixels, int gridX, int gridY){
        view.setLocked(false);
        view.setScale(MapView.Scale.FARTHEST);
        byte[][] partPixels = new byte[128][128];
        for(int y = 0; y < 128; y++) {
            byte[] row = pixels[y+gridY*128];
            System.arraycopy(row, gridX * 128, partPixels[y], 0, 128);
        }
        PaintingRenderer renderer = new PaintingRenderer(partPixels);
        view.getRenderers().clear();
        view.addRenderer(renderer);
        view.setLocked(true);
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if(rendered) return;
        rendered = true;
        for(int y = 0; y < 128; y++) {
            byte[] row = pixels[y];
            for(int x = 0; x < 128; x++) {
                byte color = row[x];
                canvas.setPixel(x, y, color);
            }
        }
        pixels = null;
    }
}
