package ch.swisssmp.adventuredungeons.mmoentity;

import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ch.swisssmp.adventuredungeons.mmoshop.MmoShop;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class MmoMerchantAgent extends EntityVillager{
	
	public MmoMerchantAgent(MmoShop shop, Player player) {
		super(((CraftWorld)player.getWorld()).getHandle());
	    CraftEntity craftEntity = this.getBukkitEntity();
		if(shop==null){
			craftEntity.remove();
		}
	    Villager villager = (Villager) craftEntity;
	    villager.setRecipes(shop.trades);
		villager.setSilent(true);
		villager.setInvulnerable(true);
		villager.setGravity(false);
		villager.setCustomName(shop.name);
		PotionEffect invisibleEffect = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
		villager.addPotionEffect(invisibleEffect);
	}
	public MmoMerchantAgent(net.minecraft.server.v1_12_R1.World world){
		super(world);
		this.getBukkitEntity().remove();
	}
	@Override
	protected void r(){
	}
	/*
	* Save NBT data
	*/
	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
	}
	/*
	* Load NBT data
	*/
	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
	}
	public void remove(){
		this.getBukkitEntity().remove();
	}
}