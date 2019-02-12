package ch.swisssmp.utils.nbt;

public abstract class NBTBase {
	protected abstract net.minecraft.server.v1_13_R2.NBTBase asNMS();
	
	public String asString(){
		return asNMS().asString();
	}
	
	public String toString(){
		return asNMS().toString();
	}
	
	public byte getTypeId(){
		return asNMS().getTypeId();
	}
	
	protected static NBTBase get(net.minecraft.server.v1_13_R2.NBTBase base){
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagByte){
			return new NBTTagByte((net.minecraft.server.v1_13_R2.NBTTagByte) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagByteArray){
			return new NBTTagByteArray((net.minecraft.server.v1_13_R2.NBTTagByteArray) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagDouble){
			return new NBTTagDouble((net.minecraft.server.v1_13_R2.NBTTagDouble) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagFloat){
			return new NBTTagFloat((net.minecraft.server.v1_13_R2.NBTTagFloat) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagInt){
			return new NBTTagInt((net.minecraft.server.v1_13_R2.NBTTagInt) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagIntArray){
			return new NBTTagIntArray((net.minecraft.server.v1_13_R2.NBTTagIntArray) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagList){
			return new NBTTagList((net.minecraft.server.v1_13_R2.NBTTagList) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagLong){
			return new NBTTagLong((net.minecraft.server.v1_13_R2.NBTTagLong) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagShort){
			return new NBTTagShort((net.minecraft.server.v1_13_R2.NBTTagShort) base);
		}
		if(base instanceof net.minecraft.server.v1_13_R2.NBTTagString){
			return new NBTTagString((net.minecraft.server.v1_13_R2.NBTTagString) base);
		}
		return null;
	}
}
