package ch.swisssmp.craftmmo.mmoentity;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityPigZombie;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class MmoPigZombie extends EntityPigZombie implements IControllable{
	
	public MmoEntitySaveData data;
	public MmoAI mmoAI;
	
	public MmoPigZombie(net.minecraft.server.v1_12_R1.World world){
		super(world);
	}
	
	public MmoPigZombie(MmoMob template, World world) {
		super(((CraftWorld)world).getHandle());
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
