package ch.swisssmp.utils.nbt;

import ch.swisssmp.utils.Position;

public class NBTTagCompound extends NBTBase {
	
	private final net.minecraft.server.v1_15_R1.NBTTagCompound nbtTag;
	
	public NBTTagCompound(){
		this.nbtTag = new net.minecraft.server.v1_15_R1.NBTTagCompound();
	}
	
	public NBTTagCompound(net.minecraft.server.v1_15_R1.NBTTagCompound nbtTag){
		this.nbtTag = nbtTag;
	}
	
	public net.minecraft.server.v1_15_R1.NBTTagCompound asNMS(){
		return nbtTag;
	}
	
	public boolean hasKey(String key){
		return nbtTag.hasKey(key);
	}
	
	public void set(String key, NBTBase value){
		nbtTag.set(key, value.asNMS());
	}
	
	public void remove(String key){
		if(nbtTag.hasKey(key)) nbtTag.remove(key);
	}
	
	public NBTBase get(String key){
		return nbtTag.hasKey(key) ? NBTBase.get(nbtTag.get(key)) : null;
	}
	
	public void setBoolean(String key, boolean value){
		this.nbtTag.setBoolean(key, value);
	}
	
	public void setByte(String key, byte value){
		this.nbtTag.setByte(key, value);
	}
	
	public void setByteArray(String key, byte[] value){
		nbtTag.setByteArray(key, value);
	}
	
	public void setDouble(String key, double value){
		nbtTag.setDouble(key, value);
	}
	
	public void setFloat(String key, float value){
		nbtTag.setFloat(key, value);
	}
	
	public void setInt(String key, int value){
		nbtTag.setInt(key, value);
	}
	
	public void setIntArray(String key, int[] value){
		nbtTag.setIntArray(key, value);
	}
	
	public void setLong(String key, long value){
		nbtTag.setLong(key, value);
	}
	
	public void setShort(String key, short value){
		nbtTag.setShort(key, value);
	}
	
	public void setString(String key, String value){
		nbtTag.setString(key, value);
	}
	
	public void setPosition(String key, Position position){
		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setPosition(position);
		this.set(key, nbtTag);
	}
	
	public void setPosition(Position position){
		nbtTag.setDouble("x", position.getX());
		nbtTag.setDouble("y", position.getY());
		nbtTag.setDouble("z", position.getZ());
		nbtTag.setFloat("yaw", position.getYaw());
		nbtTag.setFloat("pitch", position.getPitch());
	}
	
	public NBTTagCompound getCompound(String key){
		return nbtTag.hasKey(key) ? new NBTTagCompound(nbtTag.getCompound(key)) : null;
	}
	
	public byte getByte(String key){
		return getByte(key, (byte) 0);
	}
	
	public byte getByte(String key, byte fallback){
		return nbtTag.hasKey(key) ? nbtTag.getByte(key) : fallback;
	}
	
	public short getShort(String key){
		return getShort(key, (short) 0);
	}
	
	public short getShort(String key, short fallback){
		return nbtTag.hasKey(key) ? nbtTag.getShort(key) : fallback;
	}
	
	public int getInt(String key){
		return getInt(key, 0);
	}
	
	public int getInt(String key, int fallback){
		return nbtTag.hasKey(key) ? nbtTag.getInt(key) : fallback;
	}
	
	public long getLong(String key){
		return getLong(key, 0);
	}
	
	public long getLong(String key, long fallback){
		return nbtTag.hasKey(key) ? nbtTag.getLong(key) : fallback;
	}
	
	public float getFloat(String key){
		return getFloat(key, 0);
	}
	
	public float getFloat(String key, float fallback){
		return nbtTag.hasKey(key) ? nbtTag.getFloat(key) : fallback;
	}
	
	public double getDouble(String key){
		return getDouble(key, 0);
	}
	
	public double getDouble(String key, double fallback){
		return nbtTag.hasKey(key) ? nbtTag.getDouble(key) : fallback;
	}
	
	public String getString(String key){
		return getString(key, null);
	}
	
	public String getString(String key, String fallback){
		return nbtTag.hasKey(key) ? nbtTag.getString(key) : fallback;
	}
	
	public byte[] getByteArray(String key){
		return getByteArray(key, null);
	}
	
	public byte[] getByteArray(String key, byte[] fallback){
		return nbtTag.hasKey(key) ? nbtTag.getByteArray(key) : fallback;
	}
	
	public int[] getIntArray(String key){
		return getIntArray(key, null);
	}
	
	public int[] getIntArray(String key, int[] fallback){
		return nbtTag.hasKey(key) ? nbtTag.getIntArray(key) : fallback;
	}
	
	public boolean getBoolean(String key){
		return getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean fallback){
		return nbtTag.hasKey(key) ? nbtTag.getBoolean(key) : fallback;
	}
	
	public Position getPosition(String key){
		return getPosition(key, null);
	}
	
	public Position getPosition(String key, Position fallback){
		NBTTagCompound nbtTag = this.getCompound(key);
		if(nbtTag==null) return fallback;
		return nbtTag.getPosition();
	}
	
	public Position getPosition(){
		double x = nbtTag.getDouble("x");
		double y = nbtTag.getDouble("y");
		double z = nbtTag.getDouble("z");
		float yaw = nbtTag.getFloat("yaw");
		float pitch = nbtTag.getFloat("pitch");
		return new Position(x,y,z,yaw,pitch);
	}
	
	public String asString(){
		return nbtTag.asString();
	}
	
	public NBTTagCompound clone(){
		return new NBTTagCompound(nbtTag.clone());
	}
	
	@Override
	public int hashCode(){
		return nbtTag.hashCode();
	}
}
