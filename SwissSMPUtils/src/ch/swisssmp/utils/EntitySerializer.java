package ch.swisssmp.utils;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
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
import org.bukkit.entity.Bee;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Cat;
import org.bukkit.entity.ChestedHorse;
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
import org.bukkit.entity.Fox;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.*;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EntitySerializer {
	protected static JsonObject serialize(Entity entity){
		JsonObject result = new JsonObject();
		result.addProperty("entity_type", entity.getType().toString());
		serializeEntitySettings(entity, result);
		if(entity instanceof AbstractHorse){
			serializeAbstractHorseSettings((AbstractHorse)entity, result);
		}
		if(entity instanceof Ageable){
			serializeAgeableSettings((Ageable)entity,result);
		}
		if(entity instanceof AreaEffectCloud){
			serializeAreaEffectCloudSettings((AreaEffectCloud)entity,result);
		}
		if(entity instanceof ArmorStand){
			serializeArmorStandSettings((ArmorStand)entity, result);
		}
		if(entity instanceof Arrow){
			serializeArrowSettings((Arrow)entity, result);
		}
		if(entity instanceof Attributable){
			serializeAttributeSettings((Attributable)entity, result);
		}
		if(entity instanceof Bat){
			serializeBatSettings((Bat)entity, result);
		}
		if(entity instanceof Bee){
			serializeBeeSettings((Bee)entity, result);
		}
		if(entity instanceof Boat){
			serializeBoatSettings((Boat)entity, result);
		}
		if(entity instanceof Cat){
			serializeCatSettings((Cat)entity, result);
		}
		if(entity instanceof ChestedHorse){
			serializeChestedHorseSettings((ChestedHorse)entity, result);
		}
		if(entity instanceof Colorable){
			serializeColorableSettings((Colorable)entity, result);
		}
		if(entity instanceof CommandMinecart){
			serializeCommandMinecartSettings((CommandMinecart)entity, result);
		}
		if(entity instanceof Creeper){
			serializeCreeperSettings((Creeper)entity, result);
		}
		if(entity instanceof Damageable){
			serializeDamageableSettings((Damageable)entity,result);
		}
		if(entity instanceof EnderCrystal){
			serializeEnderCrystalSettings((EnderCrystal)entity,result);
		}
		if(entity instanceof EnderDragon){
			serializeEnderDragonSettings((EnderDragon)entity,result);
		}
		if(entity instanceof Enderman){
			serializeEndermanSettings((Enderman)entity,result);
		}
		if(entity instanceof EnderSignal){
			serializeEnderSignalSettings((EnderSignal)entity,result);
		}
		if(entity instanceof ExperienceOrb){
			serializeExperienceOrbSettings((ExperienceOrb)entity,result);
		}
		if(entity instanceof Explosive){
			serializeExplosiveSettings((Explosive)entity,result);
		}
		if(entity instanceof FallingBlock){
			serializeFallingBlockSettings((FallingBlock)entity,result);
		}
		if(entity instanceof Fireball){
			serializeFireballSettings((Fireball)entity,result);
		}
		if(entity instanceof Firework){
			Bukkit.getLogger().info("[EntityUtil] Serialization of Fireworks is not supported.");
		}
		if(entity instanceof Fox){
			serializeFoxSettings((Fox)entity,result);
		}
		if(entity instanceof Hanging){
			serializeHangingSettings((Hanging)entity,result);
		}
		if(entity instanceof HopperMinecart){
			serializeHopperMinecartSettings((HopperMinecart)entity,result);
		}
		if(entity instanceof Horse){
			serializeHorseSettings((Horse)entity,result);
		}
		if(entity instanceof HumanEntity){
			serializeHumanEntitySettings((HumanEntity)entity,result);
		}
		if(entity instanceof InventoryHolder){
			serializeInventoryHolderSettings((InventoryHolder)entity, result);
		}
		if(entity instanceof IronGolem){
			serializeIronGolemSettings((IronGolem)entity, result);
		}
		if(entity instanceof Item){
			serializeItemSettings((Item)entity, result);
		}
		if(entity instanceof ItemFrame){
			serializeItemFrameSettings((ItemFrame)entity, result);
		}
		if(entity instanceof LivingEntity){
			serializeLivingEntitySettings((LivingEntity)entity, result);
		}
		if(entity instanceof Llama){
			serializeLlamaSettings((Llama)entity, result);
		}
		if(entity instanceof Merchant){
			serializeMerchantSettings((Merchant)entity, result);
		}
		if(entity instanceof Minecart){
			serializeMinecartSettings((Minecart)entity, result);
		}
		if(entity instanceof Nameable){
			serializeNameableSettings((Nameable)entity, result);
		}
		if(entity instanceof Painting){
			serializePaintingSettings((Painting)entity, result);
		}
		if(entity instanceof Panda){
			serializePandaSettings((Panda)entity, result);
		}
		if(entity instanceof Parrot){
			serializeParrotSettings((Parrot)entity, result);
		}
		if(entity instanceof Phantom){
			serializePhantomSettings((Phantom)entity, result);
		}
		if(entity instanceof Pig){
			serializePigSettings((Pig)entity, result);
		}
		if(entity instanceof PigZombie){
			serializePigZombieSettings((PigZombie)entity, result);
		}
		if(entity instanceof Projectile){
			serializeProjectileSettings((Projectile)entity, result);
		}
		if(entity instanceof PufferFish){
			serializePufferFishSettings((PufferFish)entity, result);
		}
		if(entity instanceof Rabbit){
			serializeRabbitSettings((Rabbit)entity, result);
		}
		if(entity instanceof Raider){
			serializeRaiderSettings((Raider)entity, result);
		}
		if(entity instanceof Sheep){
			serializeSheepSettings((Sheep)entity, result);
		}
		if(entity instanceof ShulkerBullet){
			Bukkit.getLogger().info("[EntityUtil] Serialization of ShulkerBullet is not supported.");
		}
		if(entity instanceof Slime){
			serializeSlimeSettings((Slime)entity, result);
		}
		if(entity instanceof Snowman){
			serializeSnowmanSettings((Snowman)entity, result);
		}
		if(entity instanceof SpectralArrow){
			serializeSpectralArrowSettings((SpectralArrow)entity, result);
		}
		if(entity instanceof Spellcaster){
			serializeSpellcasterSettings((Spellcaster)entity, result);
		}
		if(entity instanceof Tameable){
			serializeTameableSettings((Tameable)entity, result);
		}
		if(entity instanceof ThrownPotion){
			serializeThrownPotionSettings((ThrownPotion)entity, result);
		}
		if(entity instanceof TNTPrimed){
			serializeTNTPrimedSettings((TNTPrimed)entity, result);
		}
		if(entity instanceof TropicalFish){
			serializeTropicalFishSettings((TropicalFish)entity, result);
		}
		if(entity instanceof Vehicle){
			serializeVehicleSettings((Vehicle)entity, result);
		}
		if(entity instanceof Villager){
			serializeVillagerSettings((Villager)entity, result);
		}
		if(entity instanceof WitherSkull){
			serializeWitherSkullSettings((WitherSkull)entity, result);
		}
		if(entity instanceof Wolf){
			serializeWolfSettings((Wolf)entity, result);
		}
		if(entity instanceof Zombie){
			serializeZombieSettings((Zombie)entity, result);
		}
		if(entity instanceof ZombieVillager){
			serializeZombieVillagerSettings((ZombieVillager)entity, result);
		}
		if(entity.getPassengers().size()>0) {
			JsonArray passengersSection = new JsonArray();
			for(Entity passenger : entity.getPassengers()){
				JsonObject passengerSection = serialize(passenger);
				if(passengerSection==null)continue;
				passengersSection.add(passengerSection);
			}
			result.add("passengers", passengersSection);
		}
		return result;
	}
	private static void serializeEntitySettings(Entity entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("cnv", entity.isCustomNameVisible());
		section.addProperty("ft", entity.getFireTicks());
		section.addProperty("gl", entity.isGlowing());
		section.addProperty("gr", entity.hasGravity());
		section.addProperty("inv", entity.isInvulnerable());
		section.addProperty("si", entity.isSilent());
		data.add("entity", section);
	}
	private static void serializeAbstractHorseSettings(AbstractHorse entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("d", entity.getDomestication());
		section.addProperty("js", entity.getJumpStrength());
		section.addProperty("md", entity.getMaxDomestication());
		data.add("abstract_horse", section);
	}
	private static void serializeAgeableSettings(Ageable entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("ad", entity.isAdult());
		section.addProperty("a", entity.getAge());
		section.addProperty("al", entity.getAgeLock());
		section.addProperty("cb", entity.canBreed());
		data.add("ageable", section);
	}
	private static void serializeAreaEffectCloudSettings(AreaEffectCloud entity, JsonObject data){
		JsonObject section = new JsonObject();
		if(entity.hasCustomEffects()) {
			JsonArray effectsSection = new JsonArray();
			for(PotionEffect potionEffect : entity.getCustomEffects()){
				JsonObject effectSection = serializePotionEffect(potionEffect);
				effectsSection.add(effectSection);
			}
			section.add("ce", effectsSection);
		}
		section.add("bpd", serializePotionData(entity.getBasePotionData()));
		section.addProperty("c", entity.getColor().asRGB());
		section.addProperty("d", entity.getDuration());
		section.addProperty("dou", entity.getDurationOnUse());
		section.addProperty("p", entity.getParticle().toString());
		section.addProperty("r", entity.getRadius());
		section.addProperty("rou", entity.getRadiusOnUse());
		section.addProperty("rpt", entity.getRadiusPerTick());
		section.addProperty("rad", entity.getReapplicationDelay());
		section.addProperty("wt", entity.getWaitTime());
		data.add("area_effect_cloud", section);
	}
	private static JsonObject serializePotionEffect(PotionEffect potionEffect) {
		JsonObject result = new JsonObject();
		result.addProperty("t", potionEffect.getType().getName());
		result.addProperty("a", potionEffect.getAmplifier());
		result.addProperty("d", potionEffect.getDuration());
		result.addProperty("p", potionEffect.hasParticles());
		result.addProperty("am", potionEffect.isAmbient());
		result.addProperty("i", potionEffect.hasIcon());
		return result;
	}
	private static JsonObject serializePotionData(PotionData potionData) {
		JsonObject result = new JsonObject();
		result.addProperty("t", potionData.getType().toString());
		result.addProperty("e", potionData.isExtended());
		result.addProperty("u", potionData.isUpgraded());
		return result;
	}
	private static void serializeArmorStandSettings(ArmorStand entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.add("bp", VectorUtil.serialize(entity.getBodyPose()));
		section.add("hp", VectorUtil.serialize(entity.getHeadPose()));
		section.add("lap", VectorUtil.serialize(entity.getLeftArmPose()));
		section.add("llp", VectorUtil.serialize(entity.getLeftLegPose()));
		section.add("rap", VectorUtil.serialize(entity.getRightArmPose()));
		section.add("rlp", VectorUtil.serialize(entity.getRightLegPose()));
		section.addProperty("m", entity.isMarker());
		section.addProperty("s", entity.isSmall());
		section.addProperty("v", entity.isVisible());
		serializeEntityEquipment(entity.getEquipment(), data);
		data.add("armor_stand", section);
	}
	private static void serializeArrowSettings(Arrow entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("cr", entity.isCritical());
		section.addProperty("c", entity.getColor().toString());
		section.addProperty("kns", entity.getKnockbackStrength());
		section.addProperty("ps", entity.getPickupStatus().toString());
		PotionData potionData = entity.getBasePotionData();
		if(potionData!=null) {
			section.add("bpd", serializePotionData(entity.getBasePotionData()));
		}
		if(entity.hasCustomEffects()) {
			List<PotionEffect> potionEffects = entity.getCustomEffects();
			JsonArray customEffectsSection = new JsonArray();
			for(PotionEffect potionEffect : potionEffects) {
				customEffectsSection.add(serializePotionEffect(potionEffect));
			}
			section.add("ce", customEffectsSection);
		}
		
		data.add("arrow", section);
	}
	private static void serializeAttributeSettings(Attributable entity, JsonObject data){
		JsonObject section = new JsonObject();
		for(Attribute attribute : Attribute.values()){
			AttributeInstance entityAttribute = entity.getAttribute(attribute);
			if(entityAttribute==null) continue;
			JsonObject attributeSection = new JsonObject();
			attributeSection.addProperty("bv", entityAttribute.getBaseValue());
			Collection<AttributeModifier> modifiers = entityAttribute.getModifiers();
			if(modifiers.size()>0) {
				JsonArray modifiersSection = new JsonArray();
				for(AttributeModifier modifier : modifiers){
					JsonObject modifierSection = new JsonObject();
					modifierSection.addProperty("n", modifier.getName());
					modifierSection.addProperty("a", modifier.getAmount());
					modifierSection.addProperty("o", modifier.getOperation().toString());
					modifiersSection.add(modifierSection);
				}
				attributeSection.add("m", modifiersSection);
			}
			section.add(attribute.toString(), attributeSection);
		}
		data.add("attributable", section);
	}
	private static void serializeBatSettings(Bat entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("a", entity.isAwake());
		data.add("bat", section);
	}
	private static void serializeBeeSettings(Bee entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("a", entity.getAnger());
		section.addProperty("ceht", entity.getCannotEnterHiveTicks());
		section.addProperty("n", entity.hasNectar());
		section.addProperty("s", entity.hasStung());
		data.add("bee", section);
	}
	private static void serializeBoatSettings(Boat entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("wt", entity.getWoodType().toString());
		data.add("boat", section);
	}
	private static void serializeChestedHorseSettings(ChestedHorse entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("cc", entity.isCarryingChest());
		data.add("chested_horse", section);
	}
	private static void serializeColorableSettings(Colorable entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("c", entity.getColor().toString());
		data.add("colorable", section);
	}
	private static void serializeCommandMinecartSettings(CommandMinecart entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("c", entity.getCommand());
		section.addProperty("n", entity.getName());
		data.add("command_minecart", section);
	}
	private static void serializeCreeperSettings(Creeper entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("p", entity.isPowered());
		section.addProperty("er", entity.getExplosionRadius());
		section.addProperty("mft", entity.getMaxFuseTicks());
		data.add("creeper", section);
	}
	private static void serializeDamageableSettings(Damageable entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("h", entity.getHealth());
		data.add("damageable", section);
	}
	private static void serializeEnderCrystalSettings(EnderCrystal entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("sb", entity.isShowingBottom());
		data.add("ender_crystal", section);
	}
	private static void serializeEnderDragonSettings(EnderDragon entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("ph", entity.getPhase().toString());
		data.add("ender_dragon", section);
	}
	private static void serializeEndermanSettings(Enderman entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("cm", entity.getCarriedBlock().getAsString());
		data.add("enderman", section);
	}
	private static void serializeEnderSignalSettings(EnderSignal entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("dt", entity.getDespawnTimer());
		section.addProperty("di", entity.getDropItem());
		data.add("ender_signal", section);
	}
	private static void serializeEntityEquipment(EntityEquipment entity, JsonObject data){
		JsonObject section = new JsonObject();
		if(entity.getBoots()!=null) section.addProperty("b", ItemUtil.serialize(entity.getBoots()));
		if(entity.getChestplate()!=null) section.addProperty("cp", ItemUtil.serialize(entity.getChestplate()));
		if(entity.getHelmet()!=null) section.addProperty("h", ItemUtil.serialize(entity.getHelmet()));
		if(entity.getItemInMainHand()!=null) section.addProperty("imh", ItemUtil.serialize(entity.getItemInMainHand()));
		if(entity.getItemInOffHand()!=null) section.addProperty("ioh", ItemUtil.serialize(entity.getItemInOffHand()));
		if(entity.getLeggings()!=null) section.addProperty("l", ItemUtil.serialize(entity.getLeggings()));
		if(entity.getHolder() instanceof Monster){
			section.addProperty("bdc", entity.getBootsDropChance());
			section.addProperty("cpdc", entity.getChestplateDropChance());
			section.addProperty("hdc", entity.getHelmetDropChance());
			section.addProperty("imhdc", entity.getItemInMainHandDropChance());
			section.addProperty("iohdc", entity.getItemInOffHandDropChance());
			section.addProperty("ldc", entity.getLeggingsDropChance());
		}
		data.add("entity_equipment", section);
	}
	private static void serializeExperienceOrbSettings(ExperienceOrb entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("e", entity.getExperience());
		data.add("experience_orb", section);
	}
	private static void serializeExplosiveSettings(Explosive entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("i", entity.isIncendiary());
		section.addProperty("y", entity.getYield());
		data.add("explosive", section);
	}
	private static void serializeFallingBlockSettings(FallingBlock entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("he", entity.canHurtEntities());
		section.addProperty("di", entity.getDropItem());
		data.add("falling_block", section);
	}
	private static void serializeFireballSettings(Fireball entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.add("f", VectorUtil.serialize(entity.getDirection()));
		data.add("fireball", section);
	}
	private static void serializeFoxSettings(Fox entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("t", entity.getFoxType().toString());
		section.addProperty("c", entity.isCrouching());
		if(entity.getFirstTrustedPlayer()!=null) section.addProperty("ftp", entity.getFirstTrustedPlayer().getUniqueId().toString());
		if(entity.getSecondTrustedPlayer()!=null) section.addProperty("stp", entity.getSecondTrustedPlayer().getUniqueId().toString());
		data.add("fox", section);
	}
	private static void serializeHangingSettings(Hanging entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("f", entity.getFacing().toString());
		data.add("hanging", section);
	}
	private static void serializeHopperMinecartSettings(HopperMinecart entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("e", entity.isEnabled());
		data.add("hopper_minecart", section);
	}
	private static void serializeHorseSettings(Horse entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("c", entity.getColor().toString());
		section.addProperty("s", entity.getStyle().toString());
		section.add("i", serializeHorseInventory(entity.getInventory()));
		data.add("horse", section);
	}
	private static JsonObject serializeHorseInventory(HorseInventory inventory){
		JsonObject section = new JsonObject();
		if(inventory.getSaddle()!=null) section.addProperty("s", ItemUtil.serialize(inventory.getSaddle()));
		if(inventory.getArmor()!=null) section.addProperty("a", ItemUtil.serialize(inventory.getArmor()));
		return section;
	}
	private static void serializeHumanEntitySettings(HumanEntity entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("gm", entity.getGameMode().toString());
		data.add("human_entity", section);
	}
	private static void serializeInventoryHolderSettings(InventoryHolder entity, JsonObject data){
		JsonObject section = new JsonObject();
		data.add("i", InventoryUtil.serialize(entity.getInventory()));
		data.add("inventory_holder", section);
	}
	private static void serializeIronGolemSettings(IronGolem entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("pc", entity.isPlayerCreated());
		data.add("iron_golem", section);
	}
	private static void serializeItemSettings(Item entity, JsonObject data){
		if(entity.getItemStack()==null) return;
		JsonObject section = new JsonObject();
		section.addProperty("i", ItemUtil.serialize(entity.getItemStack()));
		section.addProperty("pd", entity.getPickupDelay());
		data.add("item", section);
	}
	private static void serializeItemFrameSettings(ItemFrame entity, JsonObject data){
		JsonObject section = new JsonObject();
		if(entity.getItem()!=null) {
			section.addProperty("i", ItemUtil.serialize(entity.getItem()));
		}
		section.addProperty("r", entity.getRotation().toString());
		data.add("item_frame", section);
	}
	private static void serializeLivingEntitySettings(LivingEntity entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("ai", entity.hasAI());
		section.addProperty("cpi", entity.getCanPickupItems());
		section.addProperty("c", entity.isCollidable());
		section.addProperty("g", entity.isGliding());
		section.addProperty("ma", entity.getMaximumAir());
		section.addProperty("mndt", entity.getMaximumNoDamageTicks());
		section.addProperty("rwfa", entity.getRemoveWhenFarAway());
		Collection<PotionEffect> potionEffects = entity.getActivePotionEffects();
		if(potionEffects.size()>0) {
			JsonArray potionEffectsSection = new JsonArray();
			for(PotionEffect potionEffect : potionEffects) {
				JsonObject potionSection = serializePotionEffect(potionEffect);
				potionEffectsSection.add(potionSection);
			}
			section.add("ape", potionEffectsSection);
		}
		serializeEntityEquipment(entity.getEquipment(), data);
		data.add("living_entity", section);
	}
	private static void serializeLlamaSettings(Llama entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("c", entity.getColor().toString());
		section.addProperty("s", entity.getStrength());
		data.add("llama", section);
	}
	private static void serializeMerchantSettings(Merchant entity, JsonObject data){
		JsonObject section = new JsonObject();
		Collection<MerchantRecipe> recipes = entity.getRecipes();
		if(recipes.size()>0) {
			JsonArray recipesSection = new JsonArray();
			for(MerchantRecipe recipe : recipes) {
				JsonObject recipeSection = serializeMerchantRecipe(recipe);
				recipesSection.add(recipeSection);
			}
			section.add("recipes", recipesSection);
		}
		data.add("merchant", section);
	}
	private static JsonObject serializeMerchantRecipe(MerchantRecipe recipe) {
		JsonObject result = new JsonObject();
		List<ItemStack> ingredients = recipe.getIngredients();
		if(ingredients.size()>0) {
			JsonArray ingredientsSection = new JsonArray();
			for(ItemStack itemStack : ingredients) {
				if(itemStack==null) continue;
				ingredientsSection.add(ItemUtil.serialize(itemStack));
			}
			result.add("i", ingredientsSection);
		}
		result.addProperty("mu", recipe.getMaxUses());
		result.addProperty("pm", recipe.getPriceMultiplier());
		result.addProperty("u", recipe.getUses());
		result.addProperty("ve", recipe.getVillagerExperience());
		result.addProperty("er", recipe.hasExperienceReward());
		result.addProperty("r", ItemUtil.serialize(recipe.getResult()));
		return result;
	}
	private static void serializeMinecartSettings(Minecart entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("d", entity.getDamage());
		section.add("dvm", VectorUtil.serialize(entity.getDerailedVelocityMod()));
		section.addProperty("db", entity.getDisplayBlockData().getAsString());
		section.addProperty("dbo", entity.getDisplayBlockOffset());
		section.add("fvm", VectorUtil.serialize(entity.getFlyingVelocityMod()));
		section.addProperty("ms", entity.getMaxSpeed());
		section.addProperty("swe", entity.isSlowWhenEmpty());
		data.add("minecart", section);
	}
	private static void serializeNameableSettings(Nameable entity, JsonObject data){
		JsonObject section = new JsonObject();
		if(entity.getCustomName()!=null) section.addProperty("n", entity.getCustomName());
		data.add("nameable", section);
	}
	private static void serializeCatSettings(Cat entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("t", entity.getCatType().toString());
		section.addProperty("cc", entity.getCollarColor().toString());
		data.add("cat", section);
	}
	private static void serializePaintingSettings(Painting entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("a", entity.getArt().toString());
		data.add("painting", section);
	}
	private static void serializePandaSettings(Panda entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("mg", entity.getMainGene().toString());
		section.addProperty("hg", entity.getHiddenGene().toString());
		data.add("panda", section);
	}
	private static void serializeParrotSettings(Parrot entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("v", entity.getVariant().toString());
		data.add("parrot", section);
	}
	private static void serializePhantomSettings(Phantom entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("s", entity.getSize());
		data.add("phantom", section);
	}
	private static void serializePigSettings(Pig entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("s", entity.hasSaddle());
		data.add("pig", section);
	}
	private static void serializePigZombieSettings(PigZombie entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("a", entity.getAnger());
		data.add("pig_zombie", section);
	}
	private static void serializeProjectileSettings(Projectile entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("b", entity.doesBounce());
		data.add("projectile", section);
	}
	private static void serializePufferFishSettings(PufferFish entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("p", entity.getPuffState());
		data.add("puffer_fish", section);
	}
	private static void serializeRabbitSettings(Rabbit entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("t", entity.getRabbitType().toString());
		data.add("rabbit", section);
	}
	private static void serializeRaiderSettings(Raider entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("cjr", entity.isCanJoinRaid());
		section.addProperty("pl", entity.isPatrolLeader());
		data.add("rabbit", section);
	}
	private static void serializeSheepSettings(Sheep entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("s", entity.isSheared());
		data.add("sheep", section);
	}
	private static void serializeSlimeSettings(Slime entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("s", entity.getSize());
		data.add("slime", section);
	}
	private static void serializeSnowmanSettings(Snowman entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("d", entity.isDerp());
		data.add("snowman", section);
	}
	private static void serializeSpectralArrowSettings(SpectralArrow entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("gt", entity.getGlowingTicks());
		data.add("spectral_arrow", section);
	}
	private static void serializeSpellcasterSettings(Spellcaster entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("s", entity.getSpell().toString());
		data.add("spellcaster", section);
	}
	private static void serializeTameableSettings(Tameable entity, JsonObject data){
		JsonObject section = new JsonObject();
		if(entity.getOwner()!=null) section.addProperty("o", entity.getOwner().getUniqueId().toString());
		section.addProperty("t", entity.isTamed());
		data.add("tameable", section);
	}
	private static void serializeThrownPotionSettings(ThrownPotion entity, JsonObject data){
		JsonObject section = new JsonObject();
		if(entity.getItem()!=null) section.addProperty("i", ItemUtil.serialize(entity.getItem()));
		data.add("thrown_potion", section);
	}
	private static void serializeTNTPrimedSettings(TNTPrimed entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("ft", entity.getFuseTicks());
		data.add("tnt_primed", section);
	}
	private static void serializeTropicalFishSettings(TropicalFish entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("bc", entity.getBodyColor().toString());
		section.addProperty("p", entity.getPattern().toString());
		section.addProperty("pc", entity.getPatternColor().toString());
		data.add("tropical_fish", section);
	}
	private static void serializeVehicleSettings(Vehicle entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.add("v", VectorUtil.serialize(entity.getVelocity()));
		data.add("vehicle", section);
	}
	private static void serializeVillagerSettings(Villager entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("p", entity.getProfession().toString());
		section.addProperty("t", entity.getVillagerType().toString());
		section.addProperty("l", entity.getVillagerLevel());
		section.addProperty("e", entity.getVillagerExperience());
		data.add("villager", section);
	}
	private static void serializeWitherSkullSettings(WitherSkull entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("c", entity.isCharged());
		data.add("wither_skull", section);
	}
	private static void serializeWolfSettings(Wolf entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("a", entity.isAngry());
		section.addProperty("cc", entity.getCollarColor().toString());
		data.add("wolf", section);
	}
	private static void serializeZombieSettings(Zombie entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("b", entity.isBaby());
		data.add("zombie", section);
	}
	private static void serializeZombieVillagerSettings(ZombieVillager entity, JsonObject data){
		JsonObject section = new JsonObject();
		section.addProperty("vp", entity.getVillagerProfession().toString());
		data.add("zombie_villager", section);
	}
}
