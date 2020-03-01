package ch.swisssmp.utils.nbt;

public class NBTTagIntArray extends NBTBase {
	
	net.minecraft.server.v1_15_R1.NBTTagIntArray value;
	
	protected NBTTagIntArray(net.minecraft.server.v1_15_R1.NBTTagIntArray value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_15_R1.NBTTagIntArray asNMS() {
		return value;
	}
}
