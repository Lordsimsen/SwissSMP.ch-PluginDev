package ch.swisssmp.craftmmo.mmoentity;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoeffect.MmoEffect;
import ch.swisssmp.craftmmo.mmoitem.MmoEquipmentType;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;
import ch.swisssmp.craftmmo.mmoitem.MmoChanceItem;
import ch.swisssmp.craftmmo.util.MmoResourceManager;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class MmoMob {
	public static HashMap<Integer, MmoMob> templates;
	public static HashMap<UUID, IControllable> invincible_mobs = new HashMap<UUID, IControllable>();
	public static HashMap<IControllable, Integer> pending_AI_assignments = new HashMap<IControllable, Integer>();;
	
	public final Integer mmo_mob_id;
	//visuals
	public final String name;
	public final MmoEntityType entityType;
	
	//other configuration
	public final MmoAI ai;
	public boolean showName = true;
	public boolean invincible = false;
	public boolean muted = false;
	
	//stats
	public final int base_health;
	public final double base_damage;
	public final double base_attackspeed;
	public final int strength;
	public final int agility;
	public final int intelligence;
	public final int aggroRange;
	public final int attackRange;
	
	//attributes
	public final HashMap<MmoElement, Integer> elements;
	public final int max_drops;
	public final HashMap<MmoChanceItem, Double> drops;
	public final HashMap<MmoChanceItem, Double> equipment;
	
	//class properties
	public final ConfigurationSection class_configuration;
	
	private MmoMob(ConfigurationSection dataSection){
		//this.world = Main.server.getWorld(name);
		//this.directory = new File(Main.plugin.getDataFolder(), "worlds/"+name);
		this.mmo_mob_id = Integer.parseInt(dataSection.getName());
		this.name = dataSection.getString("name");
		this.entityType = MmoEntityType.valueOf(dataSection.getString("type").toUpperCase());
		this.showName = dataSection.getBoolean("showName");
		this.invincible = dataSection.getBoolean("invincible");
		this.muted = dataSection.getBoolean("muted");
		this.base_health = dataSection.getInt("base_health");
		this.base_damage = dataSection.getInt("base_damage")/(double)100;
		this.base_attackspeed = dataSection.getInt("base_attackspeed")/(double)100;
		this.strength = dataSection.getInt("strength");
		this.agility = dataSection.getInt("agility");
		this.intelligence = dataSection.getInt("intelligence");
		this.aggroRange = dataSection.getInt("aggroRange");
		this.attackRange = dataSection.getInt("attackRange");

		this.elements = new HashMap<MmoElement, Integer>();
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		if(configurationSection!=null){
			this.class_configuration = configurationSection.getConfigurationSection("class_properties");
			ConfigurationSection elementsSection = configurationSection.getConfigurationSection("elements");
			if(elementsSection != null){
				for(String elementName : elementsSection.getKeys(false)){
					this.elements.put(MmoElement.get(elementName), elementsSection.getInt(elementName));
				}
			}
		}
		else{
			this.class_configuration = null;
		}
		this.max_drops = dataSection.getInt("max_drops");
		this.drops = new HashMap<MmoChanceItem, Double>();
		this.equipment = new HashMap<MmoChanceItem, Double>();
		ConfigurationSection itemsSection = dataSection.getConfigurationSection("items");
		if(itemsSection != null){
			for(String sectionName : itemsSection.getKeys(false)){
				ConfigurationSection itemSection = itemsSection.getConfigurationSection(sectionName);
				for(String itemName : itemSection.getKeys(false)){
					ConfigurationSection itemData = itemSection.getConfigurationSection(itemName);
					double chance = itemData.getInt("chance");
					int min = itemData.getInt("min");
					int max = itemData.getInt("max");
					ItemStack itemStack = MmoItemManager.getItemFromHybridID(itemData);
					if(itemStack!=null && chance>0){
						if(sectionName.contains("drops")){
							this.drops.put(new MmoChanceItem(itemStack, min, max), chance);
						}
						else{
							this.equipment.put(new MmoChanceItem(itemStack, min, max), chance);
						}
					}
				}
			}
		}
		this.ai = new MmoAI(this.mmo_mob_id, dataSection.getConfigurationSection("ai_configuration"));
		templates.put(mmo_mob_id, this);
	}
	public static MmoMob get(Entity entity){
		Integer mmo_mob_id = getID(entity);
		if(mmo_mob_id==null)
			return null;
		return MmoMob.templates.get(mmo_mob_id);
	}
	public static void loadMobs() throws Exception{
		templates = new HashMap<Integer, MmoMob>();
		YamlConfiguration mmoMobsConfiguration = MmoResourceManager.getYamlResponse("mobs.php");
		for(String mobIDstring : mmoMobsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoMobsConfiguration.getConfigurationSection(mobIDstring);
			new MmoMob(dataSection);
		}
		for(World world : Bukkit.getWorlds()){
			for(LivingEntity entity: world.getLivingEntities()){
				if(entity instanceof IControllable){
					IControllable controllable = (IControllable) entity;
					MmoMob mmoMob = MmoMob.templates.get(controllable.getSaveData().mmo_mob_id);
					if(mmoMob!=null){
						controllable.setMmoAI(mmoMob.ai);
					}
				}
			}
		}
		for(Entry<IControllable, Integer> entry : pending_AI_assignments.entrySet()){
			IControllable iControllable = entry.getKey();
			MmoMob mmoMob = MmoMob.get(entry.getValue());
			if(mmoMob!=null && iControllable.getEntity().getBukkitEntity() instanceof LivingEntity){
				mmoMob.applyData((LivingEntity)iControllable.getEntity().getBukkitEntity());
			}
		}
		pending_AI_assignments.clear();
	}
	public static Integer getID(Entity entity){
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity)entity).getHandle();
		if(!(nmsEntity instanceof IControllable)){
			return -1;
		}
		IControllable controllable = (IControllable) nmsEntity;
		return controllable.getSaveData().mmo_mob_id;
	}
	
	public static final NBTTagCompound getSaveData(ItemStack itemStack){
		net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		if(!nmsItem.hasTag()){
			return null;
		}
		return nmsItem.getTag();
	}
	public Entity spawnInstance(Location location){
		if(this.entityType==null){
			throw new NullPointerException("EntityType is null!");
		}
		World world = location.getWorld();
		LivingEntity entity;
		switch(this.entityType){
		case MMO_CITIZEN:
			MmoCitizen mmoCitizen = new MmoCitizen(this, world);
			MmoEntityType.spawnEntity(mmoCitizen, location);
			entity = (LivingEntity)mmoCitizen.getBukkitEntity();
			break;
		case MMO_ZOMBIE:
			MmoZombie mmoZombie = new MmoZombie(this, world);
			MmoEntityType.spawnEntity(mmoZombie, location);
			entity = (LivingEntity)mmoZombie.getBukkitEntity();
			break;
		case MMO_BOAR:
			MmoBoar mmoBoar = new MmoBoar(this, world);
			MmoEntityType.spawnEntity(mmoBoar, location);
			entity = (LivingEntity)mmoBoar.getBukkitEntity();
			break;
		case MMO_SKELETON:
			MmoSkeleton mmoSkeleton = new MmoSkeleton(this, world);
			MmoEntityType.spawnEntity(mmoSkeleton, location);
			entity = (LivingEntity)mmoSkeleton.getBukkitEntity();
			break;
		case MMO_DRAGON:
			MmoDragon mmoDragon = new MmoDragon(this, world);
			MmoEntityType.spawnEntity(mmoDragon, location);
			entity = (LivingEntity)mmoDragon.getBukkitEntity();
			break;
		case MMO_GOLEM:
			MmoGolem mmoGolem = new MmoGolem(this, world);
			MmoEntityType.spawnEntity(mmoGolem, location);
			entity = (LivingEntity)mmoGolem.getBukkitEntity();
			break;
		case MMO_SPIDER:
			MmoSpider mmoSpider = new MmoSpider(this, world);
			MmoEntityType.spawnEntity(mmoSpider, location);
			entity = (LivingEntity)mmoSpider.getBukkitEntity();
			break;
		case MMO_CAVE_SPIDER:
			MmoCaveSpider mmoCaveSpider = new MmoCaveSpider(this, world);
			MmoEntityType.spawnEntity(mmoCaveSpider, location);
			entity = (LivingEntity)mmoCaveSpider.getBukkitEntity();
			break;
		case MMO_HORSE:
			MmoHorse mmoHorse = new MmoHorse(this, world);
			MmoEntityType.spawnEntity(mmoHorse, location);
			entity = (LivingEntity)mmoHorse.getBukkitEntity();
			break;
		case MMO_SLIME:
			MmoSlime mmoSlime = new MmoSlime(this, world);
			MmoEntityType.spawnEntity(mmoSlime, location);
			entity = (LivingEntity)mmoSlime.getBukkitEntity();
			break;
		case MMO_CREEPER:
			MmoCreeper mmoCreeper = new MmoCreeper(this, world);
			MmoEntityType.spawnEntity(mmoCreeper, location);
			entity = (LivingEntity)mmoCreeper.getBukkitEntity();
			break;
		case MMO_GHAST:
			MmoGhast mmoGhast = new MmoGhast(this, world);
			MmoEntityType.spawnEntity(mmoGhast, location);
			entity = (LivingEntity)mmoGhast.getBukkitEntity();
			break;
		case MMO_PIG_ZOMBIE:
			MmoPigZombie mmoPigZombie = new MmoPigZombie(this, world);
			MmoEntityType.spawnEntity(mmoPigZombie, location);
			entity = (LivingEntity)mmoPigZombie.getBukkitEntity();
			break;
		case MMO_ENDERMAN:
			MmoEnderman mmoEnderman = new MmoEnderman(this, world);
			MmoEntityType.spawnEntity(mmoEnderman, location);
			entity = (LivingEntity)mmoEnderman.getBukkitEntity();
			break;
		case MMO_SILVERFISH:
			MmoSilverfish mmoSilverfish = new MmoSilverfish(this, world);
			MmoEntityType.spawnEntity(mmoSilverfish, location);
			entity = (LivingEntity)mmoSilverfish.getBukkitEntity();
			break;
		case MMO_BLAZE:
			MmoBlaze mmoBlaze = new MmoBlaze(this, world);
			MmoEntityType.spawnEntity(mmoBlaze, location);
			entity = (LivingEntity)mmoBlaze.getBukkitEntity();
			break;
		case MMO_MAGMA_CUBE:
			MmoMagmaCube mmoMagmaCube = new MmoMagmaCube(this, world);
			MmoEntityType.spawnEntity(mmoMagmaCube, location);
			entity = (LivingEntity)mmoMagmaCube.getBukkitEntity();
			break;
//		case "MMO_WITHER_SKULL":
//			MmoWitherSKull mmoWitherSkull = new MmoWitherSKull(this, world);
//			MmoEntityType.spawnEntity(mmoWitherSkull, location);
//			entity = (LivingEntity)mmoWitherSkull.getBukkitEntity();
//			break;
		case MMO_BAT:
			MmoBat mmoBat = new MmoBat(this, world);
			MmoEntityType.spawnEntity(mmoBat, location);
			entity = (LivingEntity)mmoBat.getBukkitEntity();
			break;
		case MMO_WITCH:
			MmoWitch mmoWitch = new MmoWitch(this, world);
			MmoEntityType.spawnEntity(mmoWitch, location);
			entity = (LivingEntity)mmoWitch.getBukkitEntity();
			break;
		case MMO_ENDERMITE:
			MmoEndermite mmoEndermite = new MmoEndermite(this, world);
			MmoEntityType.spawnEntity(mmoEndermite, location);
			entity = (LivingEntity)mmoEndermite.getBukkitEntity();
			break;
		case MMO_GUARDIAN:
			MmoGuardian mmoGuardian = new MmoGuardian(this, world);
			MmoEntityType.spawnEntity(mmoGuardian, location);
			entity = (LivingEntity)mmoGuardian.getBukkitEntity();
			break;
		case MMO_SHULKER:
			MmoShulker mmoShulker = new MmoShulker(this, world);
			MmoEntityType.spawnEntity(mmoShulker, location);
			entity = (LivingEntity)mmoShulker.getBukkitEntity();
			break;
		case MMO_SHEEP:
			MmoSheep mmoSheep = new MmoSheep(this, world);
			MmoEntityType.spawnEntity(mmoSheep, location);
			entity = (LivingEntity)mmoSheep.getBukkitEntity();
			break;
		case MMO_COW:
			MmoCow mmoCow = new MmoCow(this, world);
			MmoEntityType.spawnEntity(mmoCow, location);
			entity = (LivingEntity)mmoCow.getBukkitEntity();
			break;
		case MMO_CHICKEN:
			MmoChicken mmoChicken = new MmoChicken(this, world);
			MmoEntityType.spawnEntity(mmoChicken, location);
			entity = (LivingEntity)mmoChicken.getBukkitEntity();
			break;
		case MMO_SQUID:
			MmoSquid mmoSquid = new MmoSquid(this, world);
			MmoEntityType.spawnEntity(mmoSquid, location);
			entity = (LivingEntity)mmoSquid.getBukkitEntity();
			break;
		case MMO_WOLF:
			MmoWolf mmoWolf = new MmoWolf(this, world);
			MmoEntityType.spawnEntity(mmoWolf, location);
			entity = (LivingEntity)mmoWolf.getBukkitEntity();
			break;
		case MMO_MUSHROOM_COW:
			MmoMushroomCow mmoMushroomCow = new MmoMushroomCow(this, world);
			MmoEntityType.spawnEntity(mmoMushroomCow, location);
			entity = (LivingEntity)mmoMushroomCow.getBukkitEntity();
			break;
		case MMO_SNOWMAN:
			MmoSnowman mmoSnowman = new MmoSnowman(this, world);
			MmoEntityType.spawnEntity(mmoSnowman, location);
			entity = (LivingEntity)mmoSnowman.getBukkitEntity();
			break;
		case MMO_OCELOT:
			MmoOcelot mmoOcelot = new MmoOcelot(this, world);
			MmoEntityType.spawnEntity(mmoOcelot, location);
			entity = (LivingEntity)mmoOcelot.getBukkitEntity();
			break;
		case MMO_RABBIT:
			MmoRabbit mmoRabbit = new MmoRabbit(this, world);
			MmoEntityType.spawnEntity(mmoRabbit, location);
			entity = (LivingEntity)mmoRabbit.getBukkitEntity();
			break;
		case MMO_BEAR:
			MmoBear mmoBear = new MmoBear(this, world);
			MmoEntityType.spawnEntity(mmoBear, location);
			entity = (LivingEntity)mmoBear.getBukkitEntity();
			break;
		default:
			entity=null;
			Main.info("Cannot spawn mob of type "+entityType.toString()+" ("+this.name+")");
			return null;
		}
		EntityEquipment entityEquipment = entity.getEquipment();
		if(entityEquipment!=null){
			applyEquipment(entityEquipment);
		}
		if(entity!=null){
			applyData(entity);
		}
		MmoEffect.play(location, MmoEffect.SPAWN);
		return entity;
	}
	protected void applyEquipment(EntityEquipment entityEquipment){
		entityEquipment.clear();
		List<MmoChanceItem> randomEquipment = MmoChanceItem.getRandomItems(equipment);
		for(MmoChanceItem equipmentItem : randomEquipment){
			ItemStack itemStack = equipmentItem.unpack();
			if(itemStack==null)
				continue;
			MmoEquipmentType equipmentType = MmoEquipmentType.getType(itemStack);
			switch(equipmentType){
			case HELMET:
				entityEquipment.setHelmet(itemStack);
				break;
			case CHESTPLATE:
				entityEquipment.setChestplate(itemStack);
				break;
			case LEGGINGS:
				entityEquipment.setLeggings(itemStack);
				break;
			case BOOTS:
				entityEquipment.setBoots(itemStack);
				break;
			case WEAPON:
				entityEquipment.setItemInMainHand(itemStack);
				break;
			case HAND:
				if(entityEquipment.getItemInMainHand()==null){
					entityEquipment.setItemInMainHand(itemStack);
				}
				else{
					entityEquipment.setItemInOffHand(itemStack);
				}
				break;
			default:
				break;
			}
		}
	}
	public void applyData(LivingEntity entity){
		entity.setCustomName(name);
		entity.setMaxHealth(base_health);
		entity.setHealth(base_health);
		entity.setInvulnerable(invincible);
		entity.setCustomNameVisible(showName);
		entity.setCanPickupItems(false);
		entity.setSilent(muted);
		((LivingEntity)entity).setRemoveWhenFarAway(false);
		net.minecraft.server.v1_12_R1.EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
		if(nmsEntity instanceof IControllable){
			IControllable iControllable = (IControllable) nmsEntity;
			iControllable.setMmoAI(this.ai);
		}
		if(this.class_configuration!=null){
			if(this.class_configuration.contains("age")){
				int age = this.class_configuration.getInt("age")*(-1)-1;
				Random random = new Random();
				int randomInt = random.nextInt(4);
				if(entity instanceof Ageable){
					Ageable ageable = (Ageable) entity;
					if(randomInt>age){
						ageable.setAdult();
					}
					else{
						ageable.setBaby();
					}
				}
				else if(entity instanceof Zombie){
					Zombie zombie = (Zombie) entity;
					zombie.setBaby(!(randomInt>age));
				}
			}
			if(this.class_configuration.contains("profession") && entity instanceof Villager){
				Villager villager = (Villager) entity;
				String professionString = this.class_configuration.getString("profession");
				try{
				    Profession profession = Profession.valueOf(professionString);
				    villager.setProfession(profession);
				}
				catch(Exception e){
					if(professionString.equals("NITWIT")){
						((CraftVillager)villager).getHandle().setProfession(5);
					}
				}
			}
		}
	}
	public static MmoMob get(int mmo_mob_id){
		if(templates==null){
			return null;
		}
		return templates.get(mmo_mob_id);
	}
}
