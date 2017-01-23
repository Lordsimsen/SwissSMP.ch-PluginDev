package ch.swisssmp.craftelytra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
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
	
	protected final Block sign;
	protected final Block lever;
	protected final Block pivot;
	protected final BlockFace direction;
	protected boolean powered = false;
	protected final int elevation;
	protected ProtectedRegion bottomRegion;
	protected ProtectedRegion topRegion = null;
	
	protected ElytraGate(Block sign, Block pivot, BlockFace direction, int elevation){
		this.gate_id = ElytraGate.next_id;
		ElytraGate.next_id++;
		this.sign = sign;
		this.pivot = pivot;
		this.lever = pivot.getRelative(BlockFace.UP);
		this.direction = direction;
		this.elevation = elevation;
		this.bottomRegion = createBottomRegion();
		this.topRegion = null;
		
		Main.gatesMap.put(this.lever, this);
		Main.gatesMap.put(this.sign, this);
		Main.gates.add(this);
		Main.saveGates();
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}
	protected ElytraGate(ConfigurationSection dataSection){
		this.gate_id = dataSection.getInt("gate_id");
		ElytraGate.next_id = Math.max(this.gate_id+1, ElytraGate.next_id);
		this.sign = loadLocation(dataSection.getConfigurationSection("sign")).getBlock();
		this.lever = loadLocation(dataSection.getConfigurationSection("lever")).getBlock();
		this.pivot = loadLocation(dataSection.getConfigurationSection("pivot")).getBlock();
		this.direction = BlockFace.valueOf(dataSection.getString("direction"));
		this.elevation = dataSection.getInt("elevation");
		this.powered = (dataSection.getInt("powered")==1);
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		this.bottomRegion = regionManager.getRegion(this.getBottomRegionName());
		if(this.powered){
			this.topRegion = regionManager.getRegion(this.getTopRegionName());
		}
		
		Main.gatesMap.put(this.lever, this);
		Main.gatesMap.put(this.sign, this);
		Main.gates.add(this);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		int topLimit = -1;
		if(this.powered) topLimit = 3;
		if(!validateConstruction(this.pivot, this.direction, topLimit, null)){
			this.delete();
		}
	}
	protected void save(ConfigurationSection dataSection){
		dataSection.set("gate_id", this.gate_id);
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
		locationSection.set("world", location.getWorld().getName());
		locationSection.set("x", (int)location.getX());
		locationSection.set("y", (int)location.getY());
		locationSection.set("z", (int)location.getZ());
	}
	@SuppressWarnings("deprecation")
	protected void elevate(boolean up){
		if(this.powered==up) return;
    	Sign sign = (Sign)this.sign.getState();
    	sign.setLine(0, ChatColor.DARK_PURPLE+"Elytra Gate");
    	String state = ChatColor.RED+"DEAKTIVIERT";
    	if(up) state = ChatColor.GREEN+"AKTIVIERT";
    	sign.setLine(1, state);
    	sign.update();
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
        for(int y = 0; y < size_y; y++){
        	for(int z = 0; z < size_z; z++){
        		for(int x = 0; x < size_x; x++){
        			Block fromBlock = world.getBlockAt((int)gateMin.getX()+x, (int)gateMin.getY()+y, (int)gateMin.getZ()+z);
        			Block toBlock = world.getBlockAt((int)gateMin.getX()+x, (int)gateMin.getY()+y+yOffset, (int)gateMin.getZ()+z);
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
			int topLimit = -1;
			if(this.powered) topLimit = 3;
			if(!validateConstruction(this.pivot, this.direction, topLimit, event.getPlayer())){
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
			int topLimit = -1;
			if(!validateConstruction(this.pivot, this.direction, topLimit, event.getPlayer())){
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
			Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
				public void run(){
					player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(10).setY(0));
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
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
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
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		regionManager.removeRegion(this.getBottomRegionName());
		worldGuard.saveConfig();
	}
	protected ProtectedCuboidRegion createTopRegion(){
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
		RegionManager regionManager = worldGuard.getRegionManager(this.pivot.getWorld());
		ProtectedCuboidRegion result = new ProtectedCuboidRegion(this.getTopRegionName(), this.getGateMin(), this.getGateMax());
		result.setFlag(DefaultFlag.PASSTHROUGH, State.DENY);
		regionManager.addRegion(result);
		worldGuard.saveConfig();
		return result;
	}
	protected void deleteTopRegion(){
		WorldGuardPlugin worldGuard = Main.worldGuardPlugin;
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
		Main.gatesMap.remove(this.lever);
		Main.gatesMap.remove(this.sign);
		HandlerList.unregisterAll(this);
		if(this.sign.getType()!=Material.WALL_SIGN) return;
		Sign sign = (Sign)this.sign.getState();
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}
	protected static Location loadLocation(ConfigurationSection dataSection){
		World world = Bukkit.getWorld(dataSection.getString("world"));
		int x = dataSection.getInt("x");
		int y = dataSection.getInt("y");
		int z = dataSection.getInt("z");
		return new Location(world, x, y, z);
	}
    protected static boolean validateConstruction(Block centerBottom, BlockFace direction, int topLimit, Player player){
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
