package ch.swisssmp.utils.nbt.legacy;

@Deprecated
public abstract class NBTNumber extends NBTBase {

	private final net.minecraft.server.v1_16_R1.NBTNumber value;
	
	public NBTNumber(net.minecraft.server.v1_16_R1.NBTNumber value){
		this.value = value;
	}
	
	public byte asByte(){
		return value.asByte();
	}
	
	public double asDouble(){
		return value.asDouble();
	}
	
	public float asFloat(){
		return value.asFloat();
	}
	
	public int asInt(){
		return value.asInt();
	}
	
	public long asLong(){
		return value.asLong();
	}
	
	public short asShort(){
		return value.asShort();
	}
}
