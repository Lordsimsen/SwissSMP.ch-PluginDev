package ch.swisssmp.spleef;

import ch.swisssmp.spleef.Spleef;
import ch.swisssmp.webcore.DataSource;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

public class Arena implements Listener{
    
    private final static HashMap<Integer,Arena> arenas = new HashMap<Integer,Arena>();
    private static BukkitTask countDownTask = null;
    private static long countDownStart = 0;
    private static Random random = new Random();
    
    //Arena Properties
    protected final int arena_id;
    protected final String name;
    protected final int minPlayers;
    protected final String schematicName;
    
    //Portals
    protected final String enterRegionName;
    protected final String leaveRegionName;
    
    //Times
    protected final int preparationTime;
    
    //Locations
    protected final Location joinLocation;
    protected final Location leaveLocation;
    protected final Location spectateLocation;
    protected final Location schematicLocation;
    
    //Player lists
    protected final ArrayList<UUID> player_uuids = new ArrayList<UUID>();
    protected final ArrayList<UUID> alive_uuids = new ArrayList<UUID>();
    
    //Other
    protected final ArrayList<Material> breakableMaterials = new ArrayList<Material>();
    protected final List<String> instructions;
    protected final List<String> deathMessages;
    protected final List<String> winMessages;
    
    //Ram safers        Initialize once for the use in for loops.
    protected SwissSMPler tempPlayer;
    
    protected boolean isRunning = false;
    
    public Arena(ConfigurationSection dataSection)
    {
    	this.breakableMaterials.add(Material.SOUL_SAND);
    	this.breakableMaterials.add(Material.PACKED_ICE);
    	this.breakableMaterials.add(Material.OBSIDIAN);
    	
        this.arena_id = dataSection.getInt("id");
        this.name = dataSection.getString("name");
        this.minPlayers = dataSection.getInt("min_players");
        this.schematicName = dataSection.getString("schematic");
        
        ConfigurationSection pointsSection = dataSection.getConfigurationSection("points");
        this.joinLocation = getLocationFromYaml(pointsSection.getConfigurationSection("join"));
        this.leaveLocation = getLocationFromYaml(pointsSection.getConfigurationSection("leave"));
        this.spectateLocation = getLocationFromYaml(pointsSection.getConfigurationSection("spectator"));
        this.schematicLocation = getLocationFromYaml(pointsSection.getConfigurationSection("schematic"));
        
        ConfigurationSection regionsSection = dataSection.getConfigurationSection("regions");
        this.enterRegionName = regionsSection.getString("enter");
        this.leaveRegionName = regionsSection.getString("leave");
        
        this.preparationTime = dataSection.getInt("preparation_time");
        
        ConfigurationSection messagesSection = dataSection.getConfigurationSection("messages");
        this.instructions = messagesSection.getStringList("instructions");
        this.deathMessages = messagesSection.getStringList("death");
        this.winMessages = messagesSection.getStringList("win");
        
        Bukkit.getPluginManager().registerEvents(this, Spleef.plugin);
        arenas.put(this.arena_id, this);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
    private void onPlayerInteract(PlayerInteractEvent event){
        EquipmentSlot hand = event.getHand();
        
        if(hand != EquipmentSlot.HAND)
            return;
        
        Action action = event.getAction();
        
        if(action != Action.LEFT_CLICK_BLOCK)
            return;
        
        Player player = event.getPlayer();
        
        if(!player_uuids.contains(player.getUniqueId()))
            return;
        
        Block block = event.getClickedBlock();
        Material material = block.getType();
        
        if(!breakableMaterials.contains(material) || !alive_uuids.contains(player.getUniqueId()) || isRunning)
        {
            event.setCancelled(true);
            return;
        }
        
        block.setType(Material.AIR);
        block.getWorld().playEffect(block.getLocation(), Effect.TILE_BREAK, material.getId());
    }
    
    @EventHandler(ignoreCancelled = true)
    private  void onRegionEnter(RegionEnterEvent event)
    {
        ProtectedRegion region = event.getRegion();
        SwissSMPler player = SwissSMPler.get(event.getPlayer());
        
        if(region.getId().startsWith(this.enterRegionName))
        {
            this.join(player);
        }
        
        else if(region.getId().startsWith(this.leaveRegionName))
        {   
            this.leave(player);
        }
    }
    
    protected  void join(SwissSMPler player)
    {
        if(player==null) return;
        if(isRunning)
        {
            player.sendActionBar(ChatColor.YELLOW + "Partie im Gange. Warte bis nächste Runde.");
            return;
        }
        
        if(player.getGameMode()!=GameMode.SURVIVAL) return;
        if(!player.hasPermission("spleef.play")) return;
        player.setInvulnerable(true);
        player.teleport(joinLocation);
        player.sendActionBar(ChatColor.YELLOW + name + " betreten.");
        for(String line : instructions)
        {
            player.sendMessage(ChatColor.RESET+"["+ChatColor.YELLOW+"Spleef"+ChatColor.RESET+"] "+line);
        }

        this.player_uuids.add(player.getUniqueId());
        this.reportPlayers();
        
        if(canStart())
        {
            if(countDownTask == null) 
            {
                countDownTask = Bukkit.getScheduler().runTaskLater(Spleef.plugin, new Runnable() {
                    @Override
                    public void run() {
                        countDownTask = null;
                        prepareGame();
                    }
                }, preparationTime*20);
                countDownStart = System.currentTimeMillis();
            }
            int remainingTime = preparationTime-(int)((countDownStart-System.currentTimeMillis())/1000);
            
            for(UUID uuid : player_uuids)
            {
                tempPlayer = SwissSMPler.get(uuid);
                if(tempPlayer == null)
                    continue;
                
                tempPlayer.sendActionBar(ChatColor.GREEN + String.valueOf(player_uuids.size()) + ChatColor.YELLOW + " Spieler, starte in " + remainingTime + " Sekunden...");
            }
                    
        }
        
        else
        {
            for(UUID uuid : player_uuids)
            {
                tempPlayer = SwissSMPler.get(uuid);
                if(tempPlayer == null)
                    continue;
                
                tempPlayer.sendActionBar(ChatColor.RED + String.valueOf(player_uuids.size()) + ChatColor.YELLOW + " Spieler, mindestens "+ minPlayers + " für Start.");
            }
        }
    }
    
    private void reportPlayers(){
        Spleef.debug("Reporting joined players");
        for(UUID uuid : player_uuids){
        	try{
        		Spleef.info(Bukkit.getPlayer(uuid).getName());
        	}
        	catch(Exception e){}
        }
    }
    
    protected  void leave(SwissSMPler player)
    {
        if(player==null) return;
        if(alive_uuids.contains(player.getUniqueId()))
        {
            this.alive_uuids.remove(player.getUniqueId());
            player.teleport(spectateLocation);
            player.sendActionBar(ChatColor.RED + deathMessages.get(random.nextInt(deathMessages.size())));
            
            if(alive_uuids.size() == 1)
            {
                SwissSMPler winner = SwissSMPler.get(alive_uuids.get(0));
                win(winner);
                endGame();
            }
                        
            return;
        }

        player.setInvulnerable(false);
        this.player_uuids.remove(player.getUniqueId());
        player.teleport(leaveLocation);
        player.sendActionBar(ChatColor.YELLOW + name + " verlassen.");
        this.reportPlayers();
    }
    
    protected  void prepareGame()
    {
        if(!canStart() || countDownTask!=null)
            return;
        resetField();
        for(UUID player_uuid : player_uuids)
        {
            SwissSMPler player = SwissSMPler.get(player_uuid);
            if(player == null)
                continue;
            
            player.teleport(joinLocation);
            player.sendActionBar(ChatColor.YELLOW + "Neuer Partie beigetreten.");
        }
        
        countDown(5);
    }
    
    private void countDown(int i)
    {
        
        if(i == 0)
        {
            startGame();
            return;
        }
        
        for(UUID player_uuid : player_uuids)
        {
            SwissSMPler player = SwissSMPler.get(player_uuid);
            if(player == null)
                continue;
            
            player.sendTitle(String.valueOf(i), "");
        }
        
        countDownTask = Bukkit.getScheduler().runTaskLater(Spleef.plugin, new Runnable() {
            @Override
            public void run() {
                countDownTask = null;
                countDown(i-1);
            }
        }, 20l);
    }
    
    protected void startGame()
    {
        alive_uuids.clear();
        for(UUID uuid : player_uuids)
        {
            alive_uuids.add(uuid);
            SwissSMPler.get(uuid).sendTitle("Start!", name);
        }
    }
    
    protected void endGame()
    {
        isRunning = false;
        if(countDownTask != null)
            return;
        this.reportPlayers();
        
        Bukkit.getScheduler().runTaskLater(Spleef.plugin, new Runnable() {
            @Override
            public void run() {
                prepareGame();
            }
        }, this.preparationTime*20);
    }
    
    protected void win(SwissSMPler player)
    {
        if(player == null)
            return;
        
        for(UUID uuid : player_uuids)
        {
            if(uuid.equals(player.getUniqueId()))
            {
                player.sendTitle("SIEG", ChatColor.GREEN + winMessages.get(random.nextInt(winMessages.size())));
                continue;
            }
            
            tempPlayer = SwissSMPler.get(uuid);
            tempPlayer.sendTitle("VERLOREN", player.getDisplayName() + ChatColor.RED + " hat gewonnen!");
        }
    }
    
    private  boolean canStart()
    {
        return player_uuids.size() >= minPlayers;
    }
    
    private void resetField()
    {
        
    }
    
    protected void resetGame()          //TODO: Bei command resetten
    {
        isRunning = false;
        alive_uuids.clear();
        
        while(player_uuids.size() > 0)
        {
            leave(SwissSMPler.get(player_uuids.get(0)));
        }
    }
    
    // static stuff
    
    protected  static void loadArenas()
    {
        for(Arena arena : arenas.values())
        {
            HandlerList.unregisterAll(arena);
            arena.resetGame();
            
        }
        arenas.clear();
        
        YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("spleef/arenas.php");
        
        if(yamlConfiguration == null)
            return;
        
        for(String key : yamlConfiguration.getKeys(false))
        {
            new Arena(yamlConfiguration.getConfigurationSection(key));
        }
    }
    
    protected static Arena get(int arena_id)
    {
        return arenas.get(arena_id);
    }
    
    
    protected static Location getLocationFromYaml(ConfigurationSection dataSection)
    {
        World world = Bukkit.getWorld(dataSection.getString("world"));
        
        if(world == null)
            return null;
        
        int x = dataSection.getInt("x");
        int y = dataSection.getInt("y");
        int z = dataSection.getInt("z");
        return world.getBlockAt(x, y, z).getLocation();
    }
}
