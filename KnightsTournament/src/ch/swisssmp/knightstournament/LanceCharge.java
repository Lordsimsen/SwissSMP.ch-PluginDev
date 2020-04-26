package ch.swisssmp.knightstournament;

import ch.swisssmp.utils.*;
import ch.swisssmp.utils.Random;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class LanceCharge implements Runnable{

    private static final int speedEffectInterval = 10;
    private static final int maxSpeedEffect = 4;
    private static final float boostDecayStep = 0.400f;
    private static final Random random = new Random();

    private final static HashMap<UUID,LanceCharge> charges = new HashMap<UUID, LanceCharge>();
    private final Player player;
    private final ItemStack lance;
    private final EquipmentSlot hand;
    private List<Vector> trackedPositions = new ArrayList<Vector>();

    private PotionEffect previousSpeedEffect;

    private int chargeTime = 0;
    private int speedEffectTimeout = 25; //Wait for bow to fully draw
    private int speedEffectLevel = 0;
    private float walkSpeed;
    private boolean finished;
    private boolean completed;
    private boolean applySpeedBuff;

    private BukkitTask task;

    private LanceCharge(Player player, EquipmentSlot hand, ItemStack lance){
        this.player = player;
        this.hand = hand;
        this.lance = lance;
    }

    private void initialize(){
        previousSpeedEffect = player.hasPotionEffect(PotionEffectType.SPEED) ? player.getPotionEffect(PotionEffectType.SPEED) : null;
//        setSpeedEffect(1);
        walkSpeed = player.getWalkSpeed();
//        player.setWalkSpeed(1f);
        applySpeedBuff = player.getVehicle() instanceof AbstractHorse;
    }

    private void setSpeedEffect(int level){
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level, true, false, false));
    }

    @Override
    public void run() {
        chargeTime++;
        if(chargeTime<=100);
//        SwissSMPler.get(player).sendActionBar("Ch" + multiply("a", Mathf.ceilToInt(chargeTime/5f)) + "rge");
        if(applySpeedBuff) {
            speedEffectTimeout--;
            if (speedEffectTimeout <= 0 && speedEffectLevel <= maxSpeedEffect) {
                speedEffectTimeout = speedEffectInterval;
                speedEffectLevel++;
                setSpeedEffect(speedEffectLevel + 2);
            }
        }
        trackedPositions.add(0, player.getLocation().toVector());
        if(trackedPositions.size()>4){
            trackedPositions.remove(trackedPositions.size()-1);
        }
        Location hitBoxCenter = getHitBoxCenter();
        Collection<LivingEntity> entities = checkHitBox(hitBoxCenter, 1);
        if(entities.size()>0) hit(entities);
        if(completed) {
            finish();
        }
    }

    private Collection<LivingEntity> checkHitBox(Location location, double radius){
        Bukkit.getLogger().info("Y:" + location.getY() + "," + radius);
        World world = location.getWorld();
        double radiusSquared = Math.pow(radius, 2);
        Collection<Entity> entities = world.getNearbyEntities(location, radius*2, radius*3, radius*2);
        return entities.stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(livingEntity -> livingEntity.getNoDamageTicks()<=0
                        && !livingEntity.isInvulnerable()
                        && livingEntity.isCollidable()
                        && livingEntity.getPassengers().size()==0
                        && livingEntity.getLocation().distanceSquared(location)<radiusSquared)
                .collect(Collectors.toList());
    }

    private Location getHitBoxCenter(){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();
        double reach = 2;
        return location.add(direction.normalize().multiply(reach));
    }

    private double getPlayerSpeed(){
        double totalDistance = 0;
        double totalWeight = 0;
        double currentWeight = 1;
        for(int i = 0; i < trackedPositions.size()-1; i++){
            Vector a = trackedPositions.get(i);
            Vector b = trackedPositions.get(i+1);
            double distancePassed = b.distance(a);
            totalDistance+=distancePassed*currentWeight;
            totalWeight+=currentWeight;
            currentWeight/=2;
        }
        return totalWeight > 0 ? totalDistance/totalWeight : 0;
    }

    private void hit(Collection<LivingEntity> entities){
        double playerSpeed = getPlayerSpeed();
        double baseDamage = playerSpeed*10;
        if(baseDamage < 0.5) return;
        Location playerLocation = player.getLocation();
        SwissSMPler.get(player).sendActionBar(baseDamage+"");
        for(LivingEntity entity : entities) {
            Vector delta = entity.getLocation().subtract(playerLocation).toVector();
            VectorUtil.rotateY(delta, (random.nextFloat()*2-1)*30);
            int remainincLanceHealth = hit(entity, baseDamage, delta.setY(0).normalize().multiply(playerSpeed*1.8));
            if(remainincLanceHealth==0) break;
        }
    }

    private int hit(LivingEntity entity, double baseDamage, Vector knockback){
        if(entity==player) return 1;
        if(entity==player.getVehicle()) return 1;
        if(entity.getVehicle()==player) return 1;
        double damage = DamageUtil.calculateDamage(baseDamage, entity);
        if(entity instanceof Animals) damage *= 0.4;
        double damageChance = player.getGameMode()!= GameMode.CREATIVE ? 1 : 0;
        if(damageChance > 0 && lance.getItemMeta().hasEnchant(Enchantment.DURABILITY)){
            int level = lance.getEnchantmentLevel(Enchantment.DURABILITY);
            damageChance = 1.0/(level+1);
        }
        boolean lanceTookDamage = random.nextDouble() < damageChance;
        int durabilityLoss = lanceTookDamage ? 1 : 0;
        boolean chargeEnds = lanceTookDamage;
        EntityDamageByLanceAttackEvent event = new EntityDamageByLanceAttackEvent(player, lance, durabilityLoss, knockback, chargeEnds, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return 1;
        durabilityLoss = event.getLanceDurabilityLoss();
        chargeEnds = event.getChargeEnds();
        damage = event.getFinalDamage();
        int remaining = chargeEnds ? applyLanceDamage(durabilityLoss) : 1;
        entity.setVelocity(knockback);
        entity.damage(damage);
        entity.setNoDamageTicks(10);
        if(chargeEnds) completed = true;
        return remaining;
    }

    private int applyLanceDamage(int amount){
        ItemMeta lanceMeta = lance.getItemMeta();
        if(!(lanceMeta instanceof Damageable)) return -1;
        Damageable damageable = (Damageable) lanceMeta;
        int currentDamage = damageable.getDamage();
        int maxDamage = lance.getType().getMaxDurability();
        damageable.setDamage(currentDamage+amount);
        lance.setItemMeta(lanceMeta);
        int remaining = maxDamage - currentDamage;
        if(remaining <= 0) lance.setAmount(0);
        return Math.max(remaining, 0);
    }


    private String multiply(String s, int amount){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < amount; i++){
            result.append(s);
        }
        return result.toString();
    }

    public ItemStack getLance(){
        return lance;
    }

    public EquipmentSlot getHand(){
        return hand;
    }

    public void cancel(){
        finish();
    }

    private void finish(){
        if(finished) return;
        finished = true;
        if(task!=null) task.cancel();
        charges.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.SPEED);
        if(previousSpeedEffect!=null && previousSpeedEffect.getDuration()>chargeTime){
            player.addPotionEffect(new PotionEffect(previousSpeedEffect.getType(),
                            previousSpeedEffect.getDuration()-chargeTime,
                            previousSpeedEffect.getAmplifier(),
                            previousSpeedEffect.isAmbient(),
                            previousSpeedEffect.hasParticles(),
                            previousSpeedEffect.hasIcon())
                            );
        }
        player.setWalkSpeed(walkSpeed);
    }

    protected void complete(){
        completed = true;
    }

    protected static Optional<LanceCharge> get(UUID id){
        return charges.containsKey(id) ? Optional.of(charges.get(id)) : Optional.empty();
    }

    protected static void cancel(UUID id){
        LanceCharge charge = get(id).orElse(null);
        if(charge!=null) charge.cancel();
    }

    protected static void cancelAll(){
        for(LanceCharge charge : charges.values()){
            charge.cancel();
        }
    }

    protected static LanceCharge initiate(Player player, EquipmentSlot hand, ItemStack lance){
        LanceCharge existing = charges.get(player.getUniqueId());
        if(existing!=null) return existing;
        LanceCharge charge = new LanceCharge(player, hand, lance);
        charge.task = Bukkit.getScheduler().runTaskTimer(KnightsTournamentPlugin.getInstance(), charge, 0L, 1L);
        charge.initialize();
        charges.put(player.getUniqueId(), charge);
        return charge;
    }
}
