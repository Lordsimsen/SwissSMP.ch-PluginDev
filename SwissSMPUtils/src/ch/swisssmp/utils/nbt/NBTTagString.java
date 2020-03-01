package ch.swisssmp.utils.nbt;

public class NBTTagString extends NBTBase {
	
	net.minecraft.server.v1_15_R1.NBTTagString value;
	
	protected NBTTagString(net.minecraft.server.v1_15_R1.NBTTagString value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_15_R1.NBTTagString asNMS() {
		return value;
	}
}
