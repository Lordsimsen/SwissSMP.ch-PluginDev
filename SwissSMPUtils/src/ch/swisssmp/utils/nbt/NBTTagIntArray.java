package ch.swisssmp.utils.nbt;

public class NBTTagIntArray extends NBTBase {
	
	net.minecraft.server.v1_13_R2.NBTTagIntArray value;
	
	protected NBTTagIntArray(net.minecraft.server.v1_13_R2.NBTTagIntArray value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagIntArray asNMS() {
		return value;
	}
}
