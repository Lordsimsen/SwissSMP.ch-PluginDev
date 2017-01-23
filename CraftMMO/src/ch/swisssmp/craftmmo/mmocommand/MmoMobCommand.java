package ch.swisssmp.craftmmo.mmocommand;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoentity.IControllable;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;
import ch.swisssmp.craftmmo.mmoworld.MmoWorld;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MmoMobCommand implements CommandExecutor{
	Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		Main.info("Can only be executed from within the game.");
    		return true;
    	}
    	if(args.length==0){
    		displayHelp();
    		return true;
    	}
    	switch(args[0]){
	    	case "help":
	    		displayHelp();
	    		break;
	    	case "summon":
	    		if(args.length>=2){
	    			try{
	    				String string_id = args[1];
	    				if(!NumberUtils.isNumber(string_id)){
	    					player.sendMessage(ChatColor.RED+args[1]+" ist keine gültige ID!");
	    					break;
	    				}
	    				if(args.length>=3){
		    				String string_level = args[2];
		    				if(!NumberUtils.isNumber(string_level)){
		    					player.sendMessage(ChatColor.RED+args[2]+" ist kein gültiges Level! *facepalm*");
		    				}
	    				}
		    			World world = player.getWorld();
		    			MmoWorld mmoWorld = MmoWorld.get(world);
		    			if(mmoWorld==null){
		    				player.sendMessage(ChatColor.RED+"Diese Welt ist keine registrierte Mmo Welt!");
		    				break;
		    			}
	    				player.sendMessage(ChatColor.GRAY+"Versuche "+args[1]+" zu generieren");
		    			Integer mmo_mob_id = Integer.parseInt(string_id);
		    			MmoMob mmoMob = MmoMob.templates.get(mmo_mob_id);
		    			if(mmoMob==null){
		    				player.sendMessage(ChatColor.RED+"Mob mit der ID "+mmo_mob_id+" nicht gefunden.");
		    			}
		    			else{
		    				player.sendMessage(ChatColor.GRAY+args[1]+" heisst "+mmoMob.name);
			    			Entity entity = mmoMob.spawnInstance(player.getLocation());
			    			if(entity==null){
			    				player.sendMessage(ChatColor.RED+"Der Einheitstyp "+mmoMob.entityType.toString()+" ist ungültig.");
			    			}
			    			else if(entity.isValid()){
			    				player.sendMessage(ChatColor.GRAY+mmoMob.name+" ist erschienen!");
			    			}
			    			else{
			    				player.sendMessage(ChatColor.RED+"Beim erschaffen ist etwas schiefgelaufen.");
			    			}
		    			}
	    			}
	    			catch(Exception e){
	    				player.sendMessage(ChatColor.RED+"Konnte "+args[1]+" nicht generieren");
	    				e.printStackTrace();
	    			}
	    		}
	    		break;
	    	case "reload":
				try {
					MmoMob.loadMobs();
					List<World> worlds = Bukkit.getWorlds();
		    		for(World world : worlds){
		    			List<Entity> entities = world.getEntities();
		    			for(Entity entity : entities){
		    				if(!(entity instanceof LivingEntity)){
		    					continue;
		    				}
		    				LivingEntity livingEntity = (LivingEntity) entity;
		    				MmoMob mmoMob = MmoMob.get(livingEntity);
		    				if(mmoMob==null)
		    					continue;
		    				mmoMob.applyData(livingEntity);
		    			}
		    		}
		    		player.sendMessage("[CraftMMO] Mob-Konfiguration neu geladen.");
				} catch (Exception e) {
					player.sendMessage("Fehler beim laden der Daten! Mehr Details in der Konsole...");
					e.printStackTrace();
				}
				break;
	    	case "savetofile":{
	    		YamlConfiguration output = new YamlConfiguration();
	    		ArrayList<UUID> dead = new ArrayList<UUID>();
	    		for(IControllable iControllable : MmoMob.invincible_mobs.values()){
    				if(iControllable == null){
    					continue;
    				}
    				Entity entity = iControllable.getEntity().getBukkitEntity();
    				if(entity.isDead())
    					continue;
    				ConfigurationSection dataSection = output.createSection(entity.getUniqueId().toString());
    				dataSection.set("mmo_mob_id", iControllable.getSaveData().mmo_mob_id);
    				dataSection.set("x", entity.getLocation().getX());
    				dataSection.set("y", entity.getLocation().getY());
    				dataSection.set("z", entity.getLocation().getZ());
	    		}
	    		for(UUID uuid : dead){
	    			MmoMob.invincible_mobs.remove(uuid);
	    		}
	    		File outputFile = new File(Main.dataFolder, "mobs_temp.yml");
    			try {
    	    		if(!outputFile.exists()){
    	    			outputFile.createNewFile();
    	    		}
		    		output.save(outputFile);
					player.sendMessage(ChatColor.GRAY+"Daten gespeichert.");
				} catch (IOException e) {
					player.sendMessage(ChatColor.RED+"Fehler beim Speichern der Daten, schau in die Konsole.");
					e.printStackTrace();
				}
	    		break;
	    	}
	    	case "loadfromfile":{
	    		File dataFile = new File(Main.dataFolder, "mobs_temp.yml");
	    		YamlConfiguration yamlConfiguration = new YamlConfiguration();
	    		try {
					yamlConfiguration.load(dataFile);
					for(String key : yamlConfiguration.getKeys(false)){
						ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection(key);
						int mmo_mob_id = dataSection.getInt("mmo_mob_id");
						MmoMob mmoMob = MmoMob.get(mmo_mob_id);
						if(mmoMob==null){
							continue;
						}
						double x = dataSection.getDouble("x");
						double y = dataSection.getDouble("y");
						double z = dataSection.getDouble("z");
						mmoMob.spawnInstance(new Location(Bukkit.getWorld("world"), x, y, z));
						player.sendMessage(ChatColor.GRAY+"Daten geladen.");
					}
				} catch (IOException | InvalidConfigurationException e) {
					player.sendMessage(ChatColor.RED+"Fehler beim Laden der Daten, schau in die Konsole.");
					e.printStackTrace();
				}
	    		break;
	    	}
	    	case "killall":{
	    		if(args.length<1){
	    			break;
	    		}
	    		if(!args[0].equals("confirm")){
	    			break;
	    		}
				List<World> worlds = Bukkit.getWorlds();
	    		for(World world : worlds){
	    			List<Entity> entities = world.getEntities();
	    			for(Entity entity : entities){
	    				if(!(entity instanceof LivingEntity)){
	    					continue;
	    				}
	    				LivingEntity livingEntity = (LivingEntity) entity;
	    				MmoMob mmoMob = MmoMob.get(livingEntity);
	    				if(mmoMob==null)
	    					continue;
	    				player.sendMessage(mmoMob.name+" entfernt.");
	    				livingEntity.remove();
	    			}
	    		}
	    	}
		}
		return true;
	}
    public void displayHelp(){
    	player.sendMessage("/MmoMob = /mmomob");
    	player.sendMessage("-----");
    	player.sendMessage("/mmomob help - Zeigt diese Hilfe an");
    }
}
