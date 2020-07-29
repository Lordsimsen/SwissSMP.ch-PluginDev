package ch.swisssmp.city;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.swisssmp.utils.*;
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
    private final HashSet<UUID> founders = new HashSet<UUID>();
    private final HashSet<Addon> addons = new HashSet<>();

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

    public String getLevelId() {
        return levelId;
    }

    public CityLevel getLevel() {
        Techtree techtree = CitySystem.getTechtree(techtreeId).orElse(null);
        return techtree != null ? techtree.getLevel(levelId).orElse(null) : null;
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

    public Optional<Addon> getAddon(String addonId) {
        return addons.stream().filter(a -> a.getAddonId().equals(addonId)).findAny();
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
        addons.add(addon);
        return addon;
    }

    public void broadcast(String message) {
        getCitizenships().forEach((citizenship)->SwissSMPler.get(citizenship.getUniqueId()).sendMessage(message));
    }

    public boolean setLevel(String levelId) {
        Techtree techtree = getTechtree();
        if (techtree == null || !techtree.getLevel(levelId).isPresent()) return false;
        this.levelId = levelId;
        DataSource.getResponse(CitySystemPlugin.getInstance(), "set_city_level.php", new String[]{
                "city_id=" + this.uid.toString(),
                "level_id=" + URLEncoder.encode(levelId)
        });
        return true;
    }

    public boolean promoteCity() {
        Techtree techtree = getTechtree();
        int currentLevel = techtree.getLevelIndex(levelId);
        if (currentLevel + 1 >= techtree.getLevels().size()) return false;
        String newLevelId = techtree.getLevel(currentLevel + 1).getId();
        this.setLevel(newLevelId);
        return true;
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
                parent.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + "Du hast " + player.getDisplayName() + ChatColor.GREEN + " aufgenommen!");
                ItemManager.updateItems();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
            }
            else{
                parent.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + "Konnte " + player.getDisplayName() + ChatColor.RED + " nicht aufnehmen. (Systemfehler)");
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

    public Collection<String> getZones(){
        return Collections.singletonList(name.toLowerCase());
    }

    public void save(){
        save(null);
    }

    public void save(Consumer<Boolean> callback){
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Arrays.asList("city_id=" + uid,
                "name=" + name,
                "level=" + levelId,
                "ring_type=" + ringType,
                "mayor=" + mayor));
        arguments.addAll(founders.stream().map(f->"founders[]="+f).collect(Collectors.toList()));
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "save_city.php", arguments.toArray(new String[0]));
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            boolean success = (json!=null && json.has("success") && JsonUtil.getBool("success", json));
            if(json!=null && json.has("message")){
                Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" "+JsonUtil.getString("message", json));
            }

            if(callback!=null) callback.accept(success);
        });
    }

    public void reload(){
        reload(null);
    }

    public void reload(Runnable callback){
        HTTPRequest request = DataSource.getResponse(CitySystemPlugin.getInstance(), "get_city.php", new String[]{
                "city_id="+ uid
        });
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            if(json!=null && json.has("city")){
                loadData(json.getAsJsonObject("city"));
            }
            if(callback!=null) callback.run();
        });
    }

    private void loadData(JsonObject json) {

        this.name = JsonUtil.getString("name", json);
        this.levelId = JsonUtil.getString("level", json);
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
}
