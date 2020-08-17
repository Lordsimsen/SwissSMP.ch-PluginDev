package ch.swisssmp.city.ceremony.founding.phases;

import java.util.ArrayList;
import java.util.List;

import ch.swisssmp.ceremonies.ITributeListener;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.ceremonies.effects.LightParticles;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ItemUtility;
import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.city.ceremony.founding.RingPresentation;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.Targetable;

public class BaptisePhase extends Phase implements ITributeListener {
	
	private final CityFoundingCeremony ceremony;
	private BukkitTask reminderTask;
	
	private List<RingPresentation> ringDisplays = new ArrayList<RingPresentation>();
	
	public BaptisePhase(CityFoundingCeremony ceremony){
		this.ceremony = ceremony;
	}
	
	@Override
	public void begin(){
		super.begin();
		ceremony.broadcastTitle("", "Tauft eure neue Stadt!");
		this.reminderTask = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), ()->{
			ceremony.broadcastActionBar("Wirf ein benanntes Namensschild ins Feuer.");
			}, 0, 100);
		startRingPresentations();
	}
	
	private void startRingPresentations(){
		List<Player> players = ceremony.getParticipants();
		for(int i = 0; i < players.size(); i++){
			Location location = ceremony.getFire().getLocation().add(0.5, (players.size()-i-1)*0.5-0.5, 0.5);
			RingPresentation p = RingPresentation.start(location, ceremony, players.get(i), i*120);
			ringDisplays.add(p);
		}
	}

	@Override
	public void run() {
		//wait for completion
	}
	
	@Override
	public void finish(){
		if(reminderTask!=null) reminderTask.cancel();
		Color colorA = ItemUtility.getMaterialColor(ceremony.getBaseMaterial());
		Color colorB = ItemUtility.getMaterialColor(ceremony.getCoreMaterial());
		Random random = new Random();
		for(RingPresentation p : ringDisplays){
			LightParticles.spawn(CitySystemPlugin.getInstance(), p.getLocation(), new Targetable(p.getPlayer()), 0, random.nextDouble()>0.3 ? colorA : colorB);
			p.finish();
		}
	}

	@Override
	public void payTribute(ItemStack itemStack, Player player) {
		if(!ceremony.isParticipant(player)) return;
		if(itemStack.getType()!=Material.NAME_TAG) return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasDisplayName()) return;
		String name = itemMeta.getDisplayName();
		if(name.length()<2) return;
		ceremony.setCityName(itemMeta.getDisplayName());
		FireBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getFire(), 8, Color.fromRGB(255, 200, 20),Color.fromRGB(255,150,20));
		this.setCompleted();
	}
}
