package ch.swisssmp.craftelytra;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ElytraGate implements Listener{
	protected static int next_id = 0;
	protected final int gate_id;
	protected final String owner;
	
	protected final Block sign;
	protected final Block lever;
	protected final Block pivot;
	protected final BlockFace direction;
	protected boolean powered = false;
	protected final int elevation;
	protected ProtectedRegion bottomRegion;
	protected ProtectedRegion topRegion = null;
	
	private Random random = new Random();
	
	protected ElytraGate(World world, Block sign, Block pivot, BlockFace direction, int elevation, Player owner){
		this.gate_id = ElytraGate.next_id;
		this.owner = owner.getName();
		ElytraGate.next_id++;
		this.sign = sign;
		this.pivot = pivot;
		this.lever = pivot.getRelative(BlockFace.UP);
		this.direction = direction;
		this.elevation = elevation;
		this.bottomRegion = createBottomRegion();
		this.topRegion = null;
		
		CraftElytra.gatesMap.put(this.lever, this);
		CraftElytra.gatesMap.put(this.sign, this);
		CraftElytra.gates.put(this,world);
		CraftElytra.saveGates(this.pivot.getWorld());
		Bukkit.getPluginManager().registerEvents(this, CraftElytra.plugin);
	}
	protected ElytraGate(World world, ConfigurationSection dataSection){
		this.gate_id = dataSection.getInt("gate_id");
		this.owner = dataSection.getString("owner");
		ElytraGate.next_id = Math.max(this.gate_id+1, ElytraGate.next_id);
		this.sign = loadLocation(world, dataSection.getConfigurationSection("sign")).getBlock();
		this.lever = loadLocation(world, dataSection.getConfigurationSection("lever")).getBlock();
		this.pivot = loadLocation(world, dataSection.getConfigurationSection("pivot")).getBlock();
		this.direction = BlockFace.valueOf(dataSection.getString("direction"));
		this.elevation = dataSection.getInt("elevation");
		this.powered = (dataSection.getInt("powered")==1);
		WorldGuardPlugin worldGuard = CraftElytra.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(world);
		this.bottomRegion = regionManager.getRegion(this.getBottomRegionName());
		if(this.powered){
			this.topRegion = regionManager.getRegion(this.getTopRegionName());
		}
		
		if(CraftElytra.gatesMap.containsKey(this.lever)){
			return;
		}
		CraftElytra.gatesMap.put(this.lever, this);
		CraftElytra.gatesMap.put(this.sign, this);
		CraftElytra.gates.put(this,world);
		
		this.updateSign();
		
		Bukkit.getPluginManager().registerEvents(this, CraftElytra.plugin);
		if(!validateConstruction(Bukkit.getConsoleSender())){
			this.delete();
		}
	}
	protected void save(ConfigurationSection dataSection){
		dataSection.set("gate_id", this.gate_id);
		dataSection.set("owner", this.owner);
		saveLocation(dataSection, sign.getLocation(), "sign");
		saveLocation(dataSection, lever.getLocation(), "lever");
		saveLocation(dataSection, pivot.getLocation(), "pivot");
		dataSection.set("direction", direction.toString());
		dataSection.set("elevation", elevation);
		
		if(powered)
			dataSection.set("powered", 1);
		else
			dataSection.set("powered", 0);
	}
	protected static void saveLocation(ConfigurationSection dataSection, Location location, String name){
		ConfigurationSection locationSection = dataSection.createSection(name);
		locationSection.set("x", (int)location.getX());
		locationSection.set("y", (int)location.getY());
		locationSection.set("z", (int)location.getZ());
	}
	@SuppressWarnings("deprecation")
	protected void elevate(boolean up){
		if(this.powered==up) return;
    	int yOffset = elevation;
    	if(!up) {
    		yOffset *= -1;
    	}
    	World world = this.pivot.getWorld();
    	BlockVector gateMin = this.getGateMin();
    	BlockVector gateMax = this.getGateMax();
        int size_x = (int)gateMax.getX()-(int)gateMin.getX()+1;
        int size_y = (int)gateMax.getY()-(int)gateMin.getY()+1;
        int size_z = (int)gateMax.getZ()-(int)gateMin.getZ()+1;
        Block fromBlock;
        Block toBlock;
        for(int y = 0; y < size_y; y++){
        	for(int z = 0; z < size_z; z++){
        		for(int x = 0; x < size_x; x++){
        			fromBlock = world.getBlockAt((int)gateMin.getX()+x, (int)gateMin.getY()+y, (int)gateMin.getZ()+z);
        			toBlock = world.getBlockAt((int)gateMin.getX()+x, (int)gateMin.getY()+y+yOffset, (int)gateMin.getZ()+z);
        			toBlock.setTypeIdAndData(fromBlock.getTypeId(), fromBlock.getState().getData().getData(), true);
        			fromBlock.setType(Material.AIR);
        		}
        	}
        }
        this.powered = up;
        if(this.powered){
        	this.pivot.getWorld().playSound(this.pivot.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
        	this.topRegion = this.createTopRegion();
        }
        else{
        	this.pivot.getWorld().playSound(this.pivot.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
        	this.deleteTopRegion();
        }
		this.updateSign();
		CraftElytra.saveGates(this.pivot.getWorld());
	}
	private void updateSign(){
    	if(!(this.sign.getState() instanceof Sign)){
    		this.delete();
    		return;
    	}
		Sign sign = (Sign)this.sign.getState();
    	sign.setLine(0, ChatColor.DARK_PURPLE+"Elytra Gate");
    	String state = ChatColor.RED+"DEAKTIVIERT";
    	if(this.powered) state = ChatColor.GREEN+"AKTIVIERT";
    	sign.setLine(1, state);
    	sign.setLine(2, String.valueOf(this.elevation));
    	sign.setLine(3, this.owner);
    	sign.update();
	}
	@EventHandler(ignoreCancelled=true)
	private void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(block.getLocation().equals(this.sign.getLocation())){
			event.getPlayer().sendMessage(ChatColor.RED+"Elytra Gate zerstört.");
			this.delete();
			return;
		}
		if(this.pivot.getWorld()!=block.getWorld()) return;
		if(this.bottomRegion==null){
			this.bottomRegion = this.createBottomRegion();
			if(this.bottomRegion==null){
				Bukkit.getLogger().info("Couldn't create bottomRegion for elytra gate "+this.gate_id+"!");
				return;
			}
		}
		if(this.bottomRegion.contains(block.getX(), block.getY(), block.getZ())){
			if(!validateConstruction(event.getPlayer())){
				event.getPlayer().sendMessage(ChatColor.RED+"Elytra Gate zerstört.");
				this.delete();
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		if(this.pivot.getWorld()!=block.getWorld()) return;
		if(this.bottomRegion==null){
			this.bottomRegion = this.createBottomRegion();
			if(this.bottomRegion==null){
				Bukkit.getLogger().info("Couldn't create bottomRegion for elytra gate "+this.gate_id+"!");
				return;
			}
		}
		if(this.bottomRegion.contains(block.getX(), block.getY(), block.getZ())){
			if(!validateConstruction(event.getPlayer())){
				event.getPlayer().sendMessage(ChatColor.RED+"Elytra Gate zerstört.");
				this.delete();
			}
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		Player player = event.getPlayer();
		if(!player.hasPermission("craftelytra.elytragate.use")) return;
		if(player.getWorld()!=this.pivot.getWorld()) return;
		ProtectedRegion region = event.getRegion();
		if(region==null || this.topRegion==null) return;
		if(region.getId().equals(this.topRegion.getId())){
			Bukkit.getScheduler().runTaskLater(CraftElytra.plugin, new Runnable(){
				public void run(){
		        	player.playSound(player.getLocation(), "14", 500, 0.95f+random.nextFloat()*0.1f);
					player.setVelocity(player.getVelocity().normalize().multiply(10).setY(0));
				}
			}, 3L);
		}
	}
	protected String getBottomRegionName(){
		return "elytra_gate_"+this.gate_id+"_bottom";
	}
	protected String getTopRegionName(){
		return "elytra_gate_"+this.gate_id+"_top";
	}
	protected ProtectedCuboidRegion createBottomRegion(){
		WorldGuardPlugin worldGuard = CraftElytra.worldGuardPlugin;
		if(worldGuard==null) throw new NullPointerException("WorldGuard not found!");
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		ProtectedCuboidRegion result = new ProtectedCuboidRegion(this.getBottomRegionName(), this.getBottomMin(), this.getBottomMax());
		result.setFlag(DefaultFlag.CREEPER_EXPLOSION, State.DENY);
		result.setFlag(DefaultFlag.OTHER_EXPLOSION, State.DENY);
		result.setFlag(DefaultFlag.PASSTHROUGH, State.ALLOW);
		regionManager.addRegion(result);
		worldGuard.saveConfig();
		return result;
	}
	protected void deleteBottomRegion(){
		WorldGuardPlugin worldGuard = CraftElytra.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		regionManager.removeRegion(this.getBottomRegionName());
		worldGuard.saveConfig();
	}
	protected ProtectedCuboidRegion createTopRegion(){
		WorldGuardPlugin worldGuard = CraftElytra.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		ProtectedCuboidRegion result = new ProtectedCuboidRegion(this.getTopRegionName(), this.getGateMin(), this.getGateMax());
		result.setFlag(DefaultFlag.PASSTHROUGH, State.DENY);
		regionManager.addRegion(result);
		worldGuard.saveConfig();
		return result;
	}
	protected void deleteTopRegion(){
		WorldGuardPlugin worldGuard = CraftElytra.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		regionManager.removeRegion(this.getTopRegionName());
		worldGuard.saveConfig();
		this.topRegion = null;
	}
	protected BlockVector getBottomMin(){
		return new BlockVector(pivot.getX()-4, pivot.getY(), pivot.getZ()-4);
	}
	protected BlockVector getBottomMax(){
		return new BlockVector(pivot.getX()+4, pivot.getY()+7, pivot.getZ()+4);
	}
	protected BlockVector getGateMin(){
		int yOffset = 0;
		if(this.powered) yOffset = elevation;
		int x = pivot.getX();
		int y = pivot.getY()+3+yOffset;
		int z = pivot.getZ();
		int radius = 2;
		if(this.direction == BlockFace.NORTH || this.direction == BlockFace.SOUTH)
		{
			x-=radius;
			z-=1;
		}
		else{
			z-=radius;
			x-=1;
		}
		return new BlockVector(x, y, z);
	}
	protected BlockVector getGateMax(){
		int yOffset = 0;
		if(this.powered) yOffset = elevation;
		int x = pivot.getX();
		int y = pivot.getY()+7+yOffset;
		int z = pivot.getZ();
		int radius = 2;
		if(this.direction == BlockFace.NORTH || this.direction == BlockFace.SOUTH)
		{
			x+=radius;
			z+=1;
		}
		else{
			z+=radius;
			x+=1;
		}
		return new BlockVector(x, y, z);
	}
	protected void delete(){
		this.elevate(false);
		this.deleteBottomRegion();
		this.deleteTopRegion();
		CraftElytra.gatesMap.remove(this.lever);
		CraftElytra.gatesMap.remove(this.sign);
		CraftElytra.gates.remove(this);
		HandlerList.unregisterAll(this);
		if(this.sign.getType()!=Material.WALL_SIGN) return;
		Sign sign = (Sign)this.sign.getState();
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
		CraftElytra.saveGates(this.pivot.getWorld());
	}
	protected static Location loadLocation(World world, ConfigurationSection dataSection){
		int x = dataSection.getInt("x");
		int y = dataSection.getInt("y");
		int z = dataSection.getInt("z");
		return new Location(world, x, y, z);
	}
	protected boolean validateConstruction(CommandSender responsible){
		int topLimit = -1;
		if(this.powered){
			topLimit = 3;
		}
		return validateConstruction(this.pivot,this.direction, topLimit, responsible);
	}
    protected static boolean validateConstruction(Block centerBottom, BlockFace direction, int topLimit, CommandSender player){
    	if(centerBottom==null) return false;
    	Block northWest = new Location(centerBottom.getWorld(), centerBottom.getX()-4, centerBottom.getY(), centerBottom.getZ()-4).getBlock();
    	String schematicName;
    	switch(direction.getOppositeFace()){
    	case UP:
    		return false;
    	case DOWN:
    		return false;
    	case NORTH:
    		schematicName="template_n";
    		break;
    	case EAST:
    		schematicName="template_e";
    		break;
    	case SOUTH:
    		schematicName="template_s";
    		break;
    	case WEST:
    		schematicName="template_w";
    		break;
		default:
			return false;
    	}
    	return Schematic.compare(northWest.getLocation(), topLimit, schematicName, player);
    }
}
