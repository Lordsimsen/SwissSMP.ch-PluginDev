package ch.swisssmp.events.halloween;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.customitems.CustomItems;

public class Bombon implements Runnable{
	private static Map<Projectile,Bombon> flyingBombons = new HashMap<Projectile,Bombon>();

	private final Player player;
	private final ItemStack itemStack;
	private final String bombonType;
	
	private Projectile projectile;
	
	private BukkitTask task;
	
	private Bombon(Player player, ItemStack itemStack){
		this.player = player;
		this.itemStack = itemStack;
		this.bombonType = CustomItems.getCustomEnum(itemStack);
	}
	
	@Override
	public void run() {
		if(!this.projectile.isValid()){
			this.remove();
			return;
		}
	}
	
	protected void hit(){
		Location location = this.projectile.getLocation();
		if(bombonType==null) return;
		float explosionStrength;
		switch(bombonType){
		case "BOMBON_RED": explosionStrength = 0.5f; break;
		case "BOMBON_ORANGE": explosionStrength = 1.5f; break;
		case "BOMBON_PURPLE": explosionStrength = 2.5f; break;
		case "BOMBON_AQUA": explosionStrength = 3.5f; break;
		default: return;
		}
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), explosionStrength, false, false);
		this.remove();
	}
	
	private void remove(){
		this.task.cancel();
		flyingBombons.remove(this.projectile);
	}
	
	private Projectile launch(){
		if(itemStack.getAmount()==0) return null;
		ItemStack projectileStack = itemStack.clone();
		projectileStack.setAmount(1);
		itemStack.setAmount(itemStack.getAmount()-1);
		Snowball snowball = player.launchProjectile(Snowball.class);
		this.projectile = snowball;
		return snowball;
	}
	
	protected static Bombon launch(Player player, ItemStack itemStack){
		Bombon result = new Bombon(player, itemStack);
		Projectile projectile = result.launch();
		if(projectile==null) return null;
		result.task = Bukkit.getScheduler().runTaskTimer(HalloweenEventPlugin.getInstance(), result, 0, 1);
		flyingBombons.put(projectile, result);
		return result;
	}
	
	protected static Bombon get(Projectile projectile){
		return flyingBombons.get(projectile);
	}
}
