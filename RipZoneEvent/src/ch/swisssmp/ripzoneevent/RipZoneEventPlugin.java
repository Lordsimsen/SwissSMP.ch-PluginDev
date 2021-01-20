package ch.swisssmp.ripzoneevent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class RipZoneEventPlugin extends JavaPlugin {

    protected static RipZoneEventPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

		/*
		Register early because of CustomItemBuilders in RegisterCraftingRecipe
		 */
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
