package ch.swisssmp.knightstournament;

import java.util.Random;

import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;

public class Waypoint extends BukkitRunnable{
	private final World world;
	private final SwissSMPler swissSMPler;
	private final Player player;
	private final Location location;
	private final double goalDistance;
	private final double goalDistanceSquared;
	private final Color color;
	private final Runnable onClear;
	private final Random random = new Random();
	
	public Waypoint(Player player, Location location, double goalDistance, Color color, Runnable onClear){
		this.world = location.getWorld();
		this.swissSMPler = SwissSMPler.get(player);
		this.player = player;
		this.location = location;
		this.goalDistance = goalDistance;
		this.goalDistanceSquared = Math.pow(this.goalDistance,2);
		this.color = color;
		this.onClear = onClear;
		this.runTaskTimer(KnightsTournamentPlugin.plugin, 0, 3l);
	}
	@Override
	public void run(){
		if(player.getLocation().distanceSquared(this.location)>this.goalDistanceSquared){			
			double x = this.location.getX()+getRandomOffset();
			double y = this.location.getY()+getRandomOffset();
			double z = this.location.getZ()+getRandomOffset()*3;
			world.spawnParticle(Particle.REDSTONE, x, y+1, z, 1, new Particle.DustOptions(color, 3));
		}
		else{
			ItemStack mainHand = this.player.getInventory().getItemInMainHand();
			ItemStack offHand = this.player.getInventory().getItemInOffHand();
			CompoundTag nbt = (mainHand!=null) ? ItemUtil.getData(mainHand) : null;
			if(nbt==null || (!nbt.containsKey(TournamentLance.dataProperty))){
				swissSMPler.sendActionBar("§bTurnierlanze§r ausrüsten");
				return;
			}
			if(offHand==null || offHand.getType()!=Material.SHIELD){
				swissSMPler.sendActionBar("Schild in Zweithand ausrüsten");
				return;
			}
			swissSMPler.sendActionBar("§EBereit.");
			this.cancel();
			this.onClear.run();
		}
	}
	
	private float getRandomOffset(){
		return -0.05f+random.nextFloat()*0.1f;
	}
}
