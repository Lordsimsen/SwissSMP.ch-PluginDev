package ch.swisssmp.knightstournament;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LoanerEquipment {

    private final Player player;
    private AbstractHorse horse;
    private ItemStack lance;
    private ItemStack shield;

    private LoanerEquipment(Player player){
        this.player = player;
    }

    private void initialize(){

        LoanerData.of(player).save();

        CustomItemBuilder lanceBuilder = CustomItems.getCustomItemBuilder(TournamentLance.bareCustomEnum);
        ItemStack lance = lanceBuilder.build();
        this.lance = lance;
        this.shield = new ItemStack(Material.SHIELD);
        player.getInventory().clear();
        player.getInventory().setItemInMainHand(lance);
        player.getInventory().setItemInOffHand(shield);

        AbstractHorse horse = (AbstractHorse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getPersistentDataContainer().set(getLoanerHorseKey(), PersistentDataType.SHORT, (short) 1);
        horse.addPassenger(player);
        // check that the horse is wearing a saddle a bit later because the inventory packet sometimes doesn't reach
        // the client when there is a bit of lag
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), ()->{
            if(!horse.isValid() || horse.getInventory().getSaddle()!=null) return;
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        }, 20L);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_ARMOR, SoundCategory.NEUTRAL, 0.2f, 1);
        this.horse = horse;
    }

    public Player getPlayer(){
        return player;
    }

    public AbstractHorse getHorse(){
        return horse;
    }

    public ItemStack getLance(){
        return lance;
    }

    public ItemStack getShield(){
        return shield;
    }

    protected static void give(Player player){
        LoanerData existing = LoanerData.load(player).orElse(null);
        if(existing!=null){
            return;
        }
        LoanerEquipment equipment = new LoanerEquipment(player);
        equipment.initialize();
    }

    public static NamespacedKey getLoanerHorseKey(){
        return new NamespacedKey(KnightsTournamentPlugin.getInstance(), "loaner");
    }

    public static boolean has(Player player){
        return player.getVehicle() != null &&
                player.getVehicle() instanceof AbstractHorse &&
                isLoaner((AbstractHorse) player.getVehicle());

    }

    public static boolean isLoaner(AbstractHorse horse){
        return horse.getPersistentDataContainer()
                .has(LoanerEquipment.getLoanerHorseKey(), PersistentDataType.SHORT);
    }

    public static void reset(Player player){
        LoanerData loanerData = LoanerData.load(player).orElse(null);
        if(loanerData!=null){
            loanerData.apply(player);
            loanerData.delete();
        }
    }
    public static void resetAll(){
        for(Player player : Bukkit.getOnlinePlayers()){
            reset(player);
        }
    }
}
