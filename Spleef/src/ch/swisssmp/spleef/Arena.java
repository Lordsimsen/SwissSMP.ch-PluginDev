package ch.swisssmp.spleef;

import ch.swisssmp.schematics.SchematicUtil;
import ch.swisssmp.utils.*;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

import ch.swisssmp.world.WorldManager;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class Arena implements Listener{
    
    protected final static HashMap<Integer,Arena> arenas = new HashMap<Integer,Arena>();
    private static BukkitTask countDownTask = null;
    private static long countDownStart = 0;
    private static Random random = new Random();
    
    //Arena Properties
    protected final int arenaId;
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
    protected final Block schematicLocation;
    
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
    private boolean isPreparationPhase = false;
    
    private Scoreboard scoreboard;
    private Objective objective;
    
    public Arena(ConfigurationSection dataSection)
    {
    	this.breakableMaterials.add(Material.GLOWSTONE);
    	this.breakableMaterials.add(Material.GREEN_GLAZED_TERRACOTTA);
    	this.breakableMaterials.add(Material.GREEN_TERRACOTTA);
    	this.breakableMaterials.add(Material.BROWN_GLAZED_TERRACOTTA);
    	this.breakableMaterials.add(Material.TERRACOTTA);
    	this.breakableMaterials.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
    	this.breakableMaterials.add(Material.CYAN_CONCRETE);
    	
        this.arenaId = dataSection.getInt("id");
        this.name = dataSection.getString("name");
        this.minPlayers = dataSection.getInt("min_players");
        this.schematicName = dataSection.getString("schematic");
        
        ConfigurationSection pointsSection = dataSection.getConfigurationSection("points");
        this.joinLocation = pointsSection.getLocation("join");
        this.leaveLocation = pointsSection.getLocation("leave");
        this.spectateLocation = pointsSection.getLocation("spectator");
        this.schematicLocation = pointsSection.getLocation("schematic").getBlock();
        
        ConfigurationSection regionsSection = dataSection.getConfigurationSection("regions");
        this.enterRegionName = regionsSection.getString("enter");
        this.leaveRegionName = regionsSection.getString("leave");
        
        this.preparationTime = dataSection.getInt("preparation_time");
        
        ConfigurationSection messagesSection = dataSection.getConfigurationSection("messages");
        this.instructions = messagesSection.getStringList("instructions");
        this.deathMessages = messagesSection.getStringList("death");
        this.winMessages = messagesSection.getStringList("win");
    }
    
    private void initialize() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("score", "dummy", "Punkte", RenderType.INTEGER);
        
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        
        Bukkit.getPluginManager().registerEvents(this, Spleef.getInstance());
        arenas.put(this.arenaId, this);
    }

    public World getWorld(){
        return schematicLocation!=null ? schematicLocation.getWorld() : null;
    }

    public int getId(){
        return arenaId;
    }
    
	@EventHandler(ignoreCancelled=true)
    private void onPlayerInteract(PlayerInteractEvent event){
        EquipmentSlot hand = event.getHand();
        
        if(hand != EquipmentSlot.HAND)
            return;
        
        Player player = event.getPlayer();
        
        if(!player_uuids.contains(player.getUniqueId()))
            return;
        
        Action action = event.getAction();
        
        if(action != Action.LEFT_CLICK_BLOCK)
        {
        	event.setCancelled(true);
        	return;
        }
        
        Block block = event.getClickedBlock();
        Material material = block.getType();
        
        if(!breakableMaterials.contains(material) || !alive_uuids.contains(player.getUniqueId()) || !isRunning)
        {
            event.setCancelled(true);
            return;
        }
        
        block.setType(Material.AIR);
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, material);
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
    
    @EventHandler
    private void onPlayerLogout(PlayerQuitEvent event)
    {
    	Player player = event.getPlayer();
    	if(this.player_uuids.contains(player.getUniqueId())){
        	leave(SwissSMPler.get(player));
    	}
    }
    
    protected  void join(SwissSMPler player)
    {
        if(player==null) return;
        if(isRunning)
        {
            player.sendActionBar(ChatColor.YELLOW + "Partie läuft gerade. Warte bis nächste Runde.");
            return;
        }
        
        if(player.getGameMode()!=GameMode.SURVIVAL) return;
        if(!player.hasPermission("spleef.play")) return;
        Vector randomPosition = random.insideUnitSphere().multiply(5);
        randomPosition.setY(0);
        player.setInvulnerable(true);
        player.teleport(joinLocation.clone().add(randomPosition));
        player.sendActionBar(ChatColor.YELLOW + name + " betreten.");
        Score score = objective.getScore(player.getName());
        if(!score.isScoreSet()) {
        	score.setScore(0);
        }
        Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
        bukkitPlayer.setScoreboard(scoreboard);
        
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
            	isPreparationPhase = true;
                countDownTask = Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                    	isPreparationPhase = false;
                        countDownTask = null;
                        prepareGame();
                    }
                }, preparationTime*20);  
                countDownStart = System.currentTimeMillis();
            }
            
            if(isPreparationPhase){
                int remainingTime = preparationTime-(int)((System.currentTimeMillis()-countDownStart)/1000);
                
                for(UUID uuid : player_uuids)
                {
                    tempPlayer = SwissSMPler.get(uuid);
                    if(tempPlayer == null)
                        continue;
                    
                    tempPlayer.sendActionBar(ChatColor.GREEN + String.valueOf(player_uuids.size()) + ChatColor.YELLOW + " Spieler, starte in " + remainingTime + " Sekunden...");
                }
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
        Debug.Log("Reporting joined players");
        for(UUID uuid : player_uuids){
        	try{
        		Debug.Log(Bukkit.getPlayer(uuid).getName());
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
            
            if(alive_uuids.size() <= 1)
            {
            	if(alive_uuids.size()==1){
                    SwissSMPler winner = SwissSMPler.get(alive_uuids.get(0));
                    win(winner);
            	}
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
            Vector randomPosition = random.insideUnitSphere().multiply(5);
            randomPosition.setY(0);
            player.teleport(joinLocation.clone().add(randomPosition));
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
        
        countDownTask = Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), new Runnable() {
            @Override
            public void run() {
                countDownTask = null;
                countDown(i-1);
            }
        }, 20l);
    }
    
    protected void startGame()
    {
    	isRunning = true;
    	resetField();
    	
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
        resetField();
        
        if(countDownTask != null)
            return;
        this.reportPlayers();
        
        Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), new Runnable() {
            @Override
            public void run() {
                prepareGame();
            }
        }, this.preparationTime*20);
    }
    
    protected void win(SwissSMPler smpler)
    {
        if(smpler == null)
            return;
        
        Player player = Bukkit.getPlayer(smpler.getUniqueId());
        if(player!=null) {
        	Score score = objective.getScore(player.getName());
        	score.setScore(score.getScore()+1);
        }
        
        for(UUID uuid : player_uuids)
        {
            if(uuid.equals(smpler.getUniqueId()))
            {
            	smpler.sendTitle("SIEG", ChatColor.GREEN + winMessages.get(random.nextInt(winMessages.size())));
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
    	// Bukkit.getLogger().info("Reset Field! "+schematicName+", "+schematicLocation.getX()+", "+schematicLocation.getY()+", "+schematicLocation.getZ()+" in "+schematicLocation.getWorld().getName());
        File file = getSchematicFile();
        boolean result = SchematicUtil.paste(file, schematicLocation);
        if(!result) {
        	Bukkit.getLogger().info("Reset failed!");
        }
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
    
    protected void resetScores() {
    	for(String entry : scoreboard.getEntries()) {
    		scoreboard.resetScores(entry);
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
        
        HTTPRequest request = DataSource.getResponse(Spleef.getInstance(), "arenas.php");
        
        request.onFinish(()->{
        	YamlConfiguration yamlConfiguration = request.getYamlResponse();
    		loadArenas(yamlConfiguration);
        });
    }
    
    private static void loadArenas(YamlConfiguration yamlConfiguration){
    	if(yamlConfiguration==null) {
    		Bukkit.getLogger().info("[Spleef] Arenen konnten nicht geladen werden.");
    		return;
    	}
        for(String key : yamlConfiguration.getKeys(false))
        {
            Arena arena = new Arena(yamlConfiguration.getConfigurationSection(key));
            arena.initialize();
        }
    }
    
    protected static Arena get(int arena_id)
    {
        return arenas.get(arena_id);
    }

    protected File getSchematicFile(){
        World world = joinLocation!=null ? joinLocation.getWorld() : null;
        if(world==null) return null;
        return new File(WorldManager.getPluginDirectory(Spleef.getInstance(), world), "schematics/arena_" + arenaId+".schematic");
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
