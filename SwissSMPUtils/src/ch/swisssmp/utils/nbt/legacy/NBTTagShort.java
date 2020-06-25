package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public class NBTTagShort extends NBTNumber {
	
	net.minecraft.server.v1_16_R1.NBTTagShort value;
	
	protected NBTTagShort(net.minecraft.server.v1_16_R1.NBTTagShort value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_16_R1.NBTTagShort asNMS() {
		return value;
	}
}
