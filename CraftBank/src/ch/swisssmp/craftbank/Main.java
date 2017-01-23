package ch.swisssmp.craftbank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.craftbank.PlayerCommand;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static YamlConfiguration config;
	public static File configFile;
	public static PluginDescriptionFile pdfFile;
	public static String bankSignTitle = ChatColor.DARK_GRAY+"[Bank]";
	public static File dataFolder;
	private static String rootURL;
	private static String pluginToken;
	private static Random random = new Random();
	protected static boolean debug = false;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		this.getCommand("CraftBank").setExecutor(new PlayerCommand());
		
		configFile = new File(getDataFolder(), "config.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		loadYamls();
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		String[] lines = event.getLines();
		if(!lines[0].toLowerCase().equals("[bank]"))
			return;
		createBankSign(event);
	}
	public void createBankSign(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if(!lines[2].isEmpty() && !NumberUtils.isNumber(lines[2]) && !lines[2].toLowerCase().equals("info")){
			player.sendMessage(ChatColor.RED+"Bitte auf der dritten Zeile eine Zahl oder 'Info' eingeben.");
			event.setCancelled(true);
			return;
		}
		int transferAmount = Integer.parseInt(lines[2]);
		lines[2] = String.valueOf(Math.min(Math.abs(transferAmount), 1000));
		switch(lines[1]){
		case "Schalter":
			if(!player.hasPermission("craftbank.filiale")){
				player.sendMessage(ChatColor.RED+"Nur Angestellte der Bank können Bankschalter erstellen.");
				event.setCancelled(true);
				return;
			}
			event.setLine(1, "Schalter");
			player.sendMessage(ChatColor.YELLOW+"Erfolg! Der Bankschalter kann jetzt verwendet werden.");
			break;
		case "Automat":
			if(!player.hasPermission("craftbank.region")){
				player.sendMessage(ChatColor.RED+"Nur Angestellte der Bank können Bankautomaten erstellen.");
				event.setCancelled(true);
				return;
			}
			if(lines[2].toLowerCase().equals("info")){
				player.sendMessage(ChatColor.RED+"Nur Bankschalter können den Kontostand abrufen.");
				event.setCancelled(true);
				break;
			}
			event.setLine(1, "Automat");
			sendActionBar(player, ChatColor.YELLOW+"Automat aktiviert!");
			break;
		}
		event.setLine(3, ChatColor.GREEN+"Online");
		event.setLine(0, bankSignTitle);
	}
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
        	return;
        }
        Block block = event.getClickedBlock();
        if(!isBanksign(block))
        	return;
		Player player = event.getPlayer();
        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        if(lines[2].toLowerCase().equals("info")){	
    		displayAccountInfo(player);
        }
        else if(NumberUtils.isNumber(lines[2])){
    		if(!player.hasPermission("craftbank.use")){
    			sendActionBar(player, ChatColor.RED+"Keine Berechtigung.");
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
	private void displayAccountInfo(Player player){
        transfer(player, 0);
	}
	private void deposit(Player player, String[] lines){
		Integer depositAmount = Math.max(0, Math.min(Integer.parseInt(lines[2]), ExperienceManager.getExperience(player)));
		transfer(player, depositAmount);
	}
	private void withdraw(Player player, String[] lines){
		Integer widthdrawAmount = Math.max(0, Integer.parseInt(lines[2]));
		transfer(player, -widthdrawAmount);
	}
	private void transfer(Player player, int transfer){
		YamlConfiguration userAccount = getUserAccount(player, transfer);
		if(userAccount==null) return;
		ConfigurationSection accountSection = userAccount.getConfigurationSection("account");
		if(accountSection==null){
			sendActionBar(player, ChatColor.RED+"Zugriff momentan nicht möglich.");
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
		sendActionBar(player, transferString+"Kontostand: "+new_balance+changedString);
		
	}
	public YamlConfiguration getUserAccount(Player player, int transfer){
		if(player==null) return null;
		return getYamlResponse("bank/edit_account.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"transfer="+transfer
		});
	}
	public static long getDateDiff(Date oldDate, Date newDate, TimeUnit timeUnit) {
	    long diffInMillies = newDate.getTime() - oldDate.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	public static boolean saveUserAccount(YamlConfiguration userAccount, File userFile){
		try {
			Date now = new Date();
			userAccount.set("lastView", now.getTime());
			userAccount.save(userFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean isBanksign(Block block){
        Material material = block.getType();
        if(material != Material.WALL_SIGN && material != Material.SIGN_POST) {
        	return false;
        }
        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        if(!lines[0].equals(bankSignTitle)) {
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
    private void firstRun() throws Exception {
        if(!configFile.exists()){
        	configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }
    }
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadYamls() {
        try {
        	config.load(configFile);
    		rootURL = config.getString("webserver");
    		if(!rootURL.endsWith("/")) rootURL+="/";
    		pluginToken = config.getString("token");
    		debug = config.getBoolean("debug");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static void sendActionBar(Player player, String message){
    	if(player==null || message==null) return;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) craftPlayer).getHandle().playerConnection.sendPacket(ppoc);
    }
	public static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	public static String getResponse(String relativeURL, String[] params){
		String resultString = "";
		try{
			String urlString = rootURL+relativeURL+"?token="+pluginToken+"&random="+random.nextInt(1000);
			if(params!=null && params.length>0){
				urlString+="&"+String.join("&", params);
			}
			if(debug){
				Bukkit.getLogger().info(urlString);
			}
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String tempString = "";
			while(null!=(tempString = br.readLine())){
				resultString+= tempString;
			}
			if(resultString.isEmpty()){
				return "";
			}
			return resultString;
		}
		catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL){
		return getYamlResponse(relativeURL, null);
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL, String[] params){
		String resultString = convertWebYamlString(getResponse(relativeURL, params));
		if(resultString.isEmpty()){
			return new YamlConfiguration();
		}
		try{
			YamlConfiguration yamlConfiguration = new YamlConfiguration();
			yamlConfiguration.loadFromString(resultString);
			return yamlConfiguration;
		}
		catch(Exception e){
			e.printStackTrace();
			return new YamlConfiguration();
		}
	}
    private static String convertWebYamlString(String webYamlString){
    	webYamlString = webYamlString.replace("<br>", "\r\n");
    	webYamlString = webYamlString.replace("&nbsp;", " ");
    	return webYamlString;
    }
}
