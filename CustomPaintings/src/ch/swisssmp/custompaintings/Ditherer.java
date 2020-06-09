package ch.swisssmp.custompaintings;

import org.bukkit.Bukkit;

import java.awt.image.BufferedImage;

public class Ditherer {

    private final MapPalette palette;

    public Ditherer(MapPalette palette) {
        this.palette = palette;
    }

    public void ditherOnto(BufferedImage image, byte[][] imageData) {
        Color[][] pixels = new Color[image.getHeight()][image.getWidth()];
        Bukkit.getLogger().info("Image: "+image.getWidth()+"x"+image.getHeight()+", Data: "+imageData.length+"x"+imageData[0].length+", Pixels: "+pixels.length+"x"+pixels[0].length);
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                java.awt.Color c = new java.awt.Color(image.getRGB(x, y), true);
                if(c.getAlpha()==0) continue;
                short red =   (short)c.getRed();
                short green = (short)c.getGreen();
                short blue =  (short)c.getBlue();
                pixels[y][x] = new Color(red,green,blue);
            }
        }

        final double m1 = 7.0/16;
        final double m2 = 3.0/16;
        final double m3 = 5.0/16;
        final double m4 = 1.0/16;

        for(int y = 0; y < pixels.length; y++) {
            Color[] row = pixels[y];
            Color[] nextRow = y+1 < pixels.length ? pixels[y+1] : null;
            for(int x = 0; x < row.length; x++) {
                Color color = row[x];
                if(color==null) {
                    imageData[y][x] = 0;
                    continue;
                }
                MapColor closest = palette.getMapColor(color.r, color.g, color.b);
                imageData[y][x] = (byte) closest.getId();
                Color error = new Color(color.r - closest.getRed(), color.g - closest.getGreen(), color.b - closest.getBlue());
                if(x+1<row.length && row[x+1]!=null) row[x+1].add(error.multiply(m1));
                if(nextRow!=null&&x>0&&nextRow[x-1]!=null) nextRow[x-1].add(error.multiply(m2));
                if(nextRow!=null&&nextRow[x]!=null) nextRow[x].add(error.multiply(m3));
                if(nextRow!=null&&x<nextRow.length-1&&nextRow[x+1]!=null) nextRow[x+1].add(error.multiply(m4));
            }
        }
    }

    private class Color{
        public short r;
        public short g;
        public short b;

        public Color(int r, int g, int b) {
            this((short)r,(short)g,(short)b);
        }
        public Color(short r, short g, short b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
        public void add(Color c) {
            r+=c.r;
            g+=c.g;
            b+=c.b;
        }
        public Color multiply(double d) {
            return new Color((short)Math.floor(r*d),(short)Math.floor(g*d),(short)Math.floor(b*d));
        }
    }
}
