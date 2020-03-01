package ch.swisssmp.utils.nbt;

public class NBTTagLong extends NBTNumber {
	
	net.minecraft.server.v1_15_R1.NBTTagLong value;
	
	protected NBTTagLong(net.minecraft.server.v1_15_R1.NBTTagLong value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_15_R1.NBTTagLong asNMS() {
		return value;
	}
}
