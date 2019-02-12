package ch.swisssmp.city.ceremony.founding.phases;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ceremony.Ceremony;
import ch.swisssmp.city.ceremony.Phase;
import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.city.ceremony.founding.FireBurstEffect;

public class BeginPhase extends Phase {
	
	private final CityFoundingCeremony ceremony;
	
	public BeginPhase(CityFoundingCeremony ceremony){
		this.ceremony = ceremony;
	}
	
	@Override
	public void begin(){
		super.begin();
		Color color = Color.fromRGB(255, 200, 20);
		FireBurstEffect burstEffect = FireBurstEffect.play(ceremony.getFire(), 5, color, color);
		burstEffect.addOnFinishListener(()->{
			setCompleted();
		});
		loadMusic();
		startShaker();
		Block fire = ceremony.getFire();
		Player initiator = ceremony.getInitiator();
		for(Player player : Bukkit.getWorlds().get(0).getPlayers()){
			if(Ceremony.isParticipantAnywhere(player)) continue;
			if(fire.getWorld()!=player.getWorld() || fire.getLocation().distanceSquared(player.getLocation())<10000) continue;
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+player.getName()+" {\"text\":\"\",\"extra\":[{\"text\":\"[\u00A7cStädtesystem\u00A7r] \"},{\"text\":\""+initiator.getDisplayName()+" \u00A7r\u00A7a \u00A7ahat \u00A7aeine \u00A7aGründungszeremonie \u00A7agestartet! \u00A7aSchaue \u00A7amit \"},{\"text\":\"\u00A7e/zuschauen\u00A7r\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/zuschauen "+initiator.getName()+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Dieser Zeremonie zuschauen\"}},{\"text\":\"\u00A7a zu.\"}]}");
		}
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
