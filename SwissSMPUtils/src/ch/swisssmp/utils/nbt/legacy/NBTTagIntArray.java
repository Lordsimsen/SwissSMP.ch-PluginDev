package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public class NBTTagIntArray extends NBTBase {
	
	net.minecraft.server.v1_16_R1.NBTTagIntArray value;
	
	protected NBTTagIntArray(net.minecraft.server.v1_16_R1.NBTTagIntArray value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_16_R1.NBTTagIntArray asNMS() {
		return value;
	}
}
