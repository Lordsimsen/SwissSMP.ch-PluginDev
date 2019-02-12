package ch.swisssmp.utils.nbt;

public class NBTTagString extends NBTBase {
	
	net.minecraft.server.v1_13_R2.NBTTagString value;
	
	protected NBTTagString(net.minecraft.server.v1_13_R2.NBTTagString value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagString asNMS() {
		return value;
	}
}
