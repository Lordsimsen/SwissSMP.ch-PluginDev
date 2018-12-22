package ch.swisssmp.events.halloween;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.Targetable;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class FlashMobBattle implements Runnable, Listener {
	private static HashMap<BlockVector,FlashMobBattle> ongoingBattles = new HashMap<BlockVector,FlashMobBattle>();
	
	private Random random = new Random();
	
	private final BlockVector center;
	private final Block block;

	private BukkitTask task;
	private Long remainingTime = 2520l;
	
	private List<Entity> activeEntities = new ArrayList<Entity>();
	private Long nextSpawn = 200L;
	private float nextSpawnAmount = 2;
	
	private int requiredBattleAreaSize = 50;
	private BattleArea battleArea;
	
	private EntityType[] entityTypes = new EntityType[]{
		EntityType.ZOMBIE,
		EntityType.SKELETON,
		EntityType.SPIDER,
		EntityType.BAT,
		EntityType.WITCH,
		EntityType.VINDICATOR
	};
	private float[] weights = new float[]{
			0.5f,
			0.2f,
			0.175f,
			0.05f,
			0.05f,
			0.025f
		};
	
	private ItemStack bombonRed;
	private ItemStack bombonOrange;
	private ItemStack bombonPurple;
	private ItemStack bombonAqua;
	private ItemStack batWing;
	private ItemStack deluminator;
	
	private Map<UUID, Integer>playerScores = new HashMap<UUID,Integer>();
	
	private FlashMobBattle(Block centerBlock){
		this.block = centerBlock;
		this.center = new BlockVector(centerBlock.getX(),centerBlock.getY(),centerBlock.getZ());
		this.loadItems();
	}
	
	private void loadItems(){
		this.bombonRed = CustomItems.getCustomItemBuilder("BOMBON_RED").build();
		this.bombonOrange = CustomItems.getCustomItemBuilder("BOMBON_ORANGE").build();
		this.bombonPurple = CustomItems.getCustomItemBuilder("BOMBON_PURPLE").build();
		this.bombonAqua = CustomItems.getCustomItemBuilder("BOMBON_AQUA").build();
		this.batWing = CustomItems.getCustomItemBuilder("BAT_WINGS").build();
		this.deluminator = CustomItems.getCustomItemBuilder("DELUMINATOR").build();
	}
	
	private boolean scanArea(Player player){
		this.battleArea = new BattleArea(this.block);
		this.battleArea.scan(5, this.requiredBattleAreaSize);
		if(this.battleArea.getSize()<requiredBattleAreaSize){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Die Geister verlangen mehr Platz...");
			return false;
		}
		return true;
	}
	
	private void startSoundtrack(){
		for(Player player : this.block.getWorld().getPlayers()){
			Bukkit.dispatchCommand(SwissSMPUtils.getPluginSender(), "playsound events.halloween.spookyhour record "+player.getName()+" "+block.getX()+" "+block.getY()+" "+block.getZ()+" 10 1");
		}
		//this.block.getWorld().playSound(block.getLocation(), "events.halloween.spookyhour", SoundCategory.RECORDS, 10, 1);
	}
	
	private void spawnCreeperHead(){
		ItemStack displayStack = CustomItems.getCustomItemBuilder("GHOST_CREEPER_FACE").build();
		Location location = this.block.getLocation().add(0.5, 3, 0.5);
		Item item = this.block.getWorld().dropItem(location, displayStack);
		item.setGravity(false);
		item.setPickupDelay(Integer.MAX_VALUE);
		item.setInvulnerable(true);
		Bukkit.getScheduler().runTaskLater(HalloweenEventPlugin.getInstance(), ()->{
			item.setVelocity(new Vector(0,0,0));
			//item.teleport(location);
		}, 1L);
		this.activeEntities.add(item);
	}

	@Override
	public void run() {
		this.remainingTime--;
		if(this.remainingTime<=0){
			this.finish();
			return;
		}
		this.nextSpawn--;
		if(this.nextSpawn<=0 && this.remainingTime>500){
			this.nextSpawn = 200l;
			this.spawnMobs(Mathf.floorToInt(this.nextSpawnAmount));
			this.nextSpawnAmount+=1;
		}
		Player player;
		for(UUID player_uuid : this.playerScores.keySet()){
			player = Bukkit.getPlayer(player_uuid);
			if(player==null) continue; //player is offline
			if(player.getLocation().distanceSquared(this.block.getLocation())>900){
				this.forcePlayerNearby(player);
			}
		}
		this.showBoundaries();
	}
	
	private void showBoundaries(){
		World world = this.block.getWorld();
		Vector center = new Vector(this.block.getX()+0.5, this.block.getY()+1, this.block.getZ()+0.5);
		double x;
		double z;
		for(float i = 0; i < Math.PI*2; i+=Math.PI/16){
			x = 30*Math.cos(i+this.remainingTime*0.001f);
			z = 30*Math.sin(i+this.remainingTime*0.001f);
			world.spawnParticle(Particle.REDSTONE, center.getX()+x,center.getY(),center.getZ()+z,0,0.1f,1f,0.6f,1);
		}
	}
	
	private void forcePlayerNearby(Player player){
		player.teleport(this.block.getLocation().add(0.5, 1, 0.5));
		SwissSMPler.get(player).sendActionBar(ChatColor.LIGHT_PURPLE+"Du kannst nicht fliehen!");
	}
	
	private void spawnMobs(int amount){
		for(int i = 0; i < amount; i++){
			Bukkit.getScheduler().runTaskLater(HalloweenEventPlugin.getInstance(), ()->{
				this.spawnMob();
			}, i*5);
		}
	}
	
	private void spawnMob(){
		EntityType entityType = this.chooseNextEntityType();
		if(entityType==null){
			throw new NullPointerException("Konnte den nächsten EntityType nicht bestimmen. (Warum auch immer)");
		}
		Location location = this.chooseNextSpawnLocation();
		Location from = new Location(block.getWorld(), block.getX()+0.5,block.getY()+0.9,block.getZ()+0.5,0,0);
		Targetable to = new Targetable(location);
		SoulParticles particles = SoulParticles.spawn(from, to, 0.5f);
		particles.addOnHitListener(()->{
			if(this.remainingTime<=0) return;
			location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_IMPACT, SoundCategory.HOSTILE, 0.7f, (float)(0.9+random.nextDouble()*0.2));
			Entity result = location.getWorld().spawnEntity(location, entityType);
			if(result.getType()==EntityType.VINDICATOR){
				((LivingEntity)result).getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
			}
			if(result instanceof LivingEntity){
				((LivingEntity)result).setCanPickupItems(false);
			}
			this.activeEntities.add(result);
		});
	}
	
	private EntityType chooseNextEntityType(){
		float randomValue = this.random.nextFloat();
		for(int i = 0; i < this.weights.length; i++){
			if(this.weights[i]<randomValue){
				randomValue-=this.weights[i];
				continue;
			}
			return this.entityTypes[i];
		}
		return null;
	}
	
	private Location chooseNextSpawnLocation(){
		Block block = this.battleArea.getRandomBlock();
		return new Location(block.getWorld(), block.getX()+0.5,block.getY()+0.1,block.getZ()+0.5,random.nextFloat()*360,0);
	}
	
	private void finish(){
		Player player;
		for(Entry<UUID,Integer> entry : this.playerScores.entrySet()){
			player = Bukkit.getPlayer(entry.getKey());
			if(player==null) continue;
			this.displayFinalScore(player);
		}
		this.stop();
		this.saveScores();
	}
	
	protected void stop(){
		this.remainingTime = -1l;
		HandlerList.unregisterAll(this);
		this.task.cancel();
		ongoingBattles.remove(this.center);
		for(Entity entity : this.block.getWorld().getNearbyEntities(block.getLocation(), 50, 50, 50)){
			if(!(entity instanceof Player)) continue;
			((Player)entity).stopSound("events.halloween.spookyhour");
		}
		for(Entity entity : this.activeEntities){
			try{
				entity.getWorld().playEffect(entity.getLocation().add(0, 1, 0), Effect.SMOKE, 1);
				entity.remove();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	private void onEntityDeath(EntityDeathEvent event){
		if(!this.activeEntities.contains(event.getEntity())) return;
		this.activeEntities.remove(event.getEntity());
		if(random.nextDouble()>0.8f){
			event.getDrops().add(this.bombonRed.clone());
		}
		if(random.nextDouble()>0.95f){
			event.getDrops().add(this.bombonOrange.clone());
		}
		if(random.nextDouble()>0.975f){
			event.getDrops().add(this.bombonPurple.clone());
		}
		if(random.nextDouble()>0.99f){
			event.getDrops().add(this.bombonAqua.clone());
		}
		if(random.nextDouble()>0.9f){
			event.getDrops().add(this.batWing.clone());
		}
		if(random.nextDouble()>0.99f){
			event.getDrops().add(this.deluminator.clone());
		}
		Player killer = event.getEntity().getKiller();
		if(killer==null) return;
		this.addScore(killer, this.getScore(event.getEntityType()));
	}
	
	private int getScore(EntityType entityType){
		switch(entityType){
		case SPIDER:
			return 75;
		case BAT:
			return 250;
		case VINDICATOR:
			return 200;
		case WITCH:
			return 150;
		case SKELETON:
			return 125;
		default:
			return 100;
		}
	}
	
	private void addScore(Player player, int score){
		if(!this.playerScores.containsKey(player.getUniqueId())) this.playerScores.put(player.getUniqueId(), 0);
		int currentScore = this.playerScores.get(player.getUniqueId());
		this.playerScores.put(player.getUniqueId(), currentScore+score);
		this.displayScore(player);
	}
	
	private void displayScore(Player player){
		SwissSMPler.get(player.getUniqueId()).sendActionBar(ChatColor.AQUA+""+this.playerScores.get(player.getUniqueId())+" Punkte");
	}
	
	private void displayFinalScore(Player player){
		int score = this.playerScores.get(player.getUniqueId());
		SwissSMPler.get(player.getUniqueId()).sendTitle("Süsses oder Saures!", ChatColor.LIGHT_PURPLE+""+score+" Punkte");
		player.sendMessage("["+ChatColor.LIGHT_PURPLE+"Halloween"+ChatColor.RESET+"] "+ChatColor.YELLOW+"Deine Punktzahl: "+score+" Punkte");
	}
	
	private void saveScores(){
		Collection<String> arguments = new HashSet<String>();
		for(Entry<UUID,Integer> entry : this.playerScores.entrySet()){
			arguments.add("scores["+entry.getKey()+"]="+entry.getValue());
		}
		arguments.add("battle="+URLEncoder.encode(UUID.randomUUID().toString()));
		String[] argumentsArray = new String[arguments.size()];
		arguments.toArray(argumentsArray);
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("halloween/save_scores.php", argumentsArray);
		if(yamlConfiguration==null || !yamlConfiguration.contains("result")) return;
		ConfigurationSection resultSection = yamlConfiguration.getConfigurationSection("result");
		if(resultSection.contains("broadcast")){
			Bukkit.broadcastMessage(resultSection.getString("broadcast"));
		}
	}
	
	protected static FlashMobBattle start(Player player, Block block){
		FlashMobBattle result = new FlashMobBattle(block);
		if(!result.scanArea(player)) return null;
		Bukkit.getPluginManager().registerEvents(result, HalloweenEventPlugin.getInstance());
		result.task = Bukkit.getScheduler().runTaskTimer(HalloweenEventPlugin.getInstance(), result, 0, 1);
		ongoingBattles.put(result.center, result);
		result.startSoundtrack();
		result.spawnCreeperHead();
		result.playerScores.put(player.getUniqueId(), 0);
		return result;
	}
	
	protected static FlashMobBattle get(BlockVector blockVector){
		return ongoingBattles.get(blockVector);
	}
	
	protected static FlashMobBattle getNearby(Location location){
		for(FlashMobBattle battle : ongoingBattles.values()){
			if(battle.block.getLocation().distanceSquared(location)>10000){
				continue;
			}
			return battle;
		}
		return null;
	}
}
