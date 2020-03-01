package ch.swisssmp.city.ceremony.founding;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class RingPresentation implements Runnable {
	
	private BukkitTask task;
	private Player player;
	
	private ArmorStand ringStand;
	private long t;
	
	private World hoverWorld;
	private double hoverX;
	private double hoverY;
	private double hoverZ;
	private float startYaw;
	
	private float yawMotion = 2;
	
	private void start(Location location, CityFoundingCeremony ceremony, Player player){
		this.player = player;
		ringStand = spawnRingStand(location, ceremony);
		task = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), this, 0, 1);
	}

	@Override
	public void run() {
		t+=1;
		float hoverYaw = startYaw + yawMotion * t;
		ringStand.teleport(new Location(hoverWorld, hoverX, hoverY, hoverZ, hoverYaw, 0));
	}
	
	public Location getLocation(){
		return ringStand.getEyeLocation();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void finish(){
		if(ringStand!=null) ringStand.remove();
		if(task!=null) task.cancel();
	}
	
	private ArmorStand spawnRingStand(Location location, CityFoundingCeremony ceremony){
		ItemStack itemStack;
		try{
			CustomItemBuilder ringBuilder = CustomItems.getCustomItemBuilder(ceremony.getRingType());
			if(ceremony.getInitiator()==player){
				ringBuilder.addEnchantment(Enchantment.DURABILITY, 1, false);
			}
			itemStack = ringBuilder.build();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		hoverWorld = location.getWorld();
		hoverX = location.getX();
		hoverY = location.getY();
		hoverZ = location.getZ();
		
		ArmorStand result = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		EntityEquipment equipment = result.getEquipment();
		result.setVisible(false);
		result.setInvulnerable(true);
		result.setGravity(false);
		equipment.setHelmet(itemStack);
		result.setMetadata("interactable", new FixedMetadataValue(CitySystemPlugin.getInstance(), false));
		
		return result;
	}

	public static RingPresentation start(Location location, CityFoundingCeremony ceremony, Player player, float startYaw){
		RingPresentation result = new RingPresentation();
		result.start(location, ceremony, player);
		result.startYaw = startYaw;
		return result;
	}
}
