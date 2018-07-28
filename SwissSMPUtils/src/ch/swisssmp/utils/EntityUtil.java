package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffect;

public class EntityUtil {
	
	@SuppressWarnings("deprecation")
	public static Entity clone(Entity template, Location location){
		Entity target;
		try{
			if(template instanceof FallingBlock){
				//TODO change to 1.13 ways
				target = location.getWorld().spawnFallingBlock(location, ((FallingBlock)template).getMaterial(), ((FallingBlock)template).getBlockData());
			}
			else if(template instanceof Player){
				//Cannot clone Players
				return null;
			}
			else{
				target = location.getWorld().spawnEntity(location, template.getType());
			}
		}
		catch(Exception e){
			Bukkit.getLogger().info("[EntityUtils] Cloning of Entity "+template.getName()+" failed: "+e.getMessage());
			return null;
		}
		EntityUtil.cloneEntitySettings(template, target);
		if(template instanceof AbstractHorse){
			EntityUtil.cloneAbstractHorseSettings((AbstractHorse)template, (AbstractHorse)target);
		}
		if(template instanceof Ageable){
			EntityUtil.cloneAgeableSettings((Ageable)template,(Ageable)target);
		}
		if(template instanceof AreaEffectCloud){
			EntityUtil.cloneAreaEffectCloudSettings((AreaEffectCloud)template,(AreaEffectCloud)target);
		}
		if(template instanceof ArmorStand){
			EntityUtil.cloneArmorStandSettings((ArmorStand)template, (ArmorStand)target);
		}
		if(template instanceof Arrow){
			EntityUtil.cloneArrowSettings((Arrow)template, (Arrow)target);
		}
		if(template instanceof Attributable){
			EntityUtil.cloneAttributeSettings((Attributable)template, (Attributable)target);
		}
		if(template instanceof Bat){
			EntityUtil.cloneBatSettings((Bat)template, (Bat)target);
		}
		if(template instanceof Boat){
			EntityUtil.cloneBoatSettings((Boat)template, (Boat)target);
		}
		if(template instanceof ChestedHorse){
			EntityUtil.cloneChestedHorseSettings((ChestedHorse)template, (ChestedHorse)target);
		}
		if(template instanceof Colorable){
			EntityUtil.cloneColorableSettings((Colorable)template, (Colorable)target);
		}
		if(template instanceof CommandMinecart){
			EntityUtil.cloneCommandMinecartSettings((CommandMinecart)template, (CommandMinecart)target);
		}
		if(template instanceof Creature){
			EntityUtil.cloneCreatureSettings((Creature)template, (Creature)target);
		}
		if(template instanceof Creeper){
			EntityUtil.cloneCreeperSettings((Creeper)template, (Creeper)target);
		}
		if(template instanceof Damageable){
			EntityUtil.cloneDamageableSettings((Damageable)template,(Damageable)target);
		}
		if(template instanceof EnderCrystal){
			EntityUtil.cloneEnderCrystalSettings((EnderCrystal)template,(EnderCrystal)target);
		}
		if(template instanceof EnderDragon){
			EntityUtil.cloneEnderDragonSettings((EnderDragon)template,(EnderDragon)target);
		}
		if(template instanceof Enderman){
			EntityUtil.cloneEndermanSettings((Enderman)template,(Enderman)target);
		}
		if(template instanceof EnderSignal){
			EntityUtil.cloneEnderSignalSettings((EnderSignal)template,(EnderSignal)target);
		}
		if(template instanceof ExperienceOrb){
			EntityUtil.cloneExperienceOrbSettings((ExperienceOrb)template,(ExperienceOrb)target);
		}
		if(template instanceof Explosive){
			EntityUtil.cloneExplosiveSettings((Explosive)template,(Explosive)target);
		}
		if(template instanceof FallingBlock){
			EntityUtil.cloneFallingBlockSettings((FallingBlock)template,(FallingBlock)target);
		}
		if(template instanceof Fireball){
			EntityUtil.cloneFireballSettings((Fireball)template,(Fireball)target);
		}
		if(template instanceof Firework){
			EntityUtil.cloneFireworkSettings((Firework)template,(Firework)target);
		}
		if(template instanceof Hanging){
			EntityUtil.cloneHangingSettings((Hanging)template,(Hanging)target);
		}
		if(template instanceof HopperMinecart){
			EntityUtil.cloneHopperMinecartSettings((HopperMinecart)template,(HopperMinecart)target);
		}
		if(template instanceof Horse){
			EntityUtil.cloneHorseSettings((Horse)template,(Horse)target);
		}
		if(template instanceof HumanEntity){
			EntityUtil.cloneHumanEntitySettings((HumanEntity)template,(HumanEntity)target);
		}
		if(template instanceof InventoryHolder){
			EntityUtil.cloneInventoryHolderSettings((InventoryHolder)template, (InventoryHolder)target);
		}
		if(template instanceof IronGolem){
			EntityUtil.cloneIronGolemSettings((IronGolem)template, (IronGolem)target);
		}
		if(template instanceof Item){
			EntityUtil.cloneItemSettings((Item)template, (Item)target);
		}
		if(template instanceof ItemFrame){
			EntityUtil.cloneItemFrameSettings((ItemFrame)template, (ItemFrame)target);
		}
		if(template instanceof LivingEntity){
			EntityUtil.cloneLivingEntitySettings((LivingEntity)template, (LivingEntity)target);
		}
		if(template instanceof Llama){
			EntityUtil.cloneLlamaSettings((Llama)template, (Llama)target);
		}
		if(template instanceof Minecart){
			EntityUtil.cloneMinecartSettings((Minecart)template, (Minecart)target);
		}
		if(template instanceof Nameable){
			EntityUtil.cloneNameableSettings((Nameable)template, (Nameable)target);
		}
		if(template instanceof Ocelot){
			EntityUtil.cloneOcelotSettings((Ocelot)template, (Ocelot)target);
		}
		if(template instanceof Painting){
			EntityUtil.clonePaintingSettings((Painting)template, (Painting)target);
		}
		if(template instanceof Parrot){
			EntityUtil.cloneParrotSettings((Parrot)template, (Parrot)target);
		}
		//TODO unlock 1.13 capability
		/*
		if(template instanceof Phantom){
			EntityUtil.clonePhantomSettings((Phantom)template, (Phantom)target);
		}
		*/
		if(template instanceof Pig){
			EntityUtil.clonePigSettings((Pig)template, (Pig)target);
		}
		if(template instanceof PigZombie){
			EntityUtil.clonePigZombieSettings((PigZombie)template, (PigZombie)target);
		}
		if(template instanceof Projectile){
			EntityUtil.cloneProjectileSettings((Projectile)template, (Projectile)target);
		}
		//TODO unlock 1.13 capability
		/*
		if(template instanceof PufferFish){
			EntityUtil.clonePufferFishSettings((PufferFish)template, (PufferFish)target);
		}
		*/
		if(template instanceof Rabbit){
			EntityUtil.cloneRabbitSettings((Rabbit)template, (Rabbit)target);
		}
		if(template instanceof Sheep){
			EntityUtil.cloneSheepSettings((Sheep)template, (Sheep)target);
		}
		if(template instanceof ShulkerBullet){
			EntityUtil.cloneShulkerBulletSettings((ShulkerBullet)template, (ShulkerBullet)target);
		}
		if(template instanceof Slime){
			EntityUtil.cloneSlimeSettings((Slime)template, (Slime)target);
		}
		if(template instanceof Snowman){
			EntityUtil.cloneSnowmanSettings((Snowman)template, (Snowman)target);
		}
		if(template instanceof SpawnerMinecart){
			EntityUtil.cloneSpawnerMinecartSettings((SpawnerMinecart)template, (SpawnerMinecart)target);
		}
		if(template instanceof SpectralArrow){
			EntityUtil.cloneSpectralArrowSettings((SpectralArrow)template, (SpectralArrow)target);
		}
		if(template instanceof Spellcaster){
			EntityUtil.cloneSpellcasterSettings((Spellcaster)template, (Spellcaster)target);
		}
		if(template instanceof Tameable){
			EntityUtil.cloneTameableSettings((Tameable)template, (Tameable)target);
		}
		if(template instanceof ThrownPotion){
			EntityUtil.cloneThrownPotionSettings((ThrownPotion)template, (ThrownPotion)target);
		}
		if(template instanceof TippedArrow){
			EntityUtil.cloneTippedArrowSettings((TippedArrow)template, (TippedArrow)target);
		}
		if(template instanceof TNTPrimed){
			EntityUtil.cloneTNTPrimedSettings((TNTPrimed)template, (TNTPrimed)target);
		}
		//TODO unlock 1.13 capability
		/*
		if(template instanceof TropicalFish){
			EntityUtil.cloneTropicalFishSettings((TropicalFish)template, (TropicalFish)target);
		}
		*/
		if(template instanceof Vehicle){
			EntityUtil.cloneVehicleSettings((Vehicle)template, (Vehicle)target);
		}
		if(template instanceof Villager){
			EntityUtil.cloneVillagerSettings((Villager)template, (Villager)target);
		}
		if(template instanceof WitherSkull){
			EntityUtil.cloneWitherSkullSettings((WitherSkull)template, (WitherSkull)target);
		}
		if(template instanceof Wolf){
			EntityUtil.cloneWolfSettings((Wolf)template, (Wolf)target);
		}
		if(template instanceof Zombie){
			EntityUtil.cloneZombieSettings((Zombie)template, (Zombie)target);
		}
		if(template instanceof ZombieVillager){
			EntityUtil.cloneZombieVillagerSettings((ZombieVillager)template, (ZombieVillager)target);
		}
		Entity passengerClone;
		for(Entity passenger : template.getPassengers()){
			passengerClone = EntityUtil.clone(passenger, location);
			if(passengerClone==null)continue;
			target.addPassenger(passengerClone);
		}
		return target;
	}
	private static void cloneEntitySettings(Entity template, Entity target){
		target.setCustomNameVisible(template.isCustomNameVisible());
		target.setFireTicks(template.getFireTicks());
		target.setGlowing(template.isGlowing());
		target.setGravity(template.hasGravity());
		target.setInvulnerable(template.isInvulnerable());
		target.setSilent(template.isSilent());
	}
	private static void cloneAbstractHorseSettings(AbstractHorse template, AbstractHorse target){
		template.setDomestication(target.getDomestication());
		template.setJumpStrength(target.getJumpStrength());
		template.setMaxDomestication(template.getMaxDomestication());
	}
	private static void cloneAgeableSettings(Ageable template, Ageable target){
		if(template.isAdult()) target.setAdult();
		else target.setBaby();
		target.setAge(template.getAge());
		target.setAgeLock(template.getAgeLock());
		target.setBreed(template.canBreed());
	}
	private static void cloneAreaEffectCloudSettings(AreaEffectCloud template, AreaEffectCloud target){
		for(PotionEffect potionEffect : template.getCustomEffects()){
			target.addCustomEffect(potionEffect, true);
		}
		target.setBasePotionData(template.getBasePotionData());
		target.setColor(template.getColor());
		target.setDuration(template.getDuration());
		target.setDurationOnUse(template.getDurationOnUse());
		target.setParticle(template.getParticle());
		target.setRadius(template.getRadius());
		target.setRadiusOnUse(template.getRadiusOnUse());
		target.setRadiusPerTick(template.getRadiusPerTick());
		target.setReapplicationDelay(template.getReapplicationDelay());
		target.setSource(template.getSource());
		target.setWaitTime(template.getWaitTime());
	}
	private static void cloneArmorStandSettings(ArmorStand template, ArmorStand target){
		target.setArms(template.hasArms());
		target.setBasePlate(template.hasBasePlate());
		target.setBodyPose(template.getBodyPose());
		target.setBoots(template.getBoots().clone());
		target.setChestplate(template.getChestplate().clone());
		target.setHeadPose(target.getHeadPose());
		target.setHelmet(template.getHelmet().clone());
		target.setItemInHand(template.getItemInHand().clone());
		target.setLeftArmPose(template.getLeftArmPose());
		target.setLeftLegPose(template.getLeftLegPose());
		target.setLeggings(template.getLeggings().clone());
		target.setMarker(template.isMarker());
		target.setRightArmPose(template.getRightArmPose());
		target.setRightLegPose(template.getRightLegPose());
		target.setSmall(template.isSmall());
		target.setVisible(template.isVisible());
	}
	private static void cloneArrowSettings(Arrow template, Arrow target){
		target.setCritical(template.isCritical());
		target.setKnockbackStrength(template.getKnockbackStrength());
		target.setPickupStatus(template.getPickupStatus());
	}
	private static void cloneAttributeSettings(Attributable template, Attributable target){
		AttributeInstance templateAttribute;
		AttributeInstance targetAttribute;
		for(Attribute attribute : Attribute.values()){
			templateAttribute = template.getAttribute(attribute);
			targetAttribute = target.getAttribute(attribute);
			targetAttribute.setBaseValue(templateAttribute.getBaseValue());
			for(AttributeModifier modifier : templateAttribute.getModifiers()){
				targetAttribute.addModifier(new AttributeModifier(modifier.getName(),modifier.getAmount(),modifier.getOperation()));
			}
		}
	}
	private static void cloneBatSettings(Bat template, Bat target){
		target.setAwake(template.isAwake());
	}
	private static void cloneBoatSettings(Boat template, Boat target){
		target.setWoodType(template.getWoodType());
	}
	private static void cloneChestedHorseSettings(ChestedHorse template, ChestedHorse target){
		target.setCarryingChest(template.isCarryingChest());
	}
	private static void cloneColorableSettings(Colorable template, Colorable target){
		target.setColor(template.getColor());
	}
	private static void cloneCommandMinecartSettings(CommandMinecart template, CommandMinecart target){
		target.setCommand(template.getCommand());
		target.setName(template.getName());
	}
	private static void cloneCreatureSettings(Creature template, Creature target){
		target.setTarget(template.getTarget());
	}
	private static void cloneCreeperSettings(Creeper template, Creeper target){
		target.setPowered(template.isPowered());
		//TODO add 1.13 capabilities
	}
	private static void cloneDamageableSettings(Damageable template, Damageable target){
		target.setHealth(template.getHealth());
	}
	private static void cloneEnderCrystalSettings(EnderCrystal template, EnderCrystal target){
		target.setBeamTarget(template.getBeamTarget());
		target.setShowingBottom(template.isShowingBottom());
	}
	private static void cloneEnderDragonSettings(EnderDragon template, EnderDragon target){
		target.setPhase(template.getPhase());
	}
	private static void cloneEndermanSettings(Enderman template, Enderman target){
		target.setCarriedMaterial(template.getCarriedMaterial());
	}
	private static void cloneEnderSignalSettings(EnderSignal template, EnderSignal target){
		//TODO add 1.13 capabilities
	}
	private static void cloneEntityEquipment(EntityEquipment template, EntityEquipment target){
		target.setBoots(template.getBoots().clone());
		target.setChestplate(template.getChestplate().clone());
		target.setHelmet(template.getHelmet().clone());
		target.setItemInMainHand(template.getItemInMainHand().clone());
		target.setItemInOffHand(template.getItemInOffHand().clone());
		target.setLeggings(template.getLeggings().clone());
		target.setBootsDropChance(template.getBootsDropChance());
		target.setChestplateDropChance(template.getChestplateDropChance());
		target.setHelmetDropChance(template.getHelmetDropChance());
		target.setItemInMainHandDropChance(template.getItemInMainHandDropChance());
		target.setItemInOffHandDropChance(template.getItemInOffHandDropChance());
		target.setLeggingsDropChance(template.getLeggingsDropChance());
	}
	private static void cloneExperienceOrbSettings(ExperienceOrb template, ExperienceOrb target){
		target.setExperience(template.getExperience());
	}
	private static void cloneExplosiveSettings(Explosive template, Explosive target){
		target.setIsIncendiary(template.isIncendiary());
		target.setYield(template.getYield());
	}
	private static void cloneFallingBlockSettings(FallingBlock template, FallingBlock target){
		target.setHurtEntities(template.canHurtEntities());
		target.setDropItem(template.getDropItem());
	}
	private static void cloneFireballSettings(Fireball template, Fireball target){
		target.setDirection(template.getDirection());
	}
	private static void cloneFireworkSettings(Firework template, Firework target){
		target.setFireworkMeta(template.getFireworkMeta());
	}
	private static void cloneHangingSettings(Hanging template, Hanging target){
		target.setFacingDirection(template.getAttachedFace(), true);
	}
	private static void cloneHopperMinecartSettings(HopperMinecart template, HopperMinecart target){
		target.setEnabled(template.isEnabled());
	}
	private static void cloneHorseSettings(Horse template, Horse target){
		target.setColor(template.getColor());
		target.setStyle(template.getStyle());
	}
	private static void cloneHumanEntitySettings(HumanEntity template, HumanEntity target){
		target.setGameMode(template.getGameMode());
	}
	private static void cloneInventoryHolderSettings(InventoryHolder template, InventoryHolder target){
		EntityUtil.cloneInventorySettings(template.getInventory(), target.getInventory());
	}
	private static void cloneInventorySettings(Inventory template, Inventory target){
		for(int i = 0; i < template.getSize(); i++){
			target.setItem(i, (template.getItem(i)!=null ? template.getItem(i).clone() : null));
		}
		if(template instanceof PlayerInventory) ((PlayerInventory)target).setItemInOffHand(((PlayerInventory)template).getItemInOffHand()!=null ? ((PlayerInventory)template).getItemInOffHand().clone() : null);
	}
	private static void cloneIronGolemSettings(IronGolem template, IronGolem target){
		target.setPlayerCreated(template.isPlayerCreated());
	}
	private static void cloneItemSettings(Item template, Item target){
		if(template.getItemStack()!=null) target.setItemStack(template.getItemStack().clone());
		target.setPickupDelay(template.getPickupDelay());
	}
	private static void cloneItemFrameSettings(ItemFrame template, ItemFrame target){
		if(template.getItem()!=null) target.setItem(template.getItem().clone());
		target.setRotation(template.getRotation());
	}
	private static void cloneLivingEntitySettings(LivingEntity template, LivingEntity target){
		target.setAI(template.hasAI());
		target.setCanPickupItems(template.getCanPickupItems());
		target.setCollidable(template.isCollidable());
		target.setGliding(template.isGliding());
		target.setMaximumAir(template.getMaximumAir());
		target.setMaximumNoDamageTicks(template.getMaximumNoDamageTicks());
		target.setRemoveWhenFarAway(template.getRemoveWhenFarAway());
		target.addPotionEffects(template.getActivePotionEffects());
		EntityUtil.cloneEntityEquipment(template.getEquipment(), target.getEquipment());
	}
	private static void cloneLlamaSettings(Llama template, Llama target){
		target.setColor(template.getColor());
		target.setStrength(template.getStrength());
	}
	private static void cloneMinecartSettings(Minecart template, Minecart target){
		//TODO add 1.13 capabilities
		target.setDamage(template.getDamage());
		target.setDerailedVelocityMod(template.getDerailedVelocityMod());
		target.setDisplayBlock(template.getDisplayBlock());
		target.setDisplayBlockOffset(template.getDisplayBlockOffset());
		target.setFlyingVelocityMod(template.getFlyingVelocityMod());
		target.setMaxSpeed(template.getMaxSpeed());
		target.setSlowWhenEmpty(template.isSlowWhenEmpty());
	}
	private static void cloneNameableSettings(Nameable template, Nameable target){
		if(target.getCustomName()!=null)
			target.setCustomName(template.getCustomName());
	}
	private static void cloneOcelotSettings(Ocelot template, Ocelot target){
		target.setCatType(template.getCatType());
	}
	private static void clonePaintingSettings(Painting template, Painting target){
		target.setArt(template.getArt(), true);
	}
	private static void cloneParrotSettings(Parrot template, Parrot target){
		target.setVariant(template.getVariant());
	}
	//TODO unlock 1.13 capability
	/*
	private static void clonePhantomSettings(Phantom template, Phantom target){
		target.setSize(template.getSize());
	}
	*/
	private static void clonePigSettings(Pig template, Pig target){
		target.setSaddle(template.hasSaddle());
	}
	private static void clonePigZombieSettings(PigZombie template, PigZombie target){
		target.setAnger(template.getAnger());
		target.setAngry(template.isAngry());
	}
	private static void cloneProjectileSettings(Projectile template, Projectile target){
		target.setBounce(template.doesBounce());
		target.setShooter(template.getShooter());
	}
	//TODO unlock 1.13 capability
	/*
	private static void clonePufferFishSettings(PufferFish template, PufferFish target){
		target.setPuffState(template.getPuffState());
	}
	*/
	private static void cloneRabbitSettings(Rabbit template, Rabbit target){
		target.setRabbitType(template.getRabbitType());
	}
	private static void cloneSheepSettings(Sheep template, Sheep target){
		target.setSheared(template.isSheared());
	}
	private static void cloneShulkerBulletSettings(ShulkerBullet template, ShulkerBullet target){
		target.setTarget(template.getTarget());
	}
	private static void cloneSlimeSettings(Slime template, Slime target){
		//TODO add 1.13 capability
		target.setSize(template.getSize());
	}
	private static void cloneSnowmanSettings(Snowman template, Snowman target){
		target.setDerp(template.isDerp());
	}
	private static void cloneSpawnerMinecartSettings(SpawnerMinecart template, SpawnerMinecart target){
		//TODO add code to clone EntityType as well (as soon as it is possible)
		Bukkit.getLogger().info("[WARNING] [EntityUtil] Cannot clone the spawned EntityType of SpawnerMinecarts.");
	}
	private static void cloneSpectralArrowSettings(SpectralArrow template, SpectralArrow target){
		target.setGlowingTicks(template.getGlowingTicks());
	}
	private static void cloneSpellcasterSettings(Spellcaster template, Spellcaster target){
		target.setSpell(template.getSpell());
	}
	private static void cloneTameableSettings(Tameable template, Tameable target){
		target.setOwner(template.getOwner());
		target.setTamed(template.isTamed());
	}
	private static void cloneThrownPotionSettings(ThrownPotion template, ThrownPotion target){
		if(template.getItem()!=null) target.setItem(template.getItem().clone());
	}
	private static void cloneTippedArrowSettings(TippedArrow template, TippedArrow target){
		for(PotionEffect potionEffect : template.getCustomEffects()){
			target.addCustomEffect(potionEffect, true);
		}
		target.setBasePotionData(template.getBasePotionData());
		target.setColor(template.getColor());
	}
	private static void cloneTNTPrimedSettings(TNTPrimed template, TNTPrimed target){
		target.setFuseTicks(template.getFuseTicks());
	}
	//TODO unlock 1.13 capability
	/*
	private static void cloneTropicalFishSettings(TropicalFish template, TropicalFish target){
		target.setBodyColor(template.getBodyColor());
		target.setPattern(template.getPattern());
		target.setPatternColor(template.getPatternColor());
	}
	*/
	private static void cloneVehicleSettings(Vehicle template, Vehicle target){
		target.setVelocity(template.getVelocity());
	}
	private static void cloneVillagerSettings(Villager template, Villager target){
		//TODO unlock 1.13 capabilities
		//target.setCareer(template.getCareer(), false);
		target.setProfession(template.getProfession());
		target.setRiches(template.getRiches());
	}
	private static void cloneWitherSkullSettings(WitherSkull template, WitherSkull target){
		target.setCharged(template.isCharged());
	}
	private static void cloneWolfSettings(Wolf template, Wolf target){
		target.setAngry(template.isAngry());
		target.setCollarColor(template.getCollarColor());
	}
	private static void cloneZombieSettings(Zombie template, Zombie target){
		target.setBaby(template.isBaby());
	}
	private static void cloneZombieVillagerSettings(ZombieVillager template, ZombieVillager target){
		target.setVillagerProfession(template.getVillagerProfession());
	}
}
