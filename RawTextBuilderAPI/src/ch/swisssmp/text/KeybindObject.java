package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;

public class KeybindObject extends RawBase {

    // Content: Keybinds
    private String keybind;

    public KeybindObject(String keybind){
        this.keybind = keybind;
    }

    /**
     * A keybind identifier, to be displayed as the name of the button that is currently bound to a certain action. For
     * example, {"keybind": "key.inventory"} will display "e" if the player is using the default control scheme.
     */
    public KeybindObject setKeybind(String keybind){
        this.keybind = keybind;
        return this;
    }

    public String getKeybind(){
        return keybind;
    }

    @Override
    protected BaseComponent createSpigotComponent() {
        return new KeybindComponent(keybind);
    }

    @Override
    protected void apply(JsonObject json) {
        if(keybind!=null) json.addProperty("keybind", keybind);
    }
}
