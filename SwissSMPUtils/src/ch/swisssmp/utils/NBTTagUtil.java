package ch.swisssmp.utils;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class NBTTagUtil {
	public static Position getPosition(NBTTagCompound nbtTag, String key){
		if(nbtTag==null || !nbtTag.hasKey(key)) return null;
		return NBTTagUtil.getPosition(nbtTag.getCompound(key));
	}
	public static Position getPosition(NBTTagCompound nbtTag){
		double x = nbtTag.getDouble("x");
		double y = nbtTag.getDouble("y");
		double z = nbtTag.getDouble("z");
		float yaw = nbtTag.getFloat("yaw");
		float pitch = nbtTag.getFloat("pitch");
		return new Position(x,y,z,yaw,pitch);
	}
	public static void setPosition(NBTTagCompound nbtTag, String key, Position position){
		NBTTagCompound positionCompound = new NBTTagCompound();
		NBTTagUtil.setPosition(positionCompound, position);
		nbtTag.set(key, positionCompound);
	}
	public static void setPosition(NBTTagCompound nbtTag, Position position){
		nbtTag.setDouble("x", position.getX());
		nbtTag.setDouble("y", position.getY());
		nbtTag.setDouble("z", position.getZ());
		nbtTag.setFloat("yaw", position.getYaw());
		nbtTag.setFloat("pitch", position.getPitch());
	}
}
