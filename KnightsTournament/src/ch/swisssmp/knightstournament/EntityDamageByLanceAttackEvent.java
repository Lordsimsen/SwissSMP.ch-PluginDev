package ch.swisssmp.knightstournament;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EntityDamageByLanceAttackEvent extends EntityDamageByEntityEvent {

    private static HandlerList handlers = new HandlerList();

    private final Player damager;
    private final ItemStack lance;
    private final Vector hitVector;

    private boolean chargeEnds;

    private int lanceDurabilityLoss;

    public EntityDamageByLanceAttackEvent(Player damager, ItemStack lance, int lanceDurabilityLoss, Vector hitVector, boolean chargeEnds, Entity damagee, DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
        this.damager = damager;
        this.lance = lance;
        this.lanceDurabilityLoss = lanceDurabilityLoss;
        this.hitVector = hitVector;
        this.chargeEnds = chargeEnds;
    }

    public Player getDamager(){
        return damager;
    }

    public ItemStack getLance(){
        return lance;
    }

    public Vector getHitVector(){
        return hitVector;
    }

    public int getLanceDurabilityLoss(){
        return lanceDurabilityLoss;
    }

    public void setLanceDurabilityLoss(int durabilityLoss){
        lanceDurabilityLoss = durabilityLoss;
    }

    public boolean getChargeEnds(){
        return chargeEnds;
    }

    public void setChargeEnds(boolean chargeEnds){
        this.chargeEnds = chargeEnds;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }




}
