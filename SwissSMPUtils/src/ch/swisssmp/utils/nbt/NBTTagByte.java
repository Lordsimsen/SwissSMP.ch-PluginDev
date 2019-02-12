package ch.swisssmp.utils.nbt;

public class NBTTagByte extends NBTNumber {
	
	net.minecraft.server.v1_13_R2.NBTTagByte value;
	
	protected NBTTagByte(net.minecraft.server.v1_13_R2.NBTTagByte value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagByte asNMS() {
		return value;
	}
}
