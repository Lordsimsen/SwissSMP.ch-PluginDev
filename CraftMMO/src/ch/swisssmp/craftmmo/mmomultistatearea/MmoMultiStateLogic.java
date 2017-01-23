package ch.swisssmp.craftmmo.mmomultistatearea;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressureSensor;
import org.bukkit.material.Redstone;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoblock.MmoBlock;
import ch.swisssmp.craftmmo.util.MmoLogicOperator;

public class MmoMultiStateLogic implements Listener{

	public final MmoAreaState areaState;
	public final MmoLogicOperator operator;
	public final HashMap<Location, Integer> valueMap = new HashMap<Location, Integer>();
	public final World world;
	
	public MmoMultiStateLogic(MmoAreaState areaState, ConfigurationSection dataSection, World world){
		this.areaState = areaState;
		this.operator = MmoLogicOperator.valueOf(dataSection.getString("operator"));
		ConfigurationSection blocksSection = dataSection.getConfigurationSection("blocks");
		for(String key : blocksSection.getKeys(false)){
			ConfigurationSection blockSection = blocksSection.getConfigurationSection(key);
			Block block = MmoBlock.get(blockSection, world);
			int b = blockSection.getInt("value");
			valueMap.put(block.getLocation(), b);
		}
		this.world = world;
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}
	
	public boolean apply(Location exclude){
		if(isTrue(exclude)){
			return this.areaState.trigger();
		}
		else return false;
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onBlockInteract(BlockRedstoneEvent event){
		Location location = event.getBlock().getLocation();
		if(!valueMap.containsKey(location)){
			return;
		}
		int newCurrent = event.getNewCurrent();
		int targetCurrent = valueMap.get(location);
		if(targetCurrent==newCurrent){
			if(apply(location)){
				Main.info("I'm triggered ("+this.areaState.schematicName+")");
			}
		}
	}
	
	public boolean isTrue(Location exclude){
		switch(operator){
		case AND:
			for(Entry<Location, Integer> entry : valueMap.entrySet()){
				Location location = entry.getKey();
				if(location.equals(location)) continue;
				Integer current = getCurrent(entry.getKey().getBlock());
				if(current!=entry.getValue()) 
					return false;
			}
			return true;
		case NAND:
			for(Entry<Location, Integer> entry : valueMap.entrySet()){
				Location location = entry.getKey();
				if(location.equals(location)) continue;
				Integer current = getCurrent(entry.getKey().getBlock());
				if(current!=entry.getValue()){
					return true;
				}
			}
			return false;
		case OR:
			for(Entry<Location, Integer> entry : valueMap.entrySet()){
				Location location = entry.getKey();
				if(location.equals(location)) continue;
				Integer current = getCurrent(entry.getKey().getBlock());
				if(current==entry.getValue()){
					return true;
				}
			}
			return false;
		case NOR:
			for(Entry<Location, Integer> entry : valueMap.entrySet()){
				Location location = entry.getKey();
				if(location.equals(location)) continue;
				Integer current = getCurrent(entry.getKey().getBlock());
				if(current==entry.getValue()){
					return false;
				}
			}
			return true;
		case XOR:{
			boolean firstCheck = true;
			boolean firstValue = true;
			for(Entry<Location, Integer> entry : valueMap.entrySet()){
				Location location = entry.getKey();
				if(location.equals(location)) continue;
				Integer current = getCurrent(entry.getKey().getBlock());
				boolean stepResult = (current==entry.getValue());
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
			for(Entry<Location, Integer> entry : valueMap.entrySet()){
				Location location = entry.getKey();
				if(location.equals(location)) continue;
				Integer current = getCurrent(entry.getKey().getBlock());
				boolean stepResult = (current==entry.getValue());
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
			Redstone redstone = (Redstone) data;
			if(redstone.isPowered()) return 1;
			else return 0;
		}
		else if(data instanceof PressureSensor){
			PressureSensor pressureSensor = (PressureSensor) data;
			if(pressureSensor.isPressed()){
				return 1;
			}
			else return 0;
		}
		return (int) data.getData();
	}
}
