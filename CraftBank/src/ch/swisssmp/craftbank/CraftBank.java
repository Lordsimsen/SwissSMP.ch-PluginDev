package ch.swisssmp.craftbank;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CraftBank extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	public static File dataFolder;
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		String[] lines = event.getLines();
		if(!lines[0].toLowerCase().contains("[bank]"))
			return;
		Player player = event.getPlayer();
		if(!player.hasPermission("craftbank.employee")){
			player.sendMessage(ChatColor.RED+"Du kannst keine Bank-Schalter erstellen.");
			event.setCancelled(true);
			return;
		}
		createBankSign(event);
	}
	public void createBankSign(SignChangeEvent event){
		SwissSMPler player = SwissSMPler.get(event.getPlayer());
		String[] lines = event.getLines();
		Location location = player.getLocation();
		List<String> regionIds = WorldGuardPlugin.inst().getRegionManager(player.getWorld()).getApplicableRegionsIDs(new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()));
		List<String> arguments = new ArrayList<String>();
		try {
			for(String regionId : regionIds){
				arguments.add("regions[]="+URLEncoder.encode(regionId, "utf-8"));
			}
			for(String line : lines){
				arguments.add("lines[]="+URLEncoder.encode(line, "utf-8"));
			}
			arguments.add("player="+player.getUniqueId().toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] args = new String[arguments.size()];
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("bank/sign.php", arguments.toArray(args));
		if(yamlConfiguration==null) return;
		if(yamlConfiguration.contains("sign")){
			ConfigurationSection linesSection = yamlConfiguration.getConfigurationSection("sign");
			for(String key : linesSection.getKeys(false)){
				ConfigurationSection lineSection = linesSection.getConfigurationSection(key);
				int lineIndex = lineSection.getInt("line");
				String line = lineSection.getString("text");
				if(lineIndex<0 || lineIndex>3){
					Bukkit.getLogger().info("Invalid line "+lineIndex+" with contents "+line);
					continue;
				}
				event.setLine(lineIndex, line);
			}
		}
		if(yamlConfiguration.contains("message")){
			player.sendMessage(yamlConfiguration.getString("message"));
		}
		if(yamlConfiguration.contains("action_bar")){
			player.sendActionBar(yamlConfiguration.getString("action_bar"));
		}
	}
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
        	return;
        }
    	if(event.getAction()==Action.LEFT_CLICK_BLOCK && event.getItem()!=null) return;
        Block block = event.getClickedBlock();
        if(!isBanksign(block))
        	return;
        SwissSMPler player = SwissSMPler.get(event.getPlayer());
        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        if(lines[2].toLowerCase().equals("info")){
    		displayAccountInfo(player);
        }
        else if(lines[2].matches("-?\\d+(\\.\\d+)?")){
    		if(!player.hasPermission("craftbank.use")){
    			player.sendActionBar(ChatColor.RED+"Keine Berechtigung.");
    			return;
    		}
        	if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
        		deposit(player, lines);
        	}
        	else if(event.getAction() == Action.LEFT_CLICK_BLOCK){	
        		withdraw(player, lines);
        	}
        }
    }
	private void displayAccountInfo(SwissSMPler player){
        transfer(player, 0);
	}
	private void deposit(SwissSMPler player, String[] lines){
		Integer depositAmount = Math.max(0, Math.min(Integer.parseInt(lines[2]), ExperienceManager.getExperience(player)));
		transfer(player, depositAmount);
	}
	private void withdraw(SwissSMPler player, String[] lines){
		Integer widthdrawAmount = Math.max(0, Integer.parseInt(lines[2]));
		transfer(player, -widthdrawAmount);
	}
	private void transfer(SwissSMPler player, int transfer){
		YamlConfiguration userAccount = getUserAccount(player, transfer);
		if(userAccount==null) return;
		ConfigurationSection accountSection = userAccount.getConfigurationSection("account");
		if(accountSection==null){
			player.sendActionBar(ChatColor.RED+"Zugriff momentan nicht möglich.");
			return;
		}
		int transferAmount = accountSection.getInt("transfer");
		int new_balance = accountSection.getInt("new_balance");
		int old_balance = accountSection.getInt("old_balance");
		int changed_balance = new_balance-old_balance-transferAmount;
		String transferString;
		if(transferAmount>0){
			//deposit
			Integer oldExp = ExperienceManager.getExperience(player);
			player.setLevel(0);
			player.setExp(0);
			int newExp = oldExp-transferAmount;
			player.giveExp((int)Math.round(newExp*0.989));
			transferString = ChatColor.GREEN+"+"+transferAmount+ChatColor.RESET+" ";
		}
		else if(transferAmount<0){
			//widthdraw
			player.giveExp((int)Math.round(Math.abs(transferAmount)*0.989));
			transferString = ChatColor.RED+""+transferAmount+ChatColor.RESET+" ";
		}
		else{
			//display
			transferString = "";
		}
		String changedString;
		if(changed_balance>0){
			changedString = " ("+ChatColor.GREEN+"+"+changed_balance+ChatColor.RESET+" Zinsen)";
		}
		else{
			changedString = "";
		}
		player.sendActionBar(transferString+"Kontostand: "+new_balance+changedString);
		
	}
	public YamlConfiguration getUserAccount(SwissSMPler player, int transfer){
		if(player==null) return null;
		return DataSource.getYamlResponse("bank/edit_account.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"transfer="+transfer
		});
	}
	public static boolean isBanksign(Block block){
        Material material = block.getType();
        if(material != Material.WALL_SIGN && material != Material.SIGN_POST) {
        	return false;
        }
        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        if(!lines[0].toLowerCase().contains("[bank]")) {
            return false;
    	}
        return true;
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
