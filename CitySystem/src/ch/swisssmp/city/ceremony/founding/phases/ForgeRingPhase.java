package ch.swisssmp.city.ceremony.founding.phases;

import ch.swisssmp.ceremonies.ISacrificeListener;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.CircleBurstEffect;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ItemManager;
import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.city.ceremony.founding.FoundingCeremonyCircleEffect.RingEffectType;

public class ForgeRingPhase extends Phase implements ISacrificeListener {
	
	private final Material[] baseMaterials = new Material[]{
		Material.IRON_BLOCK,
		Material.GOLD_BLOCK,
		Material.EMERALD_BLOCK,
		Material.OBSIDIAN,
		Material.PRISMARINE_BRICKS,
		Material.QUARTZ_BLOCK,
		Material.LAPIS_BLOCK,
		Material.REDSTONE_BLOCK
	};
	
	private final Material[] coreMaterials = new Material[]{
		Material.DIAMOND,
		Material.EMERALD,
		Material.NETHER_STAR,
		Material.REDSTONE,
		Material.LAPIS_LAZULI,
		Material.GLOWSTONE_DUST,
		Material.OXEYE_DAISY,
		Material.BLUE_ORCHID
	};
	
	private final CityFoundingCeremony ceremony;
	private BukkitTask reminderTask;
	
	private ItemStack baseStack;
	private ItemStack coreStack;
	
	private int submittedBaseCount = 0;
	private int submittedCoreCount = 0;
	
	private boolean expandedRing = false;
	
	public ForgeRingPhase(CityFoundingCeremony ceremony){
		this.ceremony = ceremony;
	}
	
	@Override
	public void begin(){
		super.begin();
		ceremony.broadcastTitle("", "Schmiedet einen Ring!");
		this.reminderTask = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), ()->{
			ceremony.broadcastActionBar("Werft Material für den Siegelring ins Feuer.");
			}, 0, 100);
		startMusic();
	}
	
	private void startMusic(){
		Block block = ceremony.getFire();
		for(Player player : ceremony.getPlayers()){
			player.stopSound("founding_ceremony_shaker", SoundCategory.RECORDS);
		}
		ceremony.setMusic(block.getLocation(), "founding_ceremony_finale", 932);
		Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
			ceremony.setMusic(block.getLocation(), "founding_ceremony_drums", 932);
		}, 48L);
	}

	@Override
	public void run() {
		//wait for completion
	}
	
	@Override
	public void complete(){
		super.complete();
		ceremony.setRingMaterials(baseStack.getType(), coreStack.getType());
		Color colorA = ItemManager.getMaterialColor(baseStack.getType());
		Color colorB = ItemManager.getMaterialColor(coreStack.getType());
		CircleBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getFire(), CityFoundingCeremony.ceremonyRange, colorA, colorB);
	}
	
	@Override
	public void finish(){
		super.finish();
		if(reminderTask!=null) reminderTask.cancel();
	}

	@Override
	public void sacrifice(ItemStack itemStack, Player player) {
		int previousAmount = submittedBaseCount + submittedCoreCount;
		if(baseStack!=null && baseStack.isSimilar(itemStack) && !baseMaterialsProvided()){
			baseStack.setAmount(baseStack.getAmount()+itemStack.getAmount());
		}
		else if(coreStack!=null && coreStack.isSimilar(itemStack) && !coreMaterialsProvided()){
			coreStack.setAmount(coreStack.getAmount()+itemStack.getAmount());
		}
		else if(baseStack==null && contains(baseMaterials,itemStack.getType())){
			baseStack = itemStack;
		}
		else if(coreStack==null && contains(coreMaterials,itemStack.getType())){
			coreStack = itemStack;
		}
		else return;
		submittedBaseCount = baseStack!=null ? Math.min(baseStack.getAmount(),4) : 0;
		if(coreMaterialsProvided()){
			submittedCoreCount = 1;
		}
		
		if(!expandedRing){
			expandedRing = true;
			this.ceremony.getRingEffect().setRadius(7);
			this.ceremony.getRingEffect().setRingEffectType(RingEffectType.RotatingRing);
		}
		
		Color color = ItemManager.getMaterialColor(itemStack.getType());
		FireBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getFire(), 3, color, color);
		for(int i = previousAmount; i < submittedBaseCount + submittedCoreCount; i++) {
			ceremony.getRingEffect().setColor(i, color);
		}
		
		if(allMaterialsProvided()){
			setCompleted();
		}
	}
	
	private boolean baseMaterialsProvided(){
		return baseStack!=null && baseStack.getAmount()>=ItemManager.getRequiredBaseAmount(baseStack.getType());
	}
	
	private boolean coreMaterialsProvided(){
		if(coreStack==null) return false;
		int requiredAmount = ItemManager.getRequiredCoreAmount(coreStack.getType());
		return coreStack.getAmount()>=requiredAmount;
	}
	
	private boolean allMaterialsProvided(){
		return baseMaterialsProvided() && coreMaterialsProvided();
	}
	
	private static boolean contains(Material[] materials, Material material){
		for(int i = 0; i < materials.length; i++){
			if(materials[i]==material) return true;
		}
		return false;
	}
}
