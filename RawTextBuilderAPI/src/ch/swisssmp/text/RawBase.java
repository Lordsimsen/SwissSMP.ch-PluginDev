package ch.swisssmp.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class RawBase {

    // Children
    private RawBase[] extra;

    // Formatting
    private Color color;
    private String font;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;

    // Interactivity
    private String insertion;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    public RawBase(){

    }

    public RawBase(RawBase... contents){
        this.extra = contents;
    }

    public RawBase extra(RawBase... extra){
        this.extra = extra;
        return this;
    }

    public RawBase[] extra(){
        return extra;
    }

    /**
     * Optional. The color to render the content in.
     */
    public RawBase color(Color color){
        this.color = color;
        return this;
    }

    public RawBase color(ChatColor color){
        this.color = convertChatColor(color);
        return this;
    }

    /**
     * Optional. The color to render the content in. Valid values are "black", "dark_blue", "dark_green", "dark_aqua",
     * "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow",
     * "white", and "reset" (cancels out the effects of colors used by parent objects).
     * Set to "#<hex>" to insert any color in the hexadecimal color format. Example: Using "#FF0000" makes the component
     * red. Can either be a full 6-digit value, or 3-digit.
     */
    public RawBase color(String hex){
        this.color = hexToColor(hex);
        return this;
    }

    public String getHexColor(){
        return colorToHex(color);
    }

    public Color color(){
        return color;
    }

    /**
     * Optional. The resource location of the font for this component in the resource pack within
     * assets/<namespace>/font. Defaults to "minecraft:default".
     */
    public RawBase font(String font){
        this.font = font;
        return this;
    }

    public String font(){
        return font;
    }

    /**
     * Optional. Whether to render the content in bold.
     */
    public RawBase bold(boolean bold){
        this.bold = bold;
        return this;
    }

    public boolean bold(){
        return bold;
    }

    /**
     * Optional. Whether to render the content in italics. Note that text which is italicized by default, such as custom
     * item names, can be unitalicized by setting this to false.
     */
    public RawBase italic(boolean italic){
        this.italic = italic;
        return this;
    }

    public boolean italic(){
        return italic;
    }

    /**
     * Optional. Whether to underline the content.
     */
    public RawBase underlined(boolean underlined){
        this.underlined = underlined;
        return this;
    }

    public boolean underlined(){
        return underlined;
    }

    /**
     * Optional. Whether to strikethrough the content.
     */
    public RawBase strikethrough(boolean strikethrough){
        this.strikethrough = strikethrough;
        return this;
    }

    public boolean strikethrough(){
        return underlined;
    }

    /**
     * Optional. Whether to render the content obfuscated.
     */
    public RawBase obfuscated(boolean obfuscated){
        this.obfuscated = obfuscated;
        return this;
    }

    public boolean obfuscated(){
        return obfuscated;
    }

    /**
     * Optional. When the text is shift-clicked by a player, this string is inserted in their chat input. It does not
     * overwrite any existing text the player was writing. This only works in chat messages.
     */
    public RawBase insertion(String insertion){
        this.insertion = insertion;
        return this;
    }

    public String insertion(){
        return insertion;
    }

    /**
     * Optional. Allows for events to occur when the player clicks on text. Only work in chat messages and written
     * books, unless specified otherwise.
     */
    public RawBase clickEvent(ClickEvent clickEvent){
        this.clickEvent = clickEvent;
        return this;
    }

    public ClickEvent clickEvent(){
        return clickEvent;
    }

    /**
     * Optional. Allows for a tooltip to be displayed when the player hovers their mouse over text.
     */
    public RawBase hoverEvent(HoverEvent hoverEvent){
        this.hoverEvent = hoverEvent;
        return this;
    }

    public HoverEvent hoverEvent(){
        return hoverEvent;
    }

    public BaseComponent spigot(){
        BaseComponent result = createSpigotComponent();
        if(extra!=null && extra.length>0){
            result.setExtra(Stream.of(this.extra).map(RawBase::spigot).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if(color!=null) result.setColor(net.md_5.bungee.api.ChatColor.of(colorToHex(color)));
        if(font!=null) result.setFont(font);
        if(bold!=null) result.setBold(bold);
        if(italic!=null) result.setItalic(italic);
        if(underlined !=null) result.setUnderlined(underlined);
        if(strikethrough!=null) result.setStrikethrough(strikethrough);
        if(obfuscated!=null) result.setObfuscated(obfuscated);

        if(insertion!=null) result.setInsertion(insertion);
        if(clickEvent!=null) result.setClickEvent(clickEvent.spigot());
        if(hoverEvent!=null) result.setHoverEvent(hoverEvent.spigot());
        return result;
    }

    protected abstract BaseComponent createSpigotComponent();

    @Override
    public String toString(){
        return toJson().toString();
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        this.apply(json);
        if(extra!=null && extra.length>0){
            JsonArray extraArray = new JsonArray();
            for(RawBase e : extra){
                JsonElement element = e.toJson();
                if(element==null) continue;
                extraArray.add(element);
            }
            if(extraArray.size()>0){
                json.add("extra", extraArray);
            }
        }
        if(color!=null) json.addProperty("color", colorToHex(color));
        if(font!=null) json.addProperty("font", font);
        if(bold!=null) json.addProperty("bold", bold);
        if(italic!=null) json.addProperty("italic", italic);
        if(underlined !=null) json.addProperty("underlined", underlined);
        if(strikethrough!=null) json.addProperty("strikethrough", strikethrough);
        if(obfuscated!=null) json.addProperty("obfuscated", obfuscated);

        if(insertion!=null) json.addProperty("insertion", insertion);
        if(clickEvent!=null) clickEvent.apply(json);
        if(hoverEvent!=null) hoverEvent.apply(json);
        return json;
    }

    protected abstract void apply(JsonObject json);

    private static String colorToHex(Color color){
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * @param colorStr e.g. "#FFFFFF"
     */
    public static Color hexToColor(String colorStr) {
        if(colorStr.length()==4){
            String rString = colorStr.substring(1,1);
            String gString = colorStr.substring(2,2);
            String bString = colorStr.substring(3,3);
            colorStr = "#"+rString+rString+gString+gString+bString+bString;
        }
        return Color.fromRGB(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public static Color convertChatColor(ChatColor color){
        switch(color){
            case DARK_RED:return hexToColor("#AA0000");
            case RED:return hexToColor("#FF5555");
            case GOLD:return hexToColor("#FFAA00");
            case YELLOW:return hexToColor("#FFFF55");
            case DARK_GREEN:return hexToColor("#00AA00");
            case GREEN:return hexToColor("#55FF55");
            case AQUA:return hexToColor("#55FFFF");
            case DARK_AQUA:return hexToColor("#00AAAA");
            case DARK_BLUE:return hexToColor("#0000AA");
            case BLUE:return hexToColor("#5555FF");
            case LIGHT_PURPLE:return hexToColor("#FF55FF");
            case DARK_PURPLE:return hexToColor("#AA00AA");
            case WHITE:return hexToColor("#FFFFFF");
            case GRAY:return hexToColor("#AAAAAA");
            case DARK_GRAY:return hexToColor("#555555");
            case BLACK:return hexToColor("#000000");
            default: return null;
        }
    }
}
