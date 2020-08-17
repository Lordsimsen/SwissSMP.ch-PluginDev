package ch.swisssmp.city.ceremony.founding.phases;

import java.util.Collection;
import java.util.List;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.utils.PlayerData;

public class PresentRingPhase extends Phase {
	
	private final CityFoundingCeremony ceremony;
	
	private long time = 0;
	
	private boolean foundingRequestSent = false;
	private boolean cityFounded = false;
	
	public PresentRingPhase(CityFoundingCeremony ceremony){
		this.ceremony = ceremony;
	}
	
	@Override
	public void begin(){
		super.begin();
		this.createCity();
		playMusicFinale();
	}
	
	private void announceTitleLong(String title, String subtitle){
		for(Player player : ceremony.getParticipants()){
			player.sendTitle(title, subtitle, 10, 120, 30);
		}
	}
	
	private void createCity(){
		if(foundingRequestSent) return;
		foundingRequestSent = true;
		String name = ceremony.getCityName();
		Player mayor = ceremony.getInitiator();
		Collection<Player> founders = ceremony.getParticipants();
		SigilRingType ringType = ceremony.getRingType();
		Block origin = ceremony.getFire();
		long time = origin.getWorld().getTime();
		CitySystem.createCity(name, mayor, founders, ringType, origin, time, this::sendFoundingFeedback);
	}
	
	private void sendFoundingFeedback(City city){
		if(city==null){
			ceremony.broadcast(ChatColor.RED+"Es ist etwas §cschiefgelaufen und die §cStadt konnte nicht §cgegründet werden.§c §cBitte kontaktiert §cdie §cSpielleitung.");
			ceremony.cancel();
			return;
		}

		String title = "Gratulation!";
		String subtitle = ChatColor.YELLOW+"Du hast die Stadt "+ChatColor.LIGHT_PURPLE+ceremony.getCityName()+ChatColor.YELLOW+" gegründet.";
		this.announceTitleLong(title, subtitle);

		this.ceremony.broadcast(ChatColor.GREEN+"Du bist nun Gründer von "+ceremony.getCityName()+"!");
		
		StringBuilder playersString = new StringBuilder();
		List<Player> participants = ceremony.getParticipants();
		for(int i = 0; i < participants.size(); i++){
			Player player = participants.get(i);
			String name = player.getName();
			if(i==0) playersString.append(name);
			else if(i<participants.size()-1) playersString.append(", ").append(name);
			else playersString.append(" und ").append(name);
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(CitySystemPlugin.getPrefix()+" "+ChatColor.GREEN+playersString+" haben die Stadt "+ceremony.getCityName()+" gegründet!");
		}
		super.complete();
		try{
			this.giveRings(ceremony.getRingType(), city);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		String code = Base64.encodeBase64URLSafeString(("techtree_"+city.getTechtreeId()+","+city.getUniqueId()).getBytes());
		ceremony.getInitiator().sendMessage(CitySystemPlugin.getPrefix()+ChatColor.YELLOW+" Folge dieser Anleitung, um den Techtree im Forum zu aktivieren:");
		ceremony.getInitiator().sendMessage(ChatColor.YELLOW+""+ChatColor.UNDERLINE+"https://minecraft.swisssmp.ch/techtree-code?code="+code);
		cityFounded = true;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
	}

	@Override
	public void run() {
		time++;
		if(time>=60){ // && cityFounded){
			setCompleted();
		}
	}
	
	private void playMusicFinale(){
		Block block = ceremony.getFire();
		for(Player player : ceremony.getParticipants()){
			player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
		}
		block.getWorld().playSound(block.getLocation(), "founding_ceremony_finale", 15, 1);
	}
	
	private void giveRings(SigilRingType ringType, City city){
		for(Player player : this.ceremony.getParticipants()){
			ItemStack ring = ringType.createItemStack(city, PlayerData.get(player));
			if(player.getInventory().firstEmpty()<0){
				player.getWorld().dropItem(player.getEyeLocation(), ring);
				return;
			}
			player.getInventory().addItem(ring);
		}
	}
}
