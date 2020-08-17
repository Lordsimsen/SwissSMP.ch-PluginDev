package ch.swisssmp.city.ceremony.founding.phases;

import java.util.List;

import ch.swisssmp.ceremonies.ITributeListener;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.utils.SwissSMPler;

public class PledgeAllegiancePhase extends Phase implements ITributeListener {
	
	private final CityFoundingCeremony ceremony;
	
	private BukkitTask reminderTask;
	private BukkitTask autoContinueTask;
	
	private List<Player> nearbyPlayers;
	
	public PledgeAllegiancePhase(CityFoundingCeremony ceremony){
		this.ceremony = ceremony;
	}
	
	@Override
	public void begin(){
		super.begin();
		this.updateNearbyPlayers();
//		if(this.nearbyPlayers.size()==0){
//			this.ceremony.cancel();
//			return;
//		}
		this.broadcastTitle("", "Schwöre deine Treue!");
		this.reminderTask = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), ()->{
			this.updateNearbyPlayers();
			if(this.nearbyPlayers.size()==0){
//				if(ceremony.getPlayers().size()<2) ceremony.cancel();
				if(ceremony.getParticipants().size()<1) ceremony.cancel();
				else setCompleted();
				return;
			}
			broadcastActionBar("Wirf einen Knochen ins Feuer.");
			ceremony.broadcastActionBar("Warte auf Mitgründer.");
			}, 0, 100);
		this.autoContinueTask = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
//			if(ceremony.getPlayers().size()<2){
			if(ceremony.getParticipants().size()<1){
				ceremony.cancel();
				return;
			}
			setCompleted();
			},600L);
	}

	@Override
	public void run() {
		//wait for completion
	}
	
	@Override
	public void complete(){
		super.complete();
		FireBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getFire(), 5, Color.fromRGB(255, 150, 0), Color.fromRGB(255, 100, 0));
	}
	
	@Override
	public void finish(){
		super.finish();
		if(reminderTask!=null) reminderTask.cancel();
		if(autoContinueTask!=null) autoContinueTask.cancel();
	}
	
	private void updateNearbyPlayers(){
		this.nearbyPlayers = CityFoundingCeremony.getNearbyPlayers(ceremony.getFire().getLocation());
	}
	
	private void broadcastTitle(String title, String subtitle){
		for(Player player : this.nearbyPlayers){
			SwissSMPler.get(player).sendTitle(title, subtitle);
		}
	}

	private void broadcastActionBar(String message){
		for(Player player : this.nearbyPlayers){
			SwissSMPler.get(player).sendActionBar(message);
		}
	}

	@Override
	public void payTribute(ItemStack itemStack, Player player) {
		if(itemStack.getType()!=Material.BONE){
			return;
		}
		else if(!player.hasPermission("citysystem.found")){
			return;
		}
		else if(ceremony.isParticipant(player)){
			return;
		}
		ceremony.addParticipant(player);
		FireBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getFire(), 3, Color.fromRGB(255, 200, 20), Color.fromRGB(255, 100, 20));
	}
}
