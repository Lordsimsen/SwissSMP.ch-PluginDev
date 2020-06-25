package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public class NBTTagByteArray extends NBTBase {
	
	net.minecraft.server.v1_16_R1.NBTTagByteArray value;
	
	protected NBTTagByteArray(net.minecraft.server.v1_16_R1.NBTTagByteArray value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_16_R1.NBTTagByteArray asNMS() {
		return value;
	}
}
