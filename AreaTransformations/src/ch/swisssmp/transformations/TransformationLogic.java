package ch.swisssmp.transformations;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressureSensor;
import org.bukkit.material.Redstone;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.VectorKey;

public class TransformationLogic implements Listener{

	public final AreaState areaState;
	public final LogicOperator operator;
	public final HashMap<VectorKey, Integer> valueMap = new HashMap<VectorKey, Integer>();
	public final World world;
	
	public TransformationLogic(AreaState areaState, ConfigurationSection dataSection, World world){
		this.areaState = areaState;
		this.operator = LogicOperator.valueOf(dataSection.getString("operator"));
		ConfigurationSection blocksSection = dataSection.getConfigurationSection("blocks");
		for(String key : blocksSection.getKeys(false)){
			ConfigurationSection blockSection = blocksSection.getConfigurationSection(key);
			Vector vector = blockSection.getVector();
			int b = blockSection.getInt("value");
			valueMap.put(new VectorKey(vector), b);
		}
		this.world = world;
		Bukkit.getPluginManager().registerEvents(this, AreaTransformations.plugin);
	}
	
	private boolean apply(Location exclude, boolean excludedIsTrue){
		if(isTrue(exclude, excludedIsTrue)){
			return this.areaState.trigger();
		}
		else return false;
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockInteract(BlockRedstoneEvent event){
		//AreaTransformations.info("[AreaTransformations] BlockInteractEvent!");
		if(event.getBlock().getWorld()!=this.world) return;
		//AreaTransformations.info("[AreaTransformations] Block ist in gleicher Welt wie TransformationLogic!");
		Location location = event.getBlock().getLocation();
		VectorKey vectorKey = new VectorKey(location.toVector());
		if(!valueMap.containsKey(vectorKey)){
			return;
		}
		AreaTransformations.info("[AreaTransformations] Block ist Teil von TransformationLogic!");
		int newCurrent = event.getNewCurrent();
		int targetCurrent = valueMap.get(vectorKey);
		if(apply(location, newCurrent==targetCurrent)){
			AreaTransformations.info("[AreaTransformations] Transformation triggered ("+this.areaState.getSchematicName()+")");
		}
	}
	
	private boolean isTrue(Location exclude, boolean excludedIsTrue){
		switch(operator){
		case AND:
			for(Entry<VectorKey, Integer> entry : valueMap.entrySet()){
				Vector vector = entry.getKey().getVector();
				if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
					if(!excludedIsTrue) return false;
					else continue;
				};
				Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
				if(current!=entry.getValue()){
					AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
					return false;
				}
			}
			return true;
		case NAND:
			for(Entry<VectorKey, Integer> entry : valueMap.entrySet()){
				Vector vector = entry.getKey().getVector();
				if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
					if(!excludedIsTrue) return true;
					else continue;
				};
				Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
				if(current!=entry.getValue()){
					return true;
				}
				else{
					AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
				}
			}
			return false;
		case OR:
			for(Entry<VectorKey, Integer> entry : valueMap.entrySet()){
				Vector vector = entry.getKey().getVector();
				if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
					if(excludedIsTrue) return true;
					else continue;
				};
				Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
				if(current==entry.getValue()){
					return true;
				}
				else{
					AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
				}
			}
			return false;
		case NOR:
			for(Entry<VectorKey, Integer> entry : valueMap.entrySet()){
				Vector vector = entry.getKey().getVector();
				if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
					if(excludedIsTrue) return false;
					else continue;
				};
				Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
				if(current==entry.getValue()){
					AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
					return false;
				}
			}
			return true;
		case XOR:{
			boolean firstCheck = true;
			boolean firstValue = true;
			for(Entry<VectorKey, Integer> entry : valueMap.entrySet()){
				boolean stepResult;
				Vector vector = entry.getKey().getVector();
				if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
					stepResult = excludedIsTrue;
				}
				else{
					Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
					stepResult = (current==entry.getValue());
				}
				if(firstCheck){
					firstValue = stepResult;
					firstCheck = false;
				}
				if((firstValue && !stepResult) || (!firstValue && stepResult)){
					return true;
				}
			}
			return false;
		}
		case XNOR:{
			boolean firstCheck = true;
			boolean firstValue = true;
			for(Entry<VectorKey, Integer> entry : valueMap.entrySet()){
				boolean stepResult;
				Vector vector = entry.getKey().getVector();
				if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
					stepResult = excludedIsTrue;
				}
				else{
					Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
					stepResult = (current==entry.getValue());
				}
				if(firstCheck){
					firstValue = stepResult;
					firstCheck = false;
				}
				if((firstValue && !stepResult) || (!firstValue && stepResult)){
					return false;
				}
			}
			return true;
		}
		default:
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public Integer getCurrent(Block block){
		MaterialData data = block.getState().getData();
		if(data instanceof Redstone){
			if(((Redstone)data).isPowered()) return 15;
			return 0;
		}
		else if(data instanceof PressureSensor){
			if(((PressureSensor)data).isPressed()) return 15;
			return 0;
		}
		return (int) data.getData();
	}
}
