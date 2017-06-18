package ch.swisssmp.craftmmo.mmoentity;

import net.minecraft.server.v1_12_R1.EntityInsentient;

public interface IControllable {
	public EntityInsentient getEntity();
	public void setSaveData(MmoEntitySaveData data);
	public MmoEntitySaveData getSaveData();
	public void setMmoAI(MmoAI mmoAI);
	public MmoAI getMmoAI();
}
