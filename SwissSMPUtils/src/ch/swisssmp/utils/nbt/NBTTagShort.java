package ch.swisssmp.utils.nbt;

public class NBTTagShort extends NBTNumber {
	
	net.minecraft.server.v1_13_R2.NBTTagShort value;
	
	protected NBTTagShort(net.minecraft.server.v1_13_R2.NBTTagShort value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagShort asNMS() {
		return value;
	}
}
