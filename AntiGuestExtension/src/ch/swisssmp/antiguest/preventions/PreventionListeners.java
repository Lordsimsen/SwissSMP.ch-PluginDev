package ch.swisssmp.antiguest.preventions;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import ch.swisssmp.antiguest.AntiGuestExtension;
import ch.swisssmp.antiguest.preventions.blocks.*;
import ch.swisssmp.antiguest.preventions.entities.*;
import ch.swisssmp.antiguest.preventions.special.*;

public class PreventionListeners {
	
	private static boolean registered = false;
	
	public static void register() {
		if(registered) return;
		registered = true;
		AntiGuestExtension plugin = AntiGuestExtension.getInstance();
		Bukkit.getPluginManager().registerEvents(new BlockInteractPreventionListener(new BlockInteractPrevention[]{
			new Anvil(),
			new Barrel(),
			new Beacon(),
			new Beehive(),
			new BeeNest(),
			new BlastFurnace(),
			new Campfire(),
			new CartographyTable(),
			new Chest(),
			new Comparator(),
			new Composter(),
			new Dispenser(),
			new Dropper(),
			new EnchantingTable(),
			new EnderChest(),
			new EndPortalFrame(),
			new FletchingTable(),
			new Furnace(),
			new Grindstone(),
			new Jukebox(),
			new Loom(),
			new NoteBlock(),
			new Repeater(),
			new ShulkerBox(),
			new SmithingTable(),
			new Smoker(),
			new StoneCutter(),
			new TrappedChest()
		}), plugin);
		Bukkit.getPluginManager().registerEvents(new EntityInteractPreventionListener(new EntityInteractPrevention[]{
				new Horse(),
				new ItemFrame(),
				new Pig(),
				new Villager()
			}), plugin);
		Bukkit.getPluginManager().registerEvents(new Damage(), plugin);
		Bukkit.getPluginManager().registerEvents(new Lectern(), plugin);
		Bukkit.getPluginManager().registerEvents(new ArmorStand(), plugin);
	}
	
	public static void unregister() {
		if(!registered) return;
		registered = false;
		HandlerList.unregisterAll(AntiGuestExtension.getInstance());
	}
}
