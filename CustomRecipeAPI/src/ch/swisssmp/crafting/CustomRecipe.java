package ch.swisssmp.crafting;

import org.bukkit.NamespacedKey;

public abstract class CustomRecipe {
    private final NamespacedKey key;

    private boolean registered = false;

    public CustomRecipe(NamespacedKey key){
        this.key = key;
    }

    public NamespacedKey getKey(){
        return key;
    }

    protected void registerRecipe(){
        if(registered) return;
        registered = true;
        register();
    }

    protected void unregisterRecipe(){
        registered = false;
        unregister();
    }

    protected abstract void register();
    protected abstract void unregister();
}
