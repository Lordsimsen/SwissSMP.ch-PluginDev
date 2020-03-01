package ch.swisssmp.utils.nbt;

public class NBTTagByteArray extends NBTBase {
	
	net.minecraft.server.v1_15_R1.NBTTagByteArray value;
	
	protected NBTTagByteArray(net.minecraft.server.v1_15_R1.NBTTagByteArray value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_15_R1.NBTTagByteArray asNMS() {
		return value;
	}
}
