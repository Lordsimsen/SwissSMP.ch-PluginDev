package ch.swisssmp.craftmmo.mmoentity;


import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.EntityVillager;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class MmoCitizen extends EntityVillager implements IControllable{
		
	public MmoEntitySaveData data;
	public MmoAI mmoAI;
	
	public MmoCitizen(net.minecraft.server.v1_11_R1.World world){
		super(world);
	}
	
	public MmoCitizen(MmoMob template, World world) {
		super(((CraftWorld)world).getHandle());
	    CraftEntity craftEntity = this.getBukkitEntity();
	    Villager villager = (Villager) craftEntity;
	    villager.setRecipes(new ArrayList<MerchantRecipe>());
		MmoEntityUtils.applyDefaultStuffOnCreate(this, template);
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
		MmoEntityUtils.doDefaultSaveStuff(this, nbttagcompound);
	}
	/*
	* Load NBT data
	*/
	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		MmoEntityUtils.doDefaultLoadStuff(this, nbttagcompound);
	}
	@Override
	public void setMmoAI(MmoAI mmoAI) {
		this.mmoAI = mmoAI;
		MmoEntityUtils.applyPathfinderGoals(this.mmoAI, this);
	}
	@Override
	public MmoAI getMmoAI() {
		return this.mmoAI;
	}
	@Override
	public MmoEntitySaveData getSaveData() {
		return this.data;
	}
	@Override
	public EntityInsentient getEntity() {
		return this;
	}
	@Override
	public void setSaveData(MmoEntitySaveData data) {
		this.data = data;
	}
}