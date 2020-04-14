package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.Particle;
import org.bukkit.Rotation;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
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
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EntityDeserializer {
	protected static Entity deserialize(Location location, JsonObject data) {
		Entity entity = spawn(location, data);
		if(entity instanceof Hanging){
			location.setX(Mathf.floorToInt(location.getX()));
			location.setY(Mathf.floorToInt(location.getY()));
			location.setZ(Mathf.floorToInt(location.getZ()));
			location.setYaw(0);
			location.setPitch(0);
		}
		deserializeEntitySettings(entity, data);
		if(entity instanceof AbstractHorse){
			deserializeAbstractHorseSettings((AbstractHorse)entity, data);
		}
		if(entity instanceof Ageable){
			deserializeAgeableSettings((Ageable)entity,data);
		}
		if(entity instanceof AreaEffectCloud){
			deserializeAreaEffectCloudSettings((AreaEffectCloud)entity,data);
		}
		if(entity instanceof ArmorStand){
			deserializeArmorStandSettings((ArmorStand)entity, data);
		}
		if(entity instanceof Arrow){
			deserializeArrowSettings((Arrow)entity, data);
		}
		if(entity instanceof Attributable){
			deserializeAttributeSettings((Attributable)entity, data);
		}
		if(entity instanceof Bat){
			deserializeBatSettings((Bat)entity, data);
		}
		if(entity instanceof Bee){
			deserializeBeeSettings((Bee)entity, data);
		}
		if(entity instanceof Boat){
			deserializeBoatSettings((Boat)entity, data);
		}
		if(entity instanceof Cat){
			deserializeCatSettings((Cat)entity, data);
		}
		if(entity instanceof ChestedHorse){
			deserializeChestedHorseSettings((ChestedHorse)entity, data);
		}
		if(entity instanceof Colorable){
			deserializeColorableSettings((Colorable)entity, data);
		}
		if(entity instanceof CommandMinecart){
			deserializeCommandMinecartSettings((CommandMinecart)entity, data);
		}
		if(entity instanceof Creeper){
			deserializeCreeperSettings((Creeper)entity, data);
		}
		if(entity instanceof Damageable){
			deserializeDamageableSettings((Damageable)entity,data);
		}
		if(entity instanceof EnderCrystal){
			deserializeEnderCrystalSettings((EnderCrystal)entity,data);
		}
		if(entity instanceof EnderDragon){
			deserializeEnderDragonSettings((EnderDragon)entity,data);
		}
		if(entity instanceof Enderman){
			deserializeEndermanSettings((Enderman)entity,data);
		}
		if(entity instanceof EnderSignal){
			deserializeEnderSignalSettings((EnderSignal)entity,data);
		}
		if(entity instanceof ExperienceOrb){
			deserializeExperienceOrbSettings((ExperienceOrb)entity,data);
		}
		if(entity instanceof Explosive){
			deserializeExplosiveSettings((Explosive)entity,data);
		}
		if(entity instanceof FallingBlock){
			deserializeFallingBlockSettings((FallingBlock)entity,data);
		}
		if(entity instanceof Fireball){
			deserializeFireballSettings((Fireball)entity,data);
		}
		if(entity instanceof Firework){
			Bukkit.getLogger().info("[EntityUtil] Deserialization of Fireworks is not supported.");
		}
		if(entity instanceof Fox){
			deserializeFoxSettings((Fox)entity,data);
		}
		if(entity instanceof Hanging){
			deserializeHangingSettings((Hanging)entity,data);
		}
		if(entity instanceof HopperMinecart){
			deserializeHopperMinecartSettings((HopperMinecart)entity,data);
		}
		if(entity instanceof Horse){
			deserializeHorseSettings((Horse)entity,data);
		}
		if(entity instanceof HumanEntity){
			deserializeHumanEntitySettings((HumanEntity)entity,data);
		}
		if(entity instanceof InventoryHolder){
			deserializeInventoryHolderSettings((InventoryHolder)entity, data);
		}
		if(entity instanceof IronGolem){
			deserializeIronGolemSettings((IronGolem)entity, data);
		}
		if(entity instanceof Item){
			deserializeItemSettings((Item)entity, data);
		}
		if(entity instanceof ItemFrame){
			deserializeItemFrameSettings((ItemFrame)entity, data);
		}
		if(entity instanceof LivingEntity){
			deserializeLivingEntitySettings((LivingEntity)entity, data);
		}
		if(entity instanceof Llama){
			deserializeLlamaSettings((Llama)entity, data);
		}
		if(entity instanceof Merchant){
			deserializeMerchantSettings((Merchant)entity, data);
		}
		if(entity instanceof Minecart){
			deserializeMinecartSettings((Minecart)entity, data);
		}
		if(entity instanceof Nameable){
			deserializeNameableSettings((Nameable)entity, data);
		}
		if(entity instanceof Painting){
			deserializePaintingSettings((Painting)entity, data);
		}
		if(entity instanceof Panda){
			deserializePandaSettings((Panda)entity, data);
		}
		if(entity instanceof Parrot){
			deserializeParrotSettings((Parrot)entity, data);
		}
		if(entity instanceof Phantom){
			deserializePhantomSettings((Phantom)entity, data);
		}
		if(entity instanceof Pig){
			deserializePigSettings((Pig)entity, data);
		}
		if(entity instanceof PigZombie){
			deserializePigZombieSettings((PigZombie)entity, data);
		}
		if(entity instanceof Projectile){
			deserializeProjectileSettings((Projectile)entity, data);
		}
		if(entity instanceof PufferFish){
			deserializePufferFishSettings((PufferFish)entity, data);
		}
		if(entity instanceof Rabbit){
			deserializeRabbitSettings((Rabbit)entity, data);
		}
		if(entity instanceof Raider){
			deserializeRaiderSettings((Raider)entity, data);
		}
		if(entity instanceof Sheep){
			deserializeSheepSettings((Sheep)entity, data);
		}
		if(entity instanceof ShulkerBullet){
			Bukkit.getLogger().info("[EntityUtil] Deserialization of ShulkerBullet is not supported.");
		}
		if(entity instanceof Slime){
			deserializeSlimeSettings((Slime)entity, data);
		}
		if(entity instanceof Snowman){
			deserializeSnowmanSettings((Snowman)entity, data);
		}
		if(entity instanceof SpectralArrow){
			deserializeSpectralArrowSettings((SpectralArrow)entity, data);
		}
		if(entity instanceof Spellcaster){
			deserializeSpellcasterSettings((Spellcaster)entity, data);
		}
		if(entity instanceof Tameable){
			deserializeTameableSettings((Tameable)entity, data);
		}
		if(entity instanceof ThrownPotion){
			deserializeThrownPotionSettings((ThrownPotion)entity, data);
		}
		if(entity instanceof TNTPrimed){
			deserializeTNTPrimedSettings((TNTPrimed)entity, data);
		}
		if(entity instanceof TropicalFish){
			deserializeTropicalFishSettings((TropicalFish)entity, data);
		}
		if(entity instanceof Vehicle){
			deserializeVehicleSettings((Vehicle)entity, data);
		}
		if(entity instanceof Villager){
			deserializeVillagerSettings((Villager)entity, data);
		}
		if(entity instanceof WitherSkull){
			deserializeWitherSkullSettings((WitherSkull)entity, data);
		}
		if(entity instanceof Wolf){
			deserializeWolfSettings((Wolf)entity, data);
		}
		if(entity instanceof Zombie){
			deserializeZombieSettings((Zombie)entity, data);
		}
		if(entity instanceof ZombieVillager){
			deserializeZombieVillagerSettings((ZombieVillager)entity, data);
		}
		
		if(data.has("passengers")) {
			JsonArray passengers = data.get("passengers").getAsJsonArray();
			for(JsonElement passengerElement : passengers) {
				JsonObject passengerSection = passengerElement.getAsJsonObject();
				Entity passenger = deserialize(location, passengerSection);
				if(passenger==null) continue;
				entity.addPassenger(passenger);
			}
		}
		return entity;
	}
	
	private static Entity spawn(Location location, JsonObject data) {
		World world = location.getWorld();
		try {
			EntityType entityType = EntityType.valueOf(data.get("entity_type").getAsString());
			if(entityType==EntityType.FALLING_BLOCK) {
				String blockDataString = data.get("block_data").getAsString();
				BlockData blockData = Bukkit.createBlockData(blockDataString);
				return world.spawnFallingBlock(location, blockData);
			}
			if(entityType==EntityType.DROPPED_ITEM) {
				JsonObject itemSection = data.get("item").getAsJsonObject();
				String itemStackString = itemSection.get("i").getAsString();
				ItemStack itemStack = ItemUtil.deserialize(itemStackString);
				return world.dropItem(location, itemStack);
			}
			if(entityType==EntityType.PLAYER) {
				return null;
			}
			return world.spawnEntity(location, entityType);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private static void deserializeEntitySettings(Entity entity, JsonObject data){
		String key = "abstract_horse";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		if(section.has("cnv")) entity.setCustomNameVisible(section.get("cnv").getAsBoolean());
		if(section.has("ft")) entity.setFireTicks(section.get("ft").getAsInt());
		if(section.has("gl")) entity.setGlowing(section.get("gl").getAsBoolean());
		if(section.has("gr")) entity.setGravity(section.get("gr").getAsBoolean());
		if(section.has("inv")) entity.setInvulnerable(section.get("inv").getAsBoolean());
		if(section.has("si")) entity.setSilent(section.get("si").getAsBoolean());
		
	}
	private static void deserializeAbstractHorseSettings(AbstractHorse entity, JsonObject data){
		String key = "abstract_horse";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		if(section.has("d")) entity.setDomestication(section.get("d").getAsInt());
		if(section.has("js")) entity.setJumpStrength(section.get("js").getAsInt());
		if(section.has("md")) entity.setMaxDomestication(section.get("md").getAsInt());
	}
	private static void deserializeAgeableSettings(Ageable entity, JsonObject data){
		String key = "ageable";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		if(section.has("ad")) {
			if(section.get("ad").getAsBoolean()) entity.setAdult();
			else entity.setBaby();
		}
		if(section.has("a")) entity.setAge(section.get("a").getAsInt());
		if(section.has("al")) entity.setAgeLock(section.get("al").getAsBoolean());
		if(section.has("cb")) entity.setBreed(section.get("cb").getAsBoolean());
	}
	private static void deserializeAreaEffectCloudSettings(AreaEffectCloud entity, JsonObject data){
		String key = "area_effect_cloud";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		if(section.has("ce") && section.get("ce").isJsonArray()) {
			JsonArray effectsSection = section.get("ce").getAsJsonArray();
			for(JsonElement entry : effectsSection){
				if(!entry.isJsonObject()) continue;
				PotionEffect effect = deserializePotionEffect(entry.getAsJsonObject());
				entity.addCustomEffect(effect, true);
			}
		}
		if(section.has("bpd") && section.get("bpd").isJsonObject()) entity.setBasePotionData(deserializePotionData(section.get("bpd").getAsJsonObject()));
		if(section.has("c")) entity.setColor(Color.fromRGB(section.get("c").getAsInt()));
		if(section.has("d")) entity.setDuration(section.get("d").getAsInt());
		if(section.has("dou")) entity.setDurationOnUse(section.get("dou").getAsInt());
		if(section.has("p")) {
			try {
				entity.setParticle(Particle.valueOf(section.get("p").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("r")) entity.setRadius(section.get("r").getAsInt());
		if(section.has("rou")) entity.setRadiusOnUse(section.get("rou").getAsInt());
		if(section.has("rpt")) entity.setRadiusPerTick(section.get("rpt").getAsInt());
		if(section.has("rad")) entity.setReapplicationDelay(section.get("rad").getAsInt());
		if(section.has("wt")) entity.setWaitTime(section.get("wt").getAsInt());
	}
	private static PotionEffect deserializePotionEffect(JsonObject data) {
		PotionEffectType effectType = PotionEffectType.getByName(data.get("t").getAsString());
		int amplifier = data.get("a").getAsInt();
		int duration = data.get("d").getAsInt();
		boolean particles = data.get("p").getAsBoolean();
		boolean ambient = data.get("am").getAsBoolean();
		boolean icon = data.get("i").getAsBoolean();
		
		return new PotionEffect(effectType, duration, amplifier, ambient, particles, icon);
	}
	private static PotionData deserializePotionData(JsonObject data) {
		PotionType type = PotionType.valueOf(data.get("t").getAsString());
		boolean extended = data.get("e").getAsBoolean();
		boolean upgraded = data.get("u").getAsBoolean();
		return new PotionData(type, extended, upgraded);
	}
	private static void deserializeArmorStandSettings(ArmorStand entity, JsonObject data){
		String key = "armor_stand";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		if(section.has("bp")) entity.setBodyPose(VectorUtil.deserializeEuler(section.get("bp").getAsJsonObject()));
		if(section.has("hp")) entity.setHeadPose(VectorUtil.deserializeEuler(section.get("hp").getAsJsonObject()));
		if(section.has("lap")) entity.setLeftArmPose(VectorUtil.deserializeEuler(section.get("lap").getAsJsonObject()));
		if(section.has("llp")) entity.setLeftLegPose(VectorUtil.deserializeEuler(section.get("llp").getAsJsonObject()));
		if(section.has("rap")) entity.setRightArmPose(VectorUtil.deserializeEuler(section.get("rap").getAsJsonObject()));
		if(section.has("rlp")) entity.setRightLegPose(VectorUtil.deserializeEuler(section.get("rlp").getAsJsonObject()));
		
		if(section.has("m")) entity.setMarker(section.get("m").getAsBoolean());
		if(section.has("s")) entity.setSmall(section.get("s").getAsBoolean());
		if(section.has("v")) entity.setVisible(section.get("v").getAsBoolean());

		deserializeEntityEquipment(entity.getEquipment(), data);
	}
	private static void deserializeArrowSettings(Arrow entity, JsonObject data){
		String key = "arrow";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("cr")) entity.setCritical(section.get("cr").getAsBoolean());
		if(section.has("c")) entity.setColor(Color.fromRGB(section.get("c").getAsInt()));
		if(section.has("kns")) entity.setKnockbackStrength(section.get("kns").getAsInt());
		if(section.has("ps")) {
			try {
				entity.setPickupStatus(PickupStatus.valueOf(section.get("ps").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(section.has("bpd") && section.get("bpd").isJsonObject()) {
			PotionData potionData = deserializePotionData(section.get("bpd").getAsJsonObject());
			entity.setBasePotionData(potionData);
		}
		
		if(section.has("ce") && section.get("ce").isJsonArray()) {
			JsonArray effectsSection = section.get("ce").getAsJsonArray();
			for(JsonElement entry : effectsSection){
				if(!entry.isJsonObject()) continue;
				PotionEffect effect = deserializePotionEffect(entry.getAsJsonObject());
				entity.addCustomEffect(effect, true);
			}
		}
	}
	private static void deserializeAttributeSettings(Attributable entity, JsonObject data){
		String key = "attributable";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		for(Attribute attribute : Attribute.values()){
			if(!section.has(attribute.toString())) continue;
			AttributeInstance entityAttribute = entity.getAttribute(attribute);
			if(entityAttribute==null) continue;
			JsonObject attributeSection = section.get(attribute.toString()).getAsJsonObject();
			if(attributeSection.has("bv")) entityAttribute.setBaseValue(attributeSection.get("bv").getAsDouble());

			if(attributeSection.has("m") && attributeSection.get("m").isJsonArray()) {
				JsonArray modifiersSection = attributeSection.get("m").getAsJsonArray();
				for(JsonElement entry : modifiersSection){
					JsonObject modifierSection = entry.getAsJsonObject();
					String name = modifierSection.get("n").getAsString();
					double amount = modifierSection.get("a").getAsDouble();
					Operation operation;
					try {
						operation = Operation.valueOf(modifierSection.get("o").getAsString());
					}
					catch(Exception e) {
						e.printStackTrace();
						continue;
					}
					AttributeModifier modifier = new AttributeModifier(name, amount, operation);
					entityAttribute.addModifier(modifier);
				}
				attributeSection.add("m", modifiersSection);
			}
		}
	}
	private static void deserializeBatSettings(Bat entity, JsonObject data){
		String key = "bat";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("a")) entity.setAwake(section.get("a").getAsBoolean());
	}
	private static void deserializeBeeSettings(Bee entity, JsonObject data){
		String key = "bee";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("a")) entity.setAnger(section.get("a").getAsInt());
		if(section.has("ceht")) entity.setCannotEnterHiveTicks(section.get("ceht").getAsInt());
		if(section.has("n")) entity.setHasNectar(section.get("n").getAsBoolean());
		if(section.has("s")) entity.setHasStung(section.get("s").getAsBoolean());
	}
	private static void deserializeBoatSettings(Boat entity, JsonObject data){
		String key = "boat";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("wt")) {
			try{
				entity.setWoodType(TreeSpecies.valueOf(section.get("wt").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeChestedHorseSettings(ChestedHorse entity, JsonObject data){
		String key = "chested_horse";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("cc")) entity.setCarryingChest(section.get("cc").getAsBoolean());
	}
	private static void deserializeColorableSettings(Colorable entity, JsonObject data){
		String key = "colorable";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("c")) {
			try{
				entity.setColor(DyeColor.valueOf(section.get("c").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeCommandMinecartSettings(CommandMinecart entity, JsonObject data){
		String key = "command_minecart";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("c")) entity.setCommand(section.get("c").getAsString());
		if(section.has("n")) entity.setName(section.get("n").getAsString());
	}
	private static void deserializeCreeperSettings(Creeper entity, JsonObject data){
		String key = "creeper";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("p")) entity.setPowered(section.get("p").getAsBoolean());
		if(section.has("er")) entity.setExplosionRadius(section.get("er").getAsInt());
		if(section.has("mft")) entity.setMaxFuseTicks(section.get("mft").getAsInt());
	}
	private static void deserializeDamageableSettings(Damageable entity, JsonObject data){
		String key = "damageable";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("h")) entity.setHealth(section.get("h").getAsInt());
	}
	private static void deserializeEnderCrystalSettings(EnderCrystal entity, JsonObject data){
		String key = "ender_crystal";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("sb")) entity.setShowingBottom(section.get("sb").getAsBoolean());
	}
	private static void deserializeEnderDragonSettings(EnderDragon entity, JsonObject data){
		String key = "ender_dragon";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("ph")) {
			try{
				entity.setPhase(Phase.valueOf(section.get("ph").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeEndermanSettings(Enderman entity, JsonObject data){
		String key = "enderman";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("cm")) entity.setCarriedBlock(Bukkit.createBlockData(section.get("cm").getAsString()));
	}
	private static void deserializeEnderSignalSettings(EnderSignal entity, JsonObject data){
		String key = "ender_signal";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("dt")) entity.setDespawnTimer(section.get("dt").getAsInt());
		if(section.has("di")) entity.setDropItem(section.get("di").getAsBoolean());
	}
	private static void deserializeEntityEquipment(EntityEquipment entity, JsonObject data){
		String key = "entity_equipment";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("b")) entity.setBoots(ItemUtil.deserialize(section.get("b").getAsString()));
		if(section.has("cp")) entity.setChestplate(ItemUtil.deserialize(section.get("cp").getAsString()));
		if(section.has("h")) entity.setHelmet(ItemUtil.deserialize(section.get("h").getAsString()));
		if(section.has("imh")) entity.setItemInMainHand(ItemUtil.deserialize(section.get("imh").getAsString()));
		if(section.has("ioh")) entity.setItemInOffHand(ItemUtil.deserialize(section.get("ioh").getAsString()));
		if(section.has("l")) entity.setLeggings(ItemUtil.deserialize(section.get("l").getAsString()));
		
		if(entity.getHolder() instanceof Monster){
			if(section.has("bdc")) entity.setBootsDropChance(section.get("bdc").getAsFloat());
			if(section.has("cpdc")) entity.setChestplateDropChance(section.get("cpdc").getAsFloat());
			if(section.has("hdc")) entity.setHelmetDropChance(section.get("hdc").getAsFloat());
			if(section.has("imhdc")) entity.setItemInMainHandDropChance(section.get("imhdc").getAsFloat());
			if(section.has("iohdc")) entity.setItemInOffHandDropChance(section.get("iohdc").getAsFloat());
			if(section.has("ldc")) entity.setLeggingsDropChance(section.get("ldc").getAsFloat());
		}
	}
	private static void deserializeExperienceOrbSettings(ExperienceOrb entity, JsonObject data){
		String key = "experience_orb";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("e")) entity.setExperience(section.get("e").getAsInt());
	}
	private static void deserializeExplosiveSettings(Explosive entity, JsonObject data){
		String key = "explosive";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("i")) entity.setIsIncendiary(section.get("i").getAsBoolean());
		if(section.has("y")) entity.setYield(section.get("y").getAsFloat());
	}
	private static void deserializeFallingBlockSettings(FallingBlock entity, JsonObject data){
		String key = "falling_block";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("he")) entity.setHurtEntities(section.get("he").getAsBoolean());
		if(section.has("di")) entity.setDropItem(section.get("di").getAsBoolean());
	}
	private static void deserializeFireballSettings(Fireball entity, JsonObject data){
		String key = "fireball";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("f")) entity.setDirection(VectorUtil.deserializeVector(section.get("f").getAsJsonObject()));
	}
	private static void deserializeFoxSettings(Fox entity, JsonObject data){
		String key = "fox";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("t")) {
			try{
				entity.setFoxType(Fox.Type.valueOf(section.get("t").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(section.has("c")) entity.setCrouching(section.get("c").getAsBoolean());
		
		if(section.has("ftp")) {
			try {
				UUID playerUid = UUID.fromString(section.get("ftp").getAsString());
				AnimalTamer tamer = Bukkit.getOfflinePlayer(playerUid);
				entity.setFirstTrustedPlayer(tamer);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(section.has("stp")) {
			try {
				UUID playerUid = UUID.fromString(section.get("stp").getAsString());
				AnimalTamer tamer = Bukkit.getOfflinePlayer(playerUid);
				entity.setFirstTrustedPlayer(tamer);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeHangingSettings(Hanging entity, JsonObject data){
		String key = "hanging";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("f")) entity.setFacingDirection(BlockFace.valueOf(section.get("f").getAsString()), true);
	}
	private static void deserializeHopperMinecartSettings(HopperMinecart entity, JsonObject data){
		String key = "hopper_minecart";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("e")) entity.setEnabled(section.get("e").getAsBoolean());
	}
	private static void deserializeHorseSettings(Horse entity, JsonObject data){
		String key = "horse";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("c")) entity.setColor(Horse.Color.valueOf(section.get("c").getAsString()));
		if(section.has("s")) entity.setStyle(Horse.Style.valueOf(section.get("s").getAsString()));
	}
	private static void deserializeHumanEntitySettings(HumanEntity entity, JsonObject data){
		String key = "human_entity";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("gm")) entity.setGameMode(GameMode.valueOf(section.get("gm").getAsString()));
	}
	private static void deserializeInventoryHolderSettings(InventoryHolder entity, JsonObject data){
		String key = "inventory_holder";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("i")) {
			InventoryUtil.deserialize(section.get("i").getAsJsonObject(), entity.getInventory());
		}
	}
	private static void deserializeIronGolemSettings(IronGolem entity, JsonObject data){
		String key = "iron_golem";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("pc")) entity.setPlayerCreated(section.get("pc").getAsBoolean());
	}
	private static void deserializeItemSettings(Item entity, JsonObject data){
		String key = "item";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("pd")) entity.setPickupDelay(section.get("pd").getAsInt());
	}
	private static void deserializeItemFrameSettings(ItemFrame entity, JsonObject data){
		String key = "item_frame";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("i")) {
			entity.setItem(ItemUtil.deserialize(section.get("i").getAsString()));		
		}
		if(section.has("r")) {
			try{
				entity.setRotation(Rotation.valueOf(section.get("r").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeLivingEntitySettings(LivingEntity entity, JsonObject data){
		String key = "living_entity";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("ai")) entity.setAI(section.get("ai").getAsBoolean());
		if(section.has("cpi")) entity.setCanPickupItems(section.get("cpi").getAsBoolean());
		if(section.has("c")) entity.setCollidable(section.get("c").getAsBoolean());
		if(section.has("g")) entity.setGliding(section.get("g").getAsBoolean());
		if(section.has("ma")) entity.setMaximumAir(section.get("ma").getAsInt());
		if(section.has("mndt")) entity.setMaximumNoDamageTicks(section.get("mndt").getAsInt());
		if(section.has("rwfa")) entity.setRemoveWhenFarAway(section.get("rwfa").getAsBoolean());
		
		if(section.has("ape") && section.get("ape").isJsonArray()) {
			JsonArray effectsSection = section.get("ape").getAsJsonArray();
			for(JsonElement entry : effectsSection){
				if(!entry.isJsonObject()) continue;
				PotionEffect effect = deserializePotionEffect(entry.getAsJsonObject());
				entity.addPotionEffect(effect);
			}
		}
		
		deserializeEntityEquipment(entity.getEquipment(), data);
	}
	private static void deserializeLlamaSettings(Llama entity, JsonObject data){
		String key = "llama";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("c")) {
			try{
				entity.setColor(Llama.Color.valueOf(section.get("c").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(section.has("s")) entity.setStrength(section.get("s").getAsInt());
	}
	private static void deserializeMerchantSettings(Merchant entity, JsonObject data){
		String key = "merchant";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("recipes") && section.get("recipes").isJsonArray()) {
			JsonArray recipesSection = section.get("recipes").getAsJsonArray();
			List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
			for(JsonElement entry : recipesSection){
				if(!entry.isJsonObject()) continue;
				MerchantRecipe recipe = deserializeMerchantRecipe(entry.getAsJsonObject());
				if(recipe==null) continue;
				recipes.add(recipe);
			}
			
			if(recipes.size()>0) {
				entity.setRecipes(recipes);
			}
			if(recipes.size()<recipesSection.size()) {
				Bukkit.getLogger().info("[EntityUtil] Couldn't load "+(recipesSection.size()-recipes.size())+" recipes");
			}
		}
	}
	private static MerchantRecipe deserializeMerchantRecipe(JsonObject data) {
		JsonArray ingredientsSection = data.get("i").getAsJsonArray();
		List<ItemStack> ingredients = new ArrayList<ItemStack>();
		for(JsonElement entry : ingredientsSection) {
			ItemStack itemStack = ItemUtil.deserialize(entry.getAsString());
			if(itemStack==null) continue;
			ingredients.add(itemStack);
		}
		
		if(ingredients.size()==0) {
			Bukkit.getLogger().info("[EntityUtil] Recipe does not have any ingredients");
			return null;
		}

		ItemStack resultStack = ItemUtil.deserialize(data.get("r").getAsString());
		if(resultStack==null) {
			Bukkit.getLogger().info("[EntityUtil] Recipe does not have a result stack");
			return null;
		}
		
		int uses = data.get("u").getAsInt();
		int maxUses = data.get("mu").getAsInt();
		boolean experienceReward = data.get("er").getAsBoolean();
		int villagerExperience = data.get("ve").getAsInt();
		float priceMultiplier = data.get("pm").getAsFloat();
		
		MerchantRecipe result = new MerchantRecipe(resultStack, uses, maxUses, experienceReward, villagerExperience, priceMultiplier);
		result.setIngredients(ingredients);
		return result;
	}
	private static void deserializeMinecartSettings(Minecart entity, JsonObject data){
		String key = "minecart";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("d")) entity.setDamage(section.get("d").getAsInt());
		if(section.has("dvm")) entity.setDerailedVelocityMod(VectorUtil.deserializeVector(section.get("dvm").getAsJsonObject()));
		if(section.has("db")) entity.setDisplayBlockData(Bukkit.createBlockData(section.get("db").getAsString()));
		if(section.has("dbo")) entity.setDisplayBlockOffset(section.get("dbo").getAsInt());
		if(section.has("fvm")) entity.setFlyingVelocityMod(VectorUtil.deserializeVector(section.get("fvm").getAsJsonObject()));
		if(section.has("ms")) entity.setMaxSpeed(section.get("ms").getAsInt());
		if(section.has("swe")) entity.setSlowWhenEmpty(section.get("swe").getAsBoolean());
	}
	private static void deserializeNameableSettings(Nameable entity, JsonObject data){
		String key = "nameable";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("n")) entity.setCustomName(section.get("n").getAsString());
	}
	private static void deserializeCatSettings(Cat entity, JsonObject data){
		String key = "cat";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("t")) {
			try{
				entity.setCatType(Cat.Type.valueOf(section.get("t").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("cc")) {
			try{
				entity.setCollarColor(DyeColor.valueOf(section.get("cc").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializePaintingSettings(Painting entity, JsonObject data){
		String key = "painting";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("a")) {
			try{
				entity.setArt(Art.valueOf(section.get("a").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializePandaSettings(Panda entity, JsonObject data){
		String key = "panda";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("mg")) {
			try{
				entity.setMainGene(Panda.Gene.valueOf(section.get("mg").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("hg")) {
			try{
				entity.setHiddenGene(Panda.Gene.valueOf(section.get("hg").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeParrotSettings(Parrot entity, JsonObject data){
		String key = "parrot";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("v")) {
			try{
				entity.setVariant(Parrot.Variant.valueOf(section.get("v").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializePhantomSettings(Phantom entity, JsonObject data){
		String key = "phantom";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("s")) entity.setSize(section.get("s").getAsInt());
	}
	private static void deserializePigSettings(Pig entity, JsonObject data){
		String key = "pig";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("s")) entity.setSaddle(section.get("s").getAsBoolean());
	}
	private static void deserializePigZombieSettings(PigZombie entity, JsonObject data){
		String key = "pig_zombie";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("a")) entity.setAnger(section.get("a").getAsInt());
	}
	private static void deserializeProjectileSettings(Projectile entity, JsonObject data){
		String key = "projectile";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("b")) entity.setBounce(section.get("b").getAsBoolean());
	}
	private static void deserializePufferFishSettings(PufferFish entity, JsonObject data){
		String key = "puffer_fish";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("p")) entity.setPuffState(section.get("p").getAsInt());
	}
	private static void deserializeRabbitSettings(Rabbit entity, JsonObject data){
		String key = "rabbit";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();
		
		if(section.has("t")) {
			try{
				entity.setRabbitType(Rabbit.Type.valueOf(section.get("t").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeRaiderSettings(Raider entity, JsonObject data){
		String key = "rabbit";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("cjr")) entity.setCanJoinRaid(section.get("cjr").getAsBoolean());
		if(section.has("pl")) entity.setPatrolLeader(section.get("pl").getAsBoolean());
	}
	private static void deserializeSheepSettings(Sheep entity, JsonObject data){
		String key = "sheep";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("s")) entity.setSheared(section.get("s").getAsBoolean());
	}
	private static void deserializeSlimeSettings(Slime entity, JsonObject data){
		String key = "slime";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("s")) entity.setSize(section.get("s").getAsInt());
	}
	private static void deserializeSnowmanSettings(Snowman entity, JsonObject data){
		String key = "snowman";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("d")) entity.setDerp(section.get("d").getAsBoolean());
	}
	private static void deserializeSpectralArrowSettings(SpectralArrow entity, JsonObject data){
		String key = "spectral_arrow";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("gt")) entity.setGlowingTicks(section.get("gt").getAsInt());
	}
	private static void deserializeSpellcasterSettings(Spellcaster entity, JsonObject data){
		String key = "spellcaster";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("s")) {
			try{
				entity.setSpell(Spell.valueOf(section.get("s").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeTameableSettings(Tameable entity, JsonObject data){
		String key = "tameable";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("o")) {
			try {
				UUID playerUid = UUID.fromString(section.get("o").getAsString());
				AnimalTamer tamer = Bukkit.getOfflinePlayer(playerUid);
				entity.setOwner(tamer);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		if(section.has("t")) entity.setTamed(section.get("t").getAsBoolean());
	}
	private static void deserializeThrownPotionSettings(ThrownPotion entity, JsonObject data){
		String key = "thrown_potion";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("i")) {
			try{
				entity.setItem(ItemUtil.deserialize(section.get("i").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeTNTPrimedSettings(TNTPrimed entity, JsonObject data){
		String key = "tnt_primed";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("ft")) entity.setFuseTicks(section.get("ft").getAsInt());
	}
	private static void deserializeTropicalFishSettings(TropicalFish entity, JsonObject data){
		String key = "tropical_fish";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("bc")) {
			try{
				entity.setBodyColor(DyeColor.valueOf(section.get("ft").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("p")) {
			try{
				entity.setPattern(TropicalFish.Pattern.valueOf(section.get("p").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("pc")) {
			try{
				entity.setPatternColor(DyeColor.valueOf(section.get("pc").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeVehicleSettings(Vehicle entity, JsonObject data){
		String key = "vehicle";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("v")) entity.setVelocity(VectorUtil.deserializeVector(section.get("v").getAsJsonObject()));
	}
	private static void deserializeVillagerSettings(Villager entity, JsonObject data){
		String key = "villager";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		List<MerchantRecipe> recipes = entity.getRecipes();
		
		if(section.has("p")) {
			try{
				entity.setProfession(Profession.valueOf(section.get("p").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("t")) {
			try{
				entity.setVillagerType(Villager.Type.valueOf(section.get("t").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(section.has("l")) entity.setVillagerLevel(section.get("l").getAsInt());
		
		entity.setRecipes(recipes);
	}
	private static void deserializeWitherSkullSettings(WitherSkull entity, JsonObject data){
		String key = "wither_skull";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("c")) entity.setCharged(section.get("c").getAsBoolean());
	}
	private static void deserializeWolfSettings(Wolf entity, JsonObject data){
		String key = "wolf";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("a")) entity.setAngry(section.get("a").getAsBoolean());
		if(section.has("cc")) {
			try{
				entity.setCollarColor(DyeColor.valueOf(section.get("c").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void deserializeZombieSettings(Zombie entity, JsonObject data){
		String key = "zombie";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("b")) entity.setBaby(section.get("b").getAsBoolean());
		section.addProperty("b", entity.isBaby());
	}
	private static void deserializeZombieVillagerSettings(ZombieVillager entity, JsonObject data){
		String key = "zombie_villager";
		if(!data.has(key) || !data.get(key).isJsonObject()) return;
		JsonObject section = data.get(key).getAsJsonObject();

		if(section.has("vp")) {
			try{
				entity.setVillagerProfession(Profession.valueOf(section.get("vp").getAsString()));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
