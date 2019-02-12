package ch.swisssmp.utils.nbt;

public class NBTTagInt extends NBTNumber {
	
	net.minecraft.server.v1_13_R2.NBTTagInt value;
	
	protected NBTTagInt(net.minecraft.server.v1_13_R2.NBTTagInt value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagInt asNMS() {
		return value;
	}
}
