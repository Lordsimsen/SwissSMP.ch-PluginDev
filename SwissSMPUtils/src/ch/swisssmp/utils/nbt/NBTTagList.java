package ch.swisssmp.utils.nbt;

public class NBTTagList extends NBTBase {
	
	net.minecraft.server.v1_15_R1.NBTTagList value;
	
	protected NBTTagList(net.minecraft.server.v1_15_R1.NBTTagList value){
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_15_R1.NBTTagList asNMS() {
		return value;
	}
}
