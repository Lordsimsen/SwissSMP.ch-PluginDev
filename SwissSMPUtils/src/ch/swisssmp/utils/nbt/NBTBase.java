package ch.swisssmp.utils.nbt;

public abstract class NBTBase {
	protected abstract net.minecraft.server.v1_15_R1.NBTBase asNMS();
	
	public String asString(){
		return asNMS().asString();
	}
	
	public String toString(){
		return asNMS().toString();
	}
	
	public byte getTypeId(){
		return asNMS().getTypeId();
	}
	
	protected static NBTBase get(net.minecraft.server.v1_15_R1.NBTBase base){
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagByte){
			return new NBTTagByte((net.minecraft.server.v1_15_R1.NBTTagByte) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagByteArray){
			return new NBTTagByteArray((net.minecraft.server.v1_15_R1.NBTTagByteArray) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagDouble){
			return new NBTTagDouble((net.minecraft.server.v1_15_R1.NBTTagDouble) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagFloat){
			return new NBTTagFloat((net.minecraft.server.v1_15_R1.NBTTagFloat) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagInt){
			return new NBTTagInt((net.minecraft.server.v1_15_R1.NBTTagInt) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagIntArray){
			return new NBTTagIntArray((net.minecraft.server.v1_15_R1.NBTTagIntArray) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagList){
			return new NBTTagList((net.minecraft.server.v1_15_R1.NBTTagList) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagLong){
			return new NBTTagLong((net.minecraft.server.v1_15_R1.NBTTagLong) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagShort){
			return new NBTTagShort((net.minecraft.server.v1_15_R1.NBTTagShort) base);
		}
		if(base instanceof net.minecraft.server.v1_15_R1.NBTTagString){
			return new NBTTagString((net.minecraft.server.v1_15_R1.NBTTagString) base);
		}
		return null;
	}
}
