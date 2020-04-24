package ch.swisssmp.archery;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ArrowManager {
	private static Random random = new Random();
	
	public static String getArrowType(Arrow arrow){
		List<MetadataValue> metadataValues = arrow.getMetadata("arrow_type");
		MetadataValue arrowTypeValue = null;
		for(MetadataValue metadataValue : metadataValues){
			if(metadataValue.getOwningPlugin()!=Archery.getInstance()) continue;
			arrowTypeValue = metadataValue;
			break;
		}
		if(arrowTypeValue == null) return null;
		return arrowTypeValue.asString();
	}
	protected static void onArrowShoot(EntityShootBowEvent event, Arrow arrow, String arrow_type){
		final Color particleColor;
		int bowDurabilityDamage;
		boolean addParticles;
		switch(arrow_type){
		case "BURST_ARROW":
			particleColor = Color.GREEN;
			bowDurabilityDamage = 1;
			addParticles = true;
			break;
		case "TORCH_ARROW":
			particleColor = Color.YELLOW;
			bowDurabilityDamage = 0;
			addParticles = true;
			break;
		case "MULTI_ARROW":
			onMultiArrowShoot(event,arrow);
			bowDurabilityDamage = 1;
			addParticles = false;
			particleColor = Color.BLUE;
			break;
		case "FLAME_ARROW":
			particleColor = Color.ORANGE;
			bowDurabilityDamage = 1;
			addParticles = true;
			break;
		case "EXPLOSIVE_ARROW":
			onExplosiveArrowShoot(event,arrow);
			particleColor = Color.RED;
			bowDurabilityDamage = 2;
			addParticles = true;
			break;
		case "VAMPIRE_ARROW":
			particleColor = Color.PURPLE;
			bowDurabilityDamage = 1;
			addParticles = true;
			break;
		default:return;
		}
		ItemStack bow = event.getBow();
		ProjectileSource shooter = arrow.getShooter();
		if(bowDurabilityDamage>0 && !bow.getItemMeta().isUnbreakable() && (!(shooter instanceof Player) || ((Player)shooter).getGameMode()!=GameMode.CREATIVE)){
			boolean applyDamage = true;
			if(bow.getItemMeta().hasEnchant(Enchantment.DURABILITY)){
				applyDamage = random.nextDouble()<(100/(bow.getItemMeta().getEnchantLevel(Enchantment.DURABILITY)+1));
			}
			if(applyDamage){
				ItemMeta itemMeta = bow.getItemMeta();
				Damageable damageable = (Damageable) itemMeta;
				int newDurability = damageable.getDamage()+bowDurabilityDamage; //watch it, getDurability should be called getDamage because 0 means fully repaired
				damageable.setDamage(newDurability);
				bow.setItemMeta(itemMeta);
			}
		}
		if(addParticles){
			ArrowParticles arrowParticles = new ArrowParticles(arrow,particleColor);
			final BukkitTask task = Bukkit.getScheduler().runTaskTimer(Archery.getInstance(), arrowParticles, 0, 1);
			arrowParticles.setTask(task);
		}
	}
	
	protected static void ensureArrowConsumption(ItemStack bow, Arrow arrow, ItemStack arrowStack, String arrow_type){
		if(!bow.getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)) return;//consumption handled by vanilla minecraft
		boolean allowPickup = false;
		switch(arrow_type){
		case "BURST_ARROW":
		case "FLAME_ARROW":
		case "VAMPIRE_ARROW":
			allowPickup = true;
		case "EXPLOSIVE_ARROW":
			arrowStack.setAmount(arrowStack.getAmount()-1);
			break;
		default:
			return;
		}
		if(allowPickup){
			Bukkit.getScheduler().runTaskLater(Archery.getInstance(), new Runnable(){
				public void run(){
					arrow.setPickupStatus(Arrow.PickupStatus.ALLOWED);
				}
			}, 1L);
		}
	}
	
	protected static void onArrowHit(ProjectileHitEvent event, Arrow arrow, String arrow_type){
		switch(arrow_type){
		case "BURST_ARROW": onBurstArrowHit(event,arrow);break;
		case "BURST_ARROW_THORN":onBurstArrowThornHit(event,arrow);break;
		case "TORCH_ARROW": onTorchArrowHit(event,arrow);break;
		case "FLAME_ARROW": onFlameArrowHit(event,arrow);break;
		case "EXPLOSIVE_ARROW": onExplosiveArrowHit(event,arrow);break;
		case "VAMPIRE_ARROW": onVampireArrowHit(event,arrow);break;
		default:return;
		}
	}
	
	protected static void onArrowDamage(EntityDamageByEntityEvent event, Arrow arrow, String arrow_type){
		switch(arrow_type){
		case "IRON_ARROW":onIronArrowDamage(event,arrow);break;
		case "VAMPIRE_ARROW": onVampireArrowDamage(event,arrow);break;
		default:return;
		}
	}
	
	protected static double getArrowDamage(ItemStack bow, Arrow arrow, Material dummyType){
		int defensePoints;
		int toughness;
		switch(dummyType){
		case OAK_PLANKS:
			defensePoints = 7;
			toughness = 0;
			break;
		case STONE:
			defensePoints = 12;
			toughness = 0;
			break;
		case IRON_BLOCK:
			defensePoints = 15;
			toughness = 0;
			break;
		case GOLD_BLOCK:
			defensePoints = 11;
			toughness = 0;
			break;
		case DIAMOND_BLOCK:
			defensePoints = 20;
			toughness = 10;
			break;
		default:
			defensePoints = 0;
			toughness = 0;
			break;
		}
		double damage = 9*(Math.min(3,arrow.getVelocity().length())/3);
		String customEnum = ArrowManager.getArrowType(arrow);
		if(customEnum!=null && customEnum.equals("IRON_ARROW")){
			damage*=1.5f;
		}
		if(arrow.isCritical()&&random.nextDouble()<0.2f){
			damage+=1;
		}
		if(bow!=null && bow.getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)){
			damage*=(1+(0.25f*(bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE)+1)));
		}
		damage = damage * ( 1 - Math.min( 20, Math.max( defensePoints / 5, defensePoints - damage / ( toughness / 4 + 2 ) ) ) / 25 );
		return damage;
	}
	
	private static void onIronArrowDamage(EntityDamageByEntityEvent event, Arrow arrow){
		event.setDamage(event.getDamage()*1.5f);
	}
	
	private static void onBurstArrowHit(ProjectileHitEvent event, Arrow arrow){
		if(event.getHitEntity()==null){
			//Arrow did not hit anything and is retrievable
			arrow.setMetadata("arrow_type", new FixedMetadataValue(Archery.getInstance(),"BURST_ARROW"));
			return;
		}
		Vector forward = arrow.getVelocity().setY(0).normalize();
		float speed = (float)arrow.getVelocity().length()*0.2f;
		Vector left = arrow.getVelocity().crossProduct(new Vector(0,1,0)).normalize();
		ProjectileSource source = (event.getHitEntity() instanceof ProjectileSource)?(ProjectileSource)event.getHitEntity():null;
		Location from = event.getHitEntity().getLocation().add(0,event.getHitEntity().getHeight()/2,0);
		final ProjectileSource shooter = arrow.getShooter();
		final Arrow arrow_0 = ArrowManager.spawnArrow(source, from, forward.clone().add(left).multiply(speed), Arrow.PickupStatus.DISALLOWED);
		final Arrow arrow_1 = ArrowManager.spawnArrow(source, from, forward.clone().subtract(left).multiply(speed), Arrow.PickupStatus.DISALLOWED);
		final Arrow arrow_2 = ArrowManager.spawnArrow(source, from, forward.clone().add(left).multiply(-speed), Arrow.PickupStatus.DISALLOWED);
		final Arrow arrow_3 = ArrowManager.spawnArrow(source, from, forward.clone().subtract(left).multiply(-speed), Arrow.PickupStatus.DISALLOWED);
		FixedMetadataValue arrowTypeValue = new FixedMetadataValue(Archery.getInstance(),"BURST_ARROW_THORN");
		arrow_0.setMetadata("arrow_type", arrowTypeValue);
		arrow_1.setMetadata("arrow_type", arrowTypeValue);
		arrow_2.setMetadata("arrow_type", arrowTypeValue);
		arrow_3.setMetadata("arrow_type", arrowTypeValue);
		Bukkit.getScheduler().runTaskLater(Archery.getInstance(), new Runnable(){
			public void run(){
				arrow_0.setShooter(shooter);
				arrow_1.setShooter(shooter);
				arrow_2.setShooter(shooter);
				arrow_3.setShooter(shooter);
			}
		}, 2L);
	}
	
	private static void onBurstArrowThornHit(ProjectileHitEvent event,Arrow arrow){
		Bukkit.getScheduler().runTaskLater(Archery.getInstance(), new Runnable(){
			public void run(){
				arrow.remove();
			}
		}, 5L);
	}
	
	private static void onTorchArrowHit(ProjectileHitEvent event, Arrow arrow){
		if(event.getHitBlock()!=null){
			Bukkit.getScheduler().runTaskLater(Archery.getInstance(), new Runnable(){
				//@SuppressWarnings("deprecation")
				public void run(){

					BlockFace relative = event.getHitBlock().getFace(arrow.getLocation().getBlock());
					if(relative==null)return;
					//byte data;
					switch(relative){
					case EAST:
					case EAST_NORTH_EAST:
					case EAST_SOUTH_EAST:
						relative = BlockFace.EAST;
						//data = 1;
						break;
					case NORTH:
					case NORTH_EAST:
					case NORTH_NORTH_EAST:
					case NORTH_NORTH_WEST:
					case NORTH_WEST:
						relative = BlockFace.NORTH;
						//data = 4;
						break;
					case SOUTH:
					case SOUTH_EAST:
					case SOUTH_SOUTH_EAST:
					case SOUTH_SOUTH_WEST:
					case SOUTH_WEST:
						relative = BlockFace.SOUTH;
						//data = 3;
						break;
					case WEST:
					case WEST_NORTH_WEST:
					case WEST_SOUTH_WEST:
						relative = BlockFace.WEST;
						//data = 2;
						break;
					case UP:
						relative = BlockFace.UP;
						//data = 5;
						break;
					default:return;
					}
					Block torchedBlock = event.getHitBlock().getRelative(relative);
					if(torchedBlock.getType()!=Material.AIR) return;
					BlockData blockData = Bukkit.createBlockData(Material.TORCH);
					
					torchedBlock.setBlockData(blockData,false);
					
					//torchedBlock.setTypeIdAndData(Material.TORCH.getId(),data,false);
					arrow.remove();
				}
			}, 1L);
		}
		else if(event.getHitEntity()!=null && event.getHitEntity().getFireTicks()<20) event.getHitEntity().setFireTicks(20);
	}
	
	private static void onMultiArrowShoot(EntityShootBowEvent event, Arrow arrow){
		Vector forward = arrow.getVelocity().normalize();
		float speed = (float)arrow.getVelocity().length();
		Vector left = arrow.getVelocity().crossProduct(new Vector(0,1,0)).normalize().multiply(0.75f-event.getForce()/2f);
		Vector leftVelocity = forward.clone().add(left).multiply(speed);
		Vector rightVelocity = forward.clone().subtract(left).multiply(speed);
		//Bukkit.getLogger().info("Forward: "+forward.getX()+","+forward.getY()+","+forward.getZ());
		//Bukkit.getLogger().info("Left: "+left.getX()+","+left.getY()+","+left.getZ());
		//Bukkit.getLogger().info("Left Arrow: "+leftVelocity.getX()+","+leftVelocity.getY()+","+leftVelocity.getZ());
		//Bukkit.getLogger().info("Right Arrow: "+rightVelocity.getX()+","+rightVelocity.getY()+","+rightVelocity.getZ());
		ProjectileSource source = (ProjectileSource)event.getEntity();
		ArrowManager.spawnArrow(source, arrow.getLocation(), leftVelocity, arrow.getPickupStatus());
		ArrowManager.spawnArrow(source, arrow.getLocation(), rightVelocity, arrow.getPickupStatus());
	}
	
	private static void onFlameArrowHit(ProjectileHitEvent event, Arrow arrow){
		if(event.getHitEntity()==null)return;
		if(arrow.getShooter() instanceof Entity && event.getHitEntity() instanceof LivingEntity)((LivingEntity)event.getHitEntity()).damage(2, (Entity)arrow.getShooter());
		event.getHitEntity().setFireTicks(100);
	}
	
	private static void onExplosiveArrowShoot(EntityShootBowEvent event, Arrow arrow){
		arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
	}
	
	private static void onExplosiveArrowHit(ProjectileHitEvent event, Arrow arrow){
		final Entity target;
		if(event.getHitEntity()!=null){
			target = event.getHitEntity();
		}
		else{
			target = arrow;
		}
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(Archery.getInstance(), new Runnable(){
			Vector offset = new Vector(0,target.getHeight()/2,0);
			Random random = new Random();
			Location location;
			float r;
			float g;
			float b;
			public void run(){
				location = target.getLocation().add(offset);
				r = 0.95f+random.nextFloat()*0.05f;
				g = 0.4f+random.nextFloat()*0.05f;
				b = 0.1f+random.nextFloat()*0.05f;
				target.getWorld().spawnParticle(Particle.REDSTONE, location.getX(),location.getY(),location.getZ(),0,this.r,this.g,this.b,1);
			}
		}, 0, 1l);
		Bukkit.getScheduler().runTaskLater(Archery.getInstance(), new Runnable(){
			public void run(){
				if(target==arrow)arrow.remove();
				task.cancel();
				Location hitLocation = target.getLocation();
				target.getWorld().createExplosion(hitLocation.getX(),hitLocation.getY(),hitLocation.getZ(),1.5f,false,false);
			}
		}, 40L);
	}
	
	private static void onVampireArrowHit(ProjectileHitEvent event, Arrow arrow){
		if(event.getHitEntity()==null || !(event.getHitEntity() instanceof LivingEntity))return;
		Entity entity = event.getHitEntity();
		Location location = entity.getLocation().add(0,entity.getHeight()/2,0);
		Vector position;
		for(int i = 0; i < 10; i++){
			position = location.toVector().add(Vector.getRandom().multiply(0.2f));
			location.getWorld().spawnParticle(Particle.SPELL_MOB, position.getX(),position.getY(),position.getZ(),0,1,0.1f,0,0.7f);
		}
		entity.getWorld().playSound(location, Sound.ITEM_BUCKET_FILL, 1f, 1f);
	}
	
	private static void onVampireArrowDamage(EntityDamageByEntityEvent event, Arrow arrow){
		if(event.getDamage()<=0) return;
		ProjectileSource shooter = arrow.getShooter();
		if(!(shooter instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity)shooter;
		livingEntity.setHealth(Math.min(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), livingEntity.getHealth()+event.getDamage()/4));
		Location location = livingEntity.getLocation().add(0,livingEntity.getHeight()/2,0);
		Vector position;
		for(int i = 0; i < 10; i++){
			position = location.toVector().add(Vector.getRandom().multiply(0.2f));
			location.getWorld().spawnParticle(Particle.SPELL_MOB, position.getX(),position.getY(),position.getZ(),0,1,0.1f,0,0.7f);
		}
	}
	
	private static Arrow spawnArrow(ProjectileSource source, Location from, Vector velocity, Arrow.PickupStatus pickupStatus){
		Arrow result;
		if(source!=null){
			result = source.launchProjectile(Arrow.class, velocity);
		}
		else{
			result = from.getWorld().spawnArrow(from, velocity, (float)velocity.length(), 4);
		}
		result.setPickupStatus(pickupStatus);
		return result;
	}
}
