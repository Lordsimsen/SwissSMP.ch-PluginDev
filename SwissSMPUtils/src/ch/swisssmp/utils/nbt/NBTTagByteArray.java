package ch.swisssmp.utils.nbt;

public class NBTTagByteArray extends NBTBase {
	
	net.minecraft.server.v1_13_R2.NBTTagByteArray value;
	
	protected NBTTagByteArray(net.minecraft.server.v1_13_R2.NBTTagByteArray value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagByteArray asNMS() {
		return value;
	}
}
