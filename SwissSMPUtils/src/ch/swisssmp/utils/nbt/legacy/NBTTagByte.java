package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public class NBTTagByte extends NBTNumber {
	
	net.minecraft.server.v1_16_R1.NBTTagByte value;
	
	protected NBTTagByte(net.minecraft.server.v1_16_R1.NBTTagByte value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_16_R1.NBTTagByte asNMS() {
		return value;
	}
}
