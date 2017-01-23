package ch.swisssmp.regioneffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.regioneffects.Main;

public class RegionEffectPlayerCommand implements CommandExecutor{
	private boolean consoleMode;
	private Player p;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	consoleMode = true;
    	p = null;
    	if(sender instanceof Player){
    		consoleMode = false;
    		p = (Player)sender;
    		if(!p.hasPermission("RegionEffects.use")){
    			p.sendMessage("Du hast keinen Zugriff auf diesen Befehl!");
    			return false;
    		}
    	}
    	if(args.length<1){
    		displayHelp();
    		return false;
    	}
    	else if(args[0].equals("help")){
    		if(args.length>=2){
    			if(args[1].equals("Aktionen")){
    				sendMessage("RegionEffects 'add' Aktionen erklärt");
    				sendMessage("specific: Alle anderen Effekte werden entfernt.");
    				sendMessage("regionOnly: Effekt gilt nur innerhalb der Region und wird beim Verlassen entfernt");
    				return true;
    			}
    		}
    		displayHelp();
    		return true;
    	}
    	if(args.length < 2){
    		displayHelp();
    		return false;
    	}
		boolean regionOnly = false;
		boolean specific = false;
		String region = args[0].toLowerCase();
		String command = args[1].toLowerCase();
		if(command.equals("a")){
			command="add";
		}
		else if(command.equals("addspecific")||command.equals("as")){
			command="add";
			specific = true;
		}
		else if(command.equals("addregiononly")||command.equals("ar")){
			command="add";
			regionOnly = true;
		}
		else if(command.equals("addspecificregiononly")||command.equals("addregiononlyspecific")||command.equals("ars")||command.equals("asr")){
			command="add";
			specific = true;
			regionOnly = true;
		}
		else if(command.equals("change")||command.equals("e")||command.equals("edit")){
			command = "add";
		}
		else if(command.equals("delete")||command.equals("r")){
			command = "remove";
		}
		else if(command.equals("ac")){
			command = "addcondition";
		}
		else if(command.equals("rc")){
			command = "removecondition";
		}
		else if(command.equals("cc")||command.equals("clearcondition")){
			command = "clearconditions";
		}
		else if(command.equals("ap")){
			command = "addpermission";
		}
		else if(command.equals("rp")){
			command = "removepermission";
		}
		else if(command.equals("cp")||command.equals("clearpermission")){
			command = "clearpermissions";
		}
		else if(command.equals("lp")||command.equals("listpermission")){
			command = "listpermissions";
		}
		switch(command){
		case "add":
			return add(p, region, args, regionOnly, specific);
		case "remove":
			return remove(region, args);
		case "clear":
			return clear(region);
		case "info":
			return info(region, args);
		case "addcondition":
			return addcondition(region, args);
		case "removecondition":
			return removecondition(region, args);
		case "clearconditions":
			return clearconditions(region);
		case "addpermission":
			return addpermission(region, args);
		case "removepermission":
			return removepermission(region, args);
		case "clearpermissions":
			return clearpermissions(region);
		case "listpermissions":
			return listpermissions(p, region);
		default:
    		displayHelp();
		}
    	return true;
    }
    private boolean add(Player p, String region, String[] args, boolean regionOnly, boolean specific){
    	String effect;
    	ConfigurationSection regionSection;
    	ConfigurationSection effectSection;
    	if(args.length < 3){
    		displayHelp();
    		return false;
    	}
		effect = args[2].toLowerCase();
		if(PotionEffectType.getByName(effect)==null)
		{
			sendMessage("Fehler: Effekt "+effect+" existiert nicht!");
			return false;
		}
		int time = 10;
		int amplifier = 0;
		boolean ambient = false;
		boolean particles = true;
		String color = "default";
		if(args.length>3){
			try{
				time = Integer.parseInt(args[3]);
			}
			catch(Exception e){
				sendMessage("Warnung: Effektdauer ungültig: "+args[3]);
				sendMessage("Verwende Standardwert: "+time);
			}
		}
		if(args.length>4){
			try{
				amplifier = Integer.parseInt(args[4]);
			}
			catch(Exception e){
				sendMessage("Warnung: Verstärker ungültig: "+args[4]);
				sendMessage("Verwende Standardwert: "+amplifier);
			}
		}
		if(args.length>5){
			if(args[5].toLowerCase().equals("true") || args[5].toLowerCase().equals("ja"))
				ambient = true;
			else
				ambient = false;
		}
		if(args.length>6){
			if(args[6].toLowerCase().equals("true") || args[6].toLowerCase().equals("ja"))
				particles = true;
			else
				particles = false;
		}
		if(args.length>7){
			color = args[7];
		}
		World world = p.getWorld();
		RegionManager manager = Main.container.get(world);
		Map<String, ProtectedRegion> regions = manager.getRegions();
		if(regions.containsKey(region)){
			if(!Main.regions.contains(region)){
				regionSection = Main.regions.createSection(region);
			}
			else regionSection = Main.regions.getConfigurationSection(region);
			if(!regionSection.contains("effects")){
				effectSection = regionSection.createSection("effects");
			}
			else effectSection = regionSection.getConfigurationSection("effects");
			ConfigurationSection potionSection;
			if(!effectSection.contains(effect)){
				potionSection = effectSection.createSection(effect);
			}
			else potionSection = effectSection.getConfigurationSection(effect);
			potionSection.set("type", effect);
			potionSection.set("duration", time);
			potionSection.set("amplifier", amplifier);
			potionSection.set("ambient", ambient);
			potionSection.set("particles", particles);
			potionSection.set("color", color);
			potionSection.set("regionOnly", regionOnly);
			potionSection.set("specific", specific);
			sendMessage("Effekt "+effect+" in der Region "+region+" erstellt!");
		}
		else{
			sendMessage("Region existiert nicht: "+region);
		}
		//Main.container.get(world)
		return true;
    }
    private boolean remove(String region, String[] args){
    	ConfigurationSection regionSection;
    	ConfigurationSection effectSection;
    	if(args.length < 3){
    		displayHelp();
    		return false;
    	}
		String effect = args[2].toLowerCase();
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		if(regionSection.contains("effects")){
			effectSection = regionSection.getConfigurationSection("effects");
		}
		else{
			sendMessage("Region "+region+" hat keine Effekte.");
			return false;
		}
		if(effectSection.contains(effect)){
			effectSection.set(effect,  null);
			sendMessage("Effekt "+effect+" in der Region "+region+" entfernt!");
		}
		else{
			sendMessage("Effekt "+effect+" nicht gefunden. (Existiert nicht)");
			return false;
		}
		return true;
    }
    private boolean clear(String region){
		if(Main.regions.contains(region)){
			Main.regions.set(region, null);
			sendMessage("Alle Effekte von der Region "+region+" entfernt.");
		}
		else sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
		return true;
    }
    private boolean info(String region, String[] args){
    	String effect;
    	ConfigurationSection regionSection;
    	ConfigurationSection effectSection;
		List<String> permissionList;
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		if(args.length>2){
			effect = args[2];
			if(effect.equals("permissions")){
				if(regionSection.contains("permissions")){
					permissionList = regionSection.getStringList("permissions");
				}
				else{
					sendMessage("Region "+region+" hat keine Berechtigungs-Einschränkungen.");
					return false;
				}
				sendMessage("RegionEffects Region "+region+" Effekt "+effect+" Permissions:");
				sendMessage("------");
				sendMessage("Spieler benötigen:");
				for(String permission : permissionList)
					sendMessage(permission);
				sendMessage("------");
				return false;
			}
			if(regionSection.contains("effects")){
				effectSection = regionSection.getConfigurationSection("effects");
			}
			else{
				sendMessage("Region "+region+" hat keine Effekte.");
				return false;
			}
			sendMessage("RegionEffects Information:");
			ConfigurationSection potion = effectSection.getConfigurationSection(effect);
			String _type = potion.getString("type");
			int duration = potion.getInt("duration");
			int amplifier = potion.getInt("amplifier");
			boolean ambient = potion.getBoolean("ambient");
			boolean particles = potion.getBoolean("particles");
			String _color = potion.getString("color");
			boolean specific = potion.getBoolean("specific");
			boolean regionOnly = potion.getBoolean("regionOnly");
			sendMessage(region+"/"+_type+":");
			sendMessage("Dauer: "+duration);
			sendMessage("Verstärker: "+amplifier);
			sendMessage("Umgebung: "+ambient);
			sendMessage("Partikel: "+particles);
			sendMessage("Farbe: "+_color);
			sendMessage("Spezifisch: "+specific);
			sendMessage("Nur innnerhalb der Region: "+regionOnly);
			sendMessage("Bedingungen anzeigen:");
			sendMessage("/re "+region+" info "+effect+" permissions");
		}
		else{
			sendMessage("RegionEffects Information:");
			String message = region+": ";
			if(regionSection.contains("effects")){
				effectSection = regionSection.getConfigurationSection("effects");
			}
			else{
				sendMessage("Region "+region+" hat keine Effekte.");
				return false;
			}
			Set<String> effects = effectSection.getKeys(false);
			for (String effectName : effects){
				message += effectName+", ";
			}
			message.substring(0,  message.length()-2);
			sendMessage(message);
		}
		return true;
    }
    private boolean addcondition(String region, String[] args){
    	ConfigurationSection regionSection;
		List<String> permissionList;
		//re [region] ac [permission]
    	if(args.length < 3){
    		displayHelp();
    		return false;
    	}
		String permission = args[2].trim();
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		permissionList = regionSection.getStringList("permissions");
		if(!permissionList.contains(permission))
			permissionList.add(permission);
		regionSection.set("permissions", permissionList);
		sendMessage("Bedingung "+permission+" der Region "+region+" hinzugefügt.");
		return true;
    }
    private boolean removecondition(String region, String[] args){
		//re [region] rc [permission]
    	ConfigurationSection regionSection;
		List<String> permissionList;
    	if(args.length < 3){
    		displayHelp();
    		return false;
    	}
		String permission = args[2].trim();
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		if(!regionSection.contains("permissions")){
			permissionList = new ArrayList<String>();
		}
		else permissionList = regionSection.getStringList("permissions");
		if(permissionList.contains(permission))
			permissionList.remove(permission);
		regionSection.set("permissions", permissionList);
		sendMessage("Bedingung "+permission+" von der Region "+region+" entfernt.");
		return true;
    }
    private boolean clearconditions(String region){
		//re [region] cc
    	ConfigurationSection regionSection;
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		regionSection.set("permissions", null);
		sendMessage("Bedingungen von der Region "+region+" entfernt.");
		return true;
    }
    private boolean addpermission(String region, String[] args){
    	ConfigurationSection regionSection;
		List<String> permissionList;
		//re [region] ac [permission]
    	if(args.length < 3){
    		displayHelp();
    		return true;
    	}
		String permission = args[2].trim();
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			regionSection = Main.regions.createSection(region);
		}
		permissionList = regionSection.getStringList("give_permissions");
		if(!permissionList.contains(permission))
			permissionList.add(permission);
		regionSection.set("give_permissions", permissionList);
		sendMessage("Berechtigung "+permission+" der Region "+region+" hinzugefügt.");
		return true;
    }
    private boolean removepermission(String region, String[] args){
		//re [region] rc [permission]
    	ConfigurationSection regionSection;
		List<String> permissionList;
    	if(args.length < 3){
    		displayHelp();
    		return true;
    	}
		String permission = args[2].trim();
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return true;
		}
		if(!regionSection.contains("give_permissions")){
			permissionList = new ArrayList<String>();
		}
		else permissionList = regionSection.getStringList("give_permissions");
		if(permissionList.contains(permission))
			permissionList.remove(permission);
		regionSection.set("give_permissions", permissionList);
		sendMessage("Berechtigung "+permission+" von der Region "+region+" entfernt.");
		return true;
    }
    private boolean clearpermissions(String region){
		//re [region] cc
    	ConfigurationSection regionSection;
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		regionSection.set("give_permissions", null);
		sendMessage("Bedingungen von der Region "+region+" entfernt.");
		return true;
    }
    private boolean listpermissions(Player p, String region){
		//re [region] cc
    	ConfigurationSection regionSection;
		List<String> permissionList;
		if(Main.regions.contains(region)){
			regionSection = Main.regions.getConfigurationSection(region);
		}
		else{
			sendMessage("Region "+region+" nicht gefunden. (Existiert nicht oder hat keine Effekte)");
			return false;
		}
		permissionList = regionSection.getStringList("give_permissions");
		if(permissionList.size() > 0){
			sendMessage("RegionEffects Information:");
			sendMessage("Berechtigungen beim Aufenthalt in "+region);
			sendMessage("-----");
			for(String entry : permissionList){
				sendMessage(entry);
			}
		}
		else{
			sendMessage("Keine speziellen Berechtigungen in der Region "+region);
		}
		return true;
    }
    public void displayHelp(){
		sendMessage("RegionEffects Version "+Main.pdfFile.getVersion()+" Befehle:");
		sendMessage("/RegionEffects = /re");
		sendMessage("-----");
		sendMessage("/re help");
		sendMessage("/re [Region] info (Effekt)");
		sendMessage("/re [Region] [Aktion*] [Effekt] [Dauer=10] [Verstärker=0] [Umgebung=Nein] [Partikel=Ja]");
		sendMessage("/re [Region] remove [Effekt]");
		sendMessage("/re [Region] clear");
		sendMessage("/re [Region] addcondition [permission]");
		sendMessage("/re [Region] removecondition [permission]");
		sendMessage("/re [Region] clearconditions");
		sendMessage("/re [Region] addpermission [permission]");
		sendMessage("/re [Region] removepermission [permission]");
		sendMessage("/re [Region] clearpermissions");
		sendMessage("/re [Region] listpermissions");
		sendMessage("*Aktionen: add, addspecific, addregionOnly, addspecificregiononly");
		sendMessage("/re help Aktionen");
    }
    private void sendMessage(String message){
		if(consoleMode)
			Main.logger.info(message);
		else
			p.sendMessage(message);
    }
}
