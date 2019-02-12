package ch.swisssmp.utils.nbt;

public class NBTTagDouble extends NBTNumber {
	
	net.minecraft.server.v1_13_R2.NBTTagDouble value;
	
	protected NBTTagDouble(net.minecraft.server.v1_13_R2.NBTTagDouble value){
		super(value);
		this.value = value;
	}

	@Override
	protected net.minecraft.server.v1_13_R2.NBTTagDouble asNMS() {
		return value;
	}
}
