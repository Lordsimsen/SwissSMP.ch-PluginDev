package ch.swisssmp.dungeongenerator;

import org.bukkit.Material;

public enum PartType {
	START(Material.PURPUR_BLOCK),
	GENERIC(Material.AIR),
	CORRIDOR(Material.IRON_BLOCK),
	STAIRS(Material.EMERALD_BLOCK),
	CHAMBER(Material.DIAMOND_BLOCK),
	DOOR(Material.REDSTONE_BLOCK),
	DEAD_END(Material.COAL_BLOCK),
	FORK(Material.OBSIDIAN);
	
	private Material material;
	
	private PartType(Material material){
		this.material = material;
	}
	
	public static PartType get(Material material){
		for(PartType partType : PartType.values()){
			if(partType.material==material) return partType;
		}
		return null;
	}
	
	public static PartType get(String partTypeString){
		try{
			return PartType.valueOf(partTypeString);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public Material getMaterial(){
		return this.material;
	}
}
