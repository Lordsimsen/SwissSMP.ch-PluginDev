package ch.swisssmp.dungeongenerator;

import org.bukkit.util.BlockVector;

public enum Direction {
	UP,
	DOWN,
	NORTH,
	EAST,
	SOUTH,
	WEST;
	
	public Direction opposite(){
		switch(this){
		case UP: return DOWN;
		case DOWN: return UP;
		case NORTH: return SOUTH;
		case EAST: return WEST;
		case SOUTH: return NORTH;
		case WEST: return EAST;
		default: return null;
		}
	}
	
	public String toLowerCase(){
		return this.toString().toLowerCase();
	}
	
	public Direction rotate(int steps){
		steps = steps % 4;
		switch(this){
		case NORTH: if(steps==1) return WEST; else if(steps==2) return SOUTH; else if(steps==3) return EAST; else return this;
		case EAST: if(steps==1) return NORTH; else if(steps==2) return WEST; else if(steps==3) return SOUTH; else return this;
		case SOUTH: if(steps==1) return EAST; else if(steps==2) return NORTH; else if(steps==3) return WEST; else return this;
		case WEST: if(steps==1) return SOUTH; else if(steps==2) return EAST; else if(steps==3) return NORTH; else return this;
		default: return this;
		}
	}
	
	/**
	 * 
	 * @param from - The BlockVector from which to move in this direction
	 * @return A BlockVector next to the original BlockVector in this direction
	 */
	public BlockVector moveVector(BlockVector from){
		switch(this){
		case UP: return new BlockVector(from.getBlockX(),from.getBlockY()+1,from.getBlockZ());
		case DOWN: return new BlockVector(from.getBlockX(),from.getBlockY()-1,from.getBlockZ());
		case NORTH: return new BlockVector(from.getBlockX(),from.getBlockY(),from.getBlockZ()-1);
		case EAST: return new BlockVector(from.getBlockX()+1,from.getBlockY(),from.getBlockZ());
		case SOUTH: return new BlockVector(from.getBlockX(),from.getBlockY(),from.getBlockZ()+1);
		case WEST: return new BlockVector(from.getBlockX()-1,from.getBlockY(),from.getBlockZ());
		default: return from;
		}
	}
}
