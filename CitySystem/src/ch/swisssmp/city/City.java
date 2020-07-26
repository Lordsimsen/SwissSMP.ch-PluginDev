package ch.swisssmp.city;

import java.util.*;

import ch.swisssmp.utils.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class City {
	private final UUID uid;
	private final String techtreeId;
	
	private String name;
	private String levelId;
	private String ringType;
	private UUID mayor;
	private final List<Citizen> citizens = new ArrayList<Citizen>();
	private final HashSet<UUID> founders = new HashSet<UUID>();
	
	private City(UUID uid, String techtreeId){
		this.uid = uid;
		this.techtreeId = techtreeId;
	}
	
	public UUID getUniqueId(){
		return uid;
	}
	
	public String getTechtreeId(){
		return techtreeId;
	}
	
	public String getName(){
		return name;
	}

	public String getCityLevelId() { return levelId; }
	
	public String getRingType(){
		return ringType;
	}
	
	public UUID getMayor(){
		return mayor;
	}
	
	public Citizen getCitizen(String name){
		if(name==null) return null;
		for(Citizen citizen : this.citizens){
			if(citizen.getName()==null) continue;
			if(citizen.getName().toLowerCase().equals(name.toLowerCase())) return citizen;
		}
		return null;
	}
	
	public void broadcast(String message){
		for(Citizen citizen : this.citizens){
			Player player = Bukkit.getPlayer(citizen.getUniqueId());
			if(player==null) continue;
			player.sendMessage(message);
		}
	}

	public void promoteCity(String newLevelId){
		this.levelId = newLevelId;
		DataSource.getResponse(CitySystemPlugin.getInstance(), "promote_city.php", new String[]{
			"city_id="+this.uid.toString(),
			"level_id="+newLevelId
		});
	}
	
	public HTTPRequest addCitizen(Player player, Player parent, String role){
		if(isCitizen(player.getUniqueId()) || (!isCitizen(parent.getUniqueId()) && !parent.hasPermission("citysystem.admin"))) return null;
		HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "add_citizen.php", new String[]{
			"city_id="+this.uid,
			"player_uuid="+player.getUniqueId(),
			"parent_uuid="+parent.getUniqueId(),
			"role="+URLEncoder.encode(role!=null ? role : ""),
			"world="+URLEncoder.encode(Bukkit.getWorlds().get(0).getName())
		});
		request.onFinish(()->{
			JsonObject json = request.getJsonResponse();
			if(json!=null && json.has("citizen")){
				Citizen citizen = Citizen.get(json.getAsJsonObject("citizen"));
				if(citizen.getRank()==CitizenRank.CITIZEN && founders.contains(citizen.getUniqueId())){
					citizen.setRank(CitizenRank.FOUNDER);
				}
				citizens.add(citizen);
				player.sendMessage(CitySystemPlugin.getPrefix()+parent.getDisplayName()+ChatColor.GREEN+" hat dich in "+name+" aufgenommen!");
				parent.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GREEN+"Du hast "+player.getDisplayName()+ChatColor.GREEN+" aufgenommen!");
				ItemManager.updateItems();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
				return;
			}
			else if(json!=null && json.has("result")){
				switch(JsonUtil.getString("result", json)){
				case "limit_reached":
					parent.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+player.getDisplayName()+ChatColor.RED+" kann nicht einer weiteren Stadt beitreten.");
					break;
				case "already_citizen":
					parent.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GRAY+player.getDisplayName()+ChatColor.GRAY+" ist bereits Bürger.");
					break;
				default:
					parent.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Konnte "+player.getDisplayName()+ChatColor.RED+" nicht aufnehmen. (Unbekannter Fehler)");
					break;
				}
			}
			else{
				parent.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Konnte "+player.getDisplayName()+ChatColor.RED+" nicht aufnehmen. (Systemfehler)");
			}
		});
		return request;
	}
	
	public HTTPRequest removeCitizen(Player responsible, UUID player_uuid){
		Citizen citizen = this.getCitizen(player_uuid);
		if(citizen ==null) return null;
		HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "remove_citizen.php", new String[]{
			"city_id="+this.uid,
			"player="+player_uuid.toString()
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("result")) return;
			String result = yamlConfiguration.getString("result");
			if(!result.equals("success")) return;
			citizens.remove(citizen);
			ItemManager.updateItems();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
			Player player = Bukkit.getPlayer(player_uuid);
			if(player==null || player!=responsible) return;
			player.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Du wurdest aus "+this.getName()+" ausgeschlossen.");
		});
		return request;
	}
	
	public Citizen getCitizen(UUID player_uuid){
		for(Citizen citizen : this.citizens){
			if(citizen.getUniqueId().equals(player_uuid)) return citizen;
		}
		return null;
	}
	
	public Collection<Citizen> getCitizens(){
		return new ArrayList<Citizen>(citizens);
	}
	
	public HTTPRequest setCitizenRole(Player responsible, UUID player_uuid, String role){
		if(player_uuid==null || role==null || !isCitizen(player_uuid)) return null;
		if(!isCitizen(responsible.getUniqueId()) && !responsible.hasPermission("citysystem.admin")) return null;
		
		Citizen citizen = this.getCitizen(player_uuid);
		String newRole;
		role = role.replaceAll("(§[a-z0-9])", "");
		if(role.toLowerCase().startsWith("kein titel")){
			newRole = "";
		}
		else if(role.toLowerCase().equals("bürgermeister") || role.toLowerCase().equals("buergermeister")){
			newRole = "Bürgermeister";
		}
		else{
			newRole = role;
		}
		HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "set_citizen_role.php", new String[]{
			"city_id="+this.uid,
			"player="+player_uuid,
			"responsible="+responsible.getUniqueId().toString(),
			"responsible_admin="+(responsible.hasPermission("citysystem.admin") ? "true" : "false"),
			"role="+URLEncoder.encode(newRole)
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			setCitizenRole(responsible, citizen, newRole, yamlConfiguration);
		});
		return request;
	}
	
	private void setCitizenRole(Player responsible, Citizen citizen, String role, YamlConfiguration yamlConfiguration){
		if(yamlConfiguration==null || !yamlConfiguration.contains("result")){
			responsible.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Konte den Titel nicht setzen. (Systemfehler)");
			return;
		}
		UUID player_uuid = citizen.getUniqueId();
		switch(yamlConfiguration.getString("result")){
		case "not_mayor":
			responsible.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Nur der Bürgermeister kann diesen Titel verleihen.");
			break;
		case "success":
			String previous = citizen.getRole();
			citizen.setRole(role);
			String actor = !responsible.getUniqueId().equals(citizen.getUniqueId()) ? responsible.getDisplayName() : null;
			Player citizenPlayer = Bukkit.getPlayer(player_uuid);
			if(citizenPlayer!=null){
				if(!role.isEmpty()){
					if(actor==null) actor = ChatColor.GREEN+"Du hast";
					else actor+=ChatColor.GREEN+" hat";
					SwissSMPler.get(citizenPlayer).sendMessage(CitySystemPlugin.getPrefix()+actor+ChatColor.GREEN+" dir den Titel "+role+" verliehen!");
				}
				else if(!previous.isEmpty()){
					if(actor==null) actor = ChatColor.GRAY+"Du hast";
					else actor+=ChatColor.GRAY+" hat";
					SwissSMPler.get(citizenPlayer).sendMessage(CitySystemPlugin.getPrefix()+actor+ChatColor.GRAY+" deinen Titel "+previous+" entfernt.");
				}
			}
			if(role.equals("Bürgermeister")){
				this.setMayor(citizen.getUniqueId());
			}
			ItemManager.updateItems();
			break;
		default:
			responsible.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Konte den Titel nicht setzen. (Unbekannter Fehler)");
			break;
		}
	}
	
	private void setMayor(UUID mayor){
		Citizen prevMayor = this.getCitizen(this.mayor);
		if(prevMayor!=null) prevMayor.setRank(this.isFounder(this.mayor) ? CitizenRank.FOUNDER : CitizenRank.CITIZEN);
		this.mayor = mayor;
		Citizen newMayor = this.getCitizen(this.mayor);
		if(newMayor!=null) newMayor.setRank(CitizenRank.MAYOR);
	}
	
	public boolean isMayor(UUID player_uuid){
		return this.mayor.equals(player_uuid);
	}
	
	public boolean isFounder(UUID player_uuid){
		for(UUID founder : this.founders){
			if(!founder.equals(player_uuid)) continue;
			return true;
		}
		return false;
	}
	
	public boolean isCitizen(UUID player_uuid){
		return this.getCitizen(player_uuid)!=null;
	}

	private void loadData(JsonObject json){

		this.name = JsonUtil.getString("name", json);
		this.levelId = JsonUtil.getString("level_id", json);
		this.ringType = JsonUtil.getString("ring_type", json);
		try{
			this.mayor = UUID.fromString(JsonUtil.getString("mayor", json));
		}
		catch(Exception e){
			this.mayor = null;
		}
		JsonArray citizensArray = json.has("citizens") ? json.getAsJsonArray("citizens") : null;
		if(citizensArray!=null){
			for(JsonElement element : citizensArray){
				if(!element.isJsonObject()) continue;
				JsonObject citizenSection = element.getAsJsonObject();
				Citizen info = Citizen.get(citizenSection);
				if(info==null) continue;
				citizens.add(info);
			}
		}
		List<String> foundersList = JsonUtil.getStringList("founders", json);
		if(foundersList!=null){
			for(String founder : foundersList){
				try{
					founders.add(UUID.fromString(founder));
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	protected static Optional<City> load(JsonObject json){
		if(json==null) return Optional.empty();
		UUID uid = JsonUtil.getUUID("city_id", json);
		String techtreeId = JsonUtil.getString("techtree_id", json);
		if(uid==null) return null;
		City city = new City(uid, techtreeId);
		city.loadData(json);
		return Optional.of(city);
	}
}
