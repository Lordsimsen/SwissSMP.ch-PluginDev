package ch.swisssmp.towercontrol;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressurePlate;

public class ArenaTower {
	private final Block block;
	private final String side;
	protected ArenaTower(Block block, String side){
		this.block = block;
		this.side = side;
	}
	protected void trigger(){
		Bukkit.getScheduler().runTaskLater(TowerControl.plugin, new Runnable(){
			public void run(){
				TowerControl.game.checkGameFinished(side);
			}
		}, 1L);
	}
	
	protected boolean isTriggered(){
		MaterialData materialData = block.getState().getData();
		if(!(materialData instanceof PressurePlate)) return false;
		return ((PressurePlate)materialData).isPressed();
	}
	
	@Override
	public String toString(){
		return "ArenaTower("+this.block.getX()+","+this.block.getY()+","+this.block.getZ()+","+this.side+")";
	}
}
