package ch.swisssmp.city.ceremony.founding.phases;

import ch.swisssmp.ceremonies.Ceremonies;
import ch.swisssmp.ceremonies.CeremoniesPlugin;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.ceremonies.effects.FireBurstEffect;
import ch.swisssmp.text.RawBase;
import ch.swisssmp.text.RawText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;

public class BeginPhase extends Phase {
	
	private final CityFoundingCeremony ceremony;
	
	public BeginPhase(CityFoundingCeremony ceremony){
		this.ceremony = ceremony;
	}
	
	@Override
	public void begin(){
		super.begin();
		Color color = Color.fromRGB(255, 200, 20);
		FireBurstEffect burstEffect = FireBurstEffect.play(CitySystemPlugin.getInstance(), ceremony.getFire(), 5, color, color);
		burstEffect.addOnFinishListener(this::setCompleted);
		loadMusic();
		startShaker();
		Block fire = ceremony.getFire();
		Player initiator = ceremony.getInitiator();
		RawBase spectateMessage = new RawText(new RawText(CitySystemPlugin.getPrefix())
				, new RawText(" "+initiator.getDisplayName())
				, new RawText(" hat eine Gründungszeremonie gestartet!")).color(ChatColor.GREEN);
		ceremony.broadcastSpectatorCommand(spectateMessage, initiator.getName(), (player)->fire.getLocation().distanceSquared(player.getLocation())>10000);
	}
	
	private void loadMusic(){
		Block block = ceremony.getFire();
		block.getWorld().playSound(block.getLocation(), "founding_ceremony_drums", SoundCategory.RECORDS, 0.01f, 1);
		Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
			for(Player player : block.getWorld().getPlayers()){
				player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
			}
		}, 2L);
	}
	
	private void startShaker(){
		Block block = ceremony.getFire();
		ceremony.setMusic(block.getLocation(), "founding_ceremony_shaker", 80L);
	}

	@Override
	public void run() {
		//wait for fancy particles to finish
	}
}
