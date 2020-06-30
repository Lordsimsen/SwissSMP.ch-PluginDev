package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class TranslatedText extends RawBase {

    // Content: Translated Text
    private String translate;
    private String with;

    public TranslatedText(String translate){
        this.translate = translate;
    }

    public TranslatedText(String translate, String with){
        this(translate);
        this.with = with;
    }

    /**
     * A translation identifier, to be displayed as the corresponding text in the player's selected language. If no
     * corresponding translation can be found, the identifier itself will be used as the translation text. This
     * identifier is the same as the identifiers found in lang files from assets or resource packs.
     */
    public TranslatedText setTranslate(String translate){
        this.translate = translate;
        return this;
    }

    public String getTranslate(){
        return translate;
    }

    /**
     * Optional. A list of raw JSON text component arguments to be inserted into slots in the translation text. Ignored
     * if translate is not present.
     *
     * Translations can contain slots for text that is not known ahead of time, such as player names. These slots
     * are defined in the translation text itself, not in the JSON text component, and generally take the form %s
     * (displays the next argument) or %1$s (displays the first argument; replace 1 with whichever index is
     * desired).[3] If no argument is provided for a slot, the slot will not be displayed.
     */
    public TranslatedText setWith(String with){
        this.with = with;
        return this;
    }

    public String getWith(){
        return with;
    }

    @Override
    protected BaseComponent createSpigotComponent() {
        return translate!=null
                ? with!=null ? new TranslatableComponent(translate, with) : new TranslatableComponent(translate)
                : new TranslatableComponent();
    }

    @Override
    protected void apply(JsonObject json) {
        json.addProperty("translate", translate!=null ? translate : "");
        if(with!=null) json.addProperty("with", with);
    }
}
