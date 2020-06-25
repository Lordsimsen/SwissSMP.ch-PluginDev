package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public class NBTTagInt extends NBTNumber {
	
	net.minecraft.server.v1_16_R1.NBTTagInt value;
	
	protected NBTTagInt(net.minecraft.server.v1_16_R1.NBTTagInt value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_16_R1.NBTTagInt asNMS() {
		return value;
	}
}
