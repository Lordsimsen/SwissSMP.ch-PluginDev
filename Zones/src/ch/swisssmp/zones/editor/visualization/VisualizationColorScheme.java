package ch.swisssmp.zones.editor.visualization;

import org.bukkit.Color;

public class VisualizationColorScheme {
    private final Color normalColor;
    private final Color goodColor;
    private final Color badColor;

    public VisualizationColorScheme(Color normalColor, Color goodColor, Color badColor){
        this.normalColor = normalColor;
        this.goodColor = goodColor;
        this.badColor = badColor;
    }

    public Color getNormalColor(){return normalColor;}
    public Color getGoodColor(){return goodColor;}
    public Color getBadColor(){return badColor;}
}
