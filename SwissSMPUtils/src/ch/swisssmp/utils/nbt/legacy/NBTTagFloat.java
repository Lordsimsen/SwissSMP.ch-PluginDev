package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public class NBTTagFloat extends NBTNumber {
	
	net.minecraft.server.v1_16_R1.NBTTagFloat value;
	
	protected NBTTagFloat(net.minecraft.server.v1_16_R1.NBTTagFloat value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_16_R1.NBTTagFloat asNMS() {
		return value;
	}
}
