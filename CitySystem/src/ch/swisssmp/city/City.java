package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.swisssmp.utils.*;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class City {
    private final UUID uid;
    private final String techtreeId;

    private String name;
    private String ringType;
    private UUID mayor;
    private final HashSet<UUID> founders = new HashSet<UUID>();

    private City(UUID uid, String techtreeId) {
        this.uid = uid;
        this.techtreeId = techtreeId;
    }

    public UUID getUniqueId() {
        return uid;
    }

    public String getTechtreeId() {
        return techtreeId;
    }

    public Techtree getTechtree() {
        return CitySystem.getTechtree(techtreeId).orElse(null);
    }

    public String getName() {
        return name;
    }

    public boolean hasLevel(CityLevel level) {
        return CitySystem.checkCityLevel(this, level);
    }

    public boolean hasLevel(String levelId){
        return CitySystem.checkCityLevel(this, levelId);
    }

    public String getRingType() {
        return ringType;
    }

    public UUID getMayor() {
        return mayor;
    }

    public Optional<Citizenship> getCitizenship(String name) {
        return Citizenships.getCitizenship(uid, name);
    }

    public Optional<Citizenship> getCitizenship(UUID playerUid) {
        return Citizenships.getCitizenship(uid, playerUid);
    }

    public Collection<Addon> getAddons(){
        return Addons.getAll(this);
    }

    public Optional<Addon> getAddon(String addonId) {
        return CitySystem.getAddon(uid, addonId);
    }

    public Optional<Addon> getAddon(AddonType type) {
        return getAddon(type.getAddonId());
    }

    public Addon createAddon(AddonType type) {
        return createAddon(type.getAddonId());
    }

    public Addon createAddon(String addonId) {
        Addon existing = getAddon(addonId).orElse(null);
        if (existing != null) return existing;
        Addon addon = new Addon(addonId, uid);
        Addons.add(addon);
        return addon;
    }

    public void broadcast(String message) {
        getCitizenships().forEach((citizenship)->SwissSMPler.get(citizenship.getUniqueId()).sendMessage(message));
    }

    public void unlockLevel(String levelId, Consumer<Boolean> callback){
        Techtree techtree = this.getTechtree();
        CityLevel level = techtree!=null ? techtree.getLevel(levelId).orElse(null) : null;
        if(level==null){
            callback.accept(false);
            return;
        }
        unlockLevel(level, callback);
    }

    public void unlockLevel(CityLevel level, Consumer<Boolean> callback) {
        CitySystem.unlockCityLevel(uid, level.getTechtree().getId(), level.getId(), callback);
    }

    public void lockLevel(CityLevel level, Consumer<Boolean> callback) {
        CitySystem.lockCityLevel(uid, level.getTechtree().getId(), level.getId(), callback);
    }

    public void addCitizen(Player player, Player parent, String role, Consumer<Boolean> callback) {
        if (isCitizen(player.getUniqueId()) || (!isCitizen(parent.getUniqueId()) && !parent.hasPermission(CitySystemPermission.ADMIN))){
            callback.accept(false);
            return;
        }

        Citizenship citizenship = new Citizenship(uid, PlayerData.get(player), isFounder(player) ? CitizenRank.FOUNDER : CitizenRank.CITIZEN, parent.getUniqueId(), role);
        citizenship.save((success)->{
            if(success){
                citizenship.announceCitizenshipAwarded(parent);
                parent.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Du hast " + player.getDisplayName() + ChatColor.GREEN + " aufgenommen!");
                ItemUtility.updateItems();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
            }
            else{
                parent.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Konnte " + player.getDisplayName() + ChatColor.RED + " nicht aufnehmen. (Systemfehler)");
            }
            callback.accept(success);
        });
    }

    public void removeCitizen(Player player, Consumer<Boolean> callback){
        removeCitizen(player.getUniqueId(), callback);
    }

    public void removeCitizen(UUID playerUid, Consumer<Boolean> callback){
        Citizenship citizenship = getCitizenship(playerUid).orElse(null);
        if(citizenship==null){
            if(callback!=null) callback.accept(false);
            return;
        }

        removeCitizen(citizenship, callback);
    }

    public void removeCitizen(Citizenship citizenship, Consumer<Boolean> callback){
        citizenship.remove(callback);
    }

    public Collection<Citizenship> getCitizenships() {
        return Citizenships.getAllCitizenships(uid);
    }

    public void setMayor(UUID mayor) {
        UUID previousMayor = this.mayor;
        this.mayor = mayor;
        getCitizenship(previousMayor).ifPresent(prevMayor -> prevMayor.setRank(isFounder(previousMayor) ? CitizenRank.FOUNDER : CitizenRank.CITIZEN));
        getCitizenship(mayor).ifPresent(newMayor -> newMayor.setRank(CitizenRank.MAYOR));
    }

    public boolean isMayor(Player player){
        return isMayor(player.getUniqueId());
    }

    public boolean isMayor(UUID playerUid) {
        return this.mayor.equals(playerUid);
    }

    public boolean isFounder(Player player){
        return isFounder(player.getUniqueId());
    }

    public boolean isFounder(UUID playerUid) {
        return founders.stream().anyMatch(f->f.equals(playerUid));
    }

    public boolean isCitizen(Player player){
        return CitySystem.getCitizenship(uid, player).isPresent();
    }

    public boolean isCitizen(UUID playerUid) {
        return CitySystem.getCitizenship(uid, playerUid).isPresent();
    }

    public void save(){
        save(null);
    }

    public void save(Consumer<Boolean> callback){
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Arrays.asList("city_id=" + uid,
                "name=" + name,
                "ring_type=" + ringType,
                "mayor=" + mayor));
        arguments.addAll(founders.stream().map(f->"founders[]="+f).collect(Collectors.toList()));
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.SAVE_CITY, arguments.toArray(new String[0]));
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = (json!=null && json.has("success") && JsonUtil.getBool("success", json));
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }

            if(callback!=null) callback.accept(success);
        });
    }

    public void reload(){
        reload(null);
    }

    public void reload(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.GET_CITY, new String[]{
                "city_id="+ uid
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = (json!=null && json.has("success") && JsonUtil.getBool("success", json));
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            }
            if(json!=null && json.has("city")){
                loadData(json.getAsJsonObject("city"));
            }
            if(callback!=null) callback.accept(success);
        });
    }

    public void delete(Consumer<Boolean> callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.REMOVE_CITY, new String[]{
                "city=" + this.uid
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
                return;
            }
            if(success){
                Cities.remove(this);
            }
            if(callback!=null) callback.accept(success);
        });
    }

    private void loadData(JsonObject json) {

        this.name = JsonUtil.getString("name", json);
        this.ringType = JsonUtil.getString("ring_type", json);
        try {
            this.mayor = UUID.fromString(JsonUtil.getString("mayor", json));
        } catch (Exception e) {
            this.mayor = null;
        }
        List<String> foundersList = JsonUtil.getStringList("founders", json);
        if (foundersList != null) {
            for (String founder : foundersList) {
                try {
                    founders.add(UUID.fromString(founder));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static Optional<City> load(JsonObject json) {
        if (json == null) return Optional.empty();
        UUID uid = JsonUtil.getUUID("city_id", json);
        String techtreeId = JsonUtil.getString("techtree_id", json);
        if (uid == null) return Optional.empty();
        City city = new City(uid, techtreeId);
        city.loadData(json);
        return Optional.of(city);
    }

    protected static void create(String name, Player mayor, Collection<Player> founders, SigilRingType ringType, Block origin, long time, Consumer<City> callback){
        List<String> founderNames = new ArrayList<String>();
        for(Player player : founders){
            founderNames.add("founders[]="+player.getUniqueId().toString());
        }
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), CitySystemUrl.CREATE_CITY, new String[]{
                "name="+URLEncoder.encode(name),
                "mayor="+mayor.getUniqueId().toString(),
                "world="+URLEncoder.encode(origin.getWorld().getName()),
                "place[x]="+origin.getX(),
                "place[y]="+origin.getY(),
                "place[z]="+origin.getZ(),
                "time="+time,
                "ring="+URLEncoder.encode(ringType.toString().toLowerCase()),
                String.join("&", founderNames)
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = json!=null && JsonUtil.getBool("success", json);
            String message = json!=null ? JsonUtil.getString("message", json) : null;
            if(message!=null) Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+message);
            City city = json!=null && success && json.has("city") ? load(json.getAsJsonObject("city")).orElse(null) : null;
            callback.accept(city);
        });
    }
}
