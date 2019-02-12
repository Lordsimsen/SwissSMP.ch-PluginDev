package ch.swisssmp.utils.nbt;

public class NBTTagList extends NBTBase {
	
	net.minecraft.server.v1_13_R2.NBTTagList value;
	
	protected NBTTagList(net.minecraft.server.v1_13_R2.NBTTagList value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagList asNMS() {
		return value;
	}
}
