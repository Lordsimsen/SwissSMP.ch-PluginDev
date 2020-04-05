package ch.swisssmp.event.quarantine.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineEventPlugin;
import ch.swisssmp.event.quarantine.QuarantineEventInstance.Phase;
import ch.swisssmp.event.quarantine.QuarantineMaterial;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;

/**
 * Setze alle Teilnehmer zur Startposition
 * @author Oliver
 *
 */
public class BeforeStartPhase extends QuarantineEventInstanceTask {

	private static Random random = new Random();
	
	ItemStack potionStack;
	
	public BeforeStartPhase(QuarantineEventInstance instance) {
		super(instance);
	}
	
	@Override
	protected void onInitialize() {

		random.setSeed(LocalDateTime.now().getNano()+LocalDateTime.now().getSecond());
		
		CustomItemBuilder healPotionBuilder = CustomItems.getCustomItemBuilder("QUARANTINE_HEAL_POTION");
		if(healPotionBuilder==null) {
			Bukkit.getLogger().info(QuarantineEventPlugin.getPrefix()+ChatColor.RED+" Heiltrank nicht gefunden!");
			cancel();
			return;
		}
		healPotionBuilder.setAmount(1);
		ItemStack potionStack = healPotionBuilder.build();
		if(potionStack==null) {
			Bukkit.getLogger().info(QuarantineEventPlugin.getPrefix()+ChatColor.RED+" Heiltrank konnte nicht zusammengestellt werden!");
			cancel();
			return;
		}
		potionStack.setAmount(3);
		
		PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
		potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false));
		potionStack.setItemMeta(potionMeta);
		this.potionStack = potionStack;
		
		getInstance().getScoreController().reset();
		
		for(Entity entity : getArena().getWorld().getEntities()) {
			if(entity.getType()!=EntityType.DROPPED_ITEM) continue;
			if(!getArena().contains(entity.getLocation())) continue;
			entity.remove();
		}
	}

	@Override
	public void run() {
		QuarantineEventInstance instance = getInstance();
		List<Player> players = new ArrayList<Player>();
		List<UUID> survivors = new ArrayList<UUID>();
		List<UUID> infected = new ArrayList<UUID>();
		for(UUID playerUid : instance.getPlayers()) {
			Player player = Bukkit.getPlayer(playerUid);
			if(player==null) {
				instance.removePlayer(playerUid);
				continue;
			}
			
			instance.clearEffects(player);
			players.add(player);
			survivors.add(player.getUniqueId());
		}
		int infectedCount = Mathf.ceilToInt(players.size() * getArena().getInfectedRatio());
		for(int i = 0; i < infectedCount; i++) {
			int infectedIndex = random.nextInt(survivors.size());
			UUID infectedUid = survivors.get(infectedIndex);
			survivors.remove(infectedIndex);
			infected.add(infectedUid);
		}
		instance.setSurvivors(survivors);
		instance.setInfected(infected);
		
		QuarantineArena arena = getArena();
		World world = arena.getWorld();
		Location survivorStart = arena.getSurvivorStart().getLocation(world);
		Location infectedSpawn = arena.getRespawn().getLocation(world);
		double randomizedRadius = 1.5f;
		for(Player player : players) {
			boolean isInfected = infected.contains(player.getUniqueId());
			double randomizedX = (random.nextDouble()*2-1) * randomizedRadius;
			double randomizedZ = (random.nextDouble()*2-1) * randomizedRadius;
			if(isInfected) {
				player.teleport(infectedSpawn.clone().add(randomizedX, 0, randomizedZ));
				instance.initializeInfected(player);
			}
			else {
				player.teleport(survivorStart.clone().add(randomizedX, 0, randomizedZ));
				instance.initializeSurvivor(player);
			}
		}

		Bukkit.clearRecipes();
		getArena().getWorld().setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
		NamespacedKey key = new NamespacedKey(QuarantineEventPlugin.getInstance(), "quarantine_heal_potion");
		Recipe recipe = createRecipe(key);
		if(recipe!=null) {
			Bukkit.addRecipe(recipe);
		}
		else {
			String prefix = QuarantineEventPlugin.getPrefix();
			Bukkit.getLogger().info(prefix+ChatColor.RED+" Rezept für Heiltrank konnte nicht generiert werden!");
			cancel();
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			// player.sendMessage("Heilmittel: "+String.join(", ", r.getIngredientList().stream().map(ing->QuarantineMaterial.getName(ing.getType())).collect(Collectors.toList())));
			player.discoverRecipe(key);
		}
		
		complete();
	}
	
	@Override
	protected void onComplete() {
		getInstance().setPhase(Phase.Start);
	}
	
	@Override
	protected void onCancel() {
		getInstance().cancel();
	}

	private Recipe createRecipe(NamespacedKey key) {
		String prefix = QuarantineEventPlugin.getPrefix();
		
		QuarantineMaterial[] liquids = QuarantineMaterial.getLiquids();
		QuarantineMaterial[] solids = QuarantineMaterial.getSolids();
		QuarantineMaterial[] catalysts = QuarantineMaterial.getCatalysts();
		
		QuarantineMaterial liquid = liquids[random.nextInt(liquids.length)];	
		QuarantineMaterial solid = solids[random.nextInt(solids.length)];	
		QuarantineMaterial catalyst = catalysts[random.nextInt(catalysts.length)];

		ItemStack liquidItemStack = liquid.getItemStack();
		ItemStack solidItemStack = solid.getItemStack();
		ItemStack catalystItemStack = catalyst.getItemStack();

		if(potionStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" Heiltrank nicht initialisiert!");
			return null;
		}
		if(liquidItemStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" "+liquid.toString()+" nicht gefunden!");
			return null;
		}
		if(solidItemStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" "+solid.toString()+" nicht gefunden!");
			return null;
		}
		if(catalystItemStack==null) {
			Bukkit.getLogger().info(prefix+ChatColor.RED+" "+catalyst.toString()+" nicht gefunden!");
			return null;
		}
		
		
		ShapelessRecipe recipe = new ShapelessRecipe(key, potionStack);
		recipe.addIngredient(new RecipeChoice.MaterialChoice(liquidItemStack.getType()));
		recipe.addIngredient(new RecipeChoice.MaterialChoice(solidItemStack.getType()));
		recipe.addIngredient(new RecipeChoice.MaterialChoice(catalystItemStack.getType()));
		
		recipe.setGroup("Brewing recipe");
		
		Bukkit.getLogger().info(String.join(", ", recipe.getIngredientList().stream().map(e->e.getType().toString()).collect(Collectors.toList())));
		
		return recipe;
	}
}
