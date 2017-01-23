package ch.swisssmp.craftmmo.mmoentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class MmoAI {
	public static MmoAI tempAI = null;
	public static Vector tempHome;
	public final int mmo_mob_id;
	public final ConfigurationSection dataSection;
	public final boolean arrowAttack;
	public final boolean avoidTarget;
	public final boolean beg;
	public final boolean breakDoor;
	public final boolean breed;
	public final boolean defendVillage;
	public final boolean doorInteract; //not in use, couldn't figure it out
	public final boolean eatTile;
	public final boolean fleeSun;
	public final boolean idle_float;
	public final boolean followOwner;
	public final boolean followParent;
	public final boolean hurtByTarget;
	public final boolean interact;
	public final boolean jumpOnBlock;
	public final boolean leapAtTarget;
	public final boolean lookAtPlayer;
	public final boolean lookAtTradingPlayer;
	public final boolean makeLove;
	public final boolean meleeAttack;
	public final boolean moveIndoors;
	public final boolean moveThroughVillage;
	public final boolean moveTowardsRestriction;
	public final boolean moveTowardsTarget;
	public final boolean nearestAttackableTarget;
	public final boolean ocelotAttack;
	public final boolean offerFlower;
	public final boolean openDoor;
	public final boolean ownerHurtByTarget;
	public final boolean panic;
	public final boolean passengerCarrotStick; //not in use, couldn't figure it out
	public final boolean play;
	public final boolean randomLookaround;
	public final boolean randomStroll;
	public final boolean randomTargetNonTamed;
	public final boolean restrictOpenDoor;
	public final boolean restrictSun;
	public final boolean sit;
	public final boolean swell;
	public final boolean takeFlower;
	public final boolean tame;
	public final boolean target; //not in use, couldn't figure it out
	public final boolean tempt;
	public final boolean tradeWithPlayer;
	public final boolean stayNearHome;
	
	public MmoAI(int mmo_mob_id, ConfigurationSection dataSection){
		this.mmo_mob_id = mmo_mob_id;
		this.dataSection = dataSection;
		this.arrowAttack = dataSection.contains("arrowAttack");
		this.avoidTarget = dataSection.contains("avoidTarget");
		this.beg = dataSection.contains("beg");
		this.breakDoor = dataSection.contains("breakDoor");
		this.breed = dataSection.contains("breed");
		this.defendVillage = dataSection.contains("defendVillage");
		this.doorInteract = dataSection.contains("doorInteract");
		this.eatTile = dataSection.contains("eatTile");
		this.fleeSun = dataSection.contains("fleeSun");
		this.idle_float = dataSection.contains("idle_float");
		this.followOwner = dataSection.contains("followOwner");
		this.followParent = dataSection.contains("followParent");
		this.hurtByTarget = dataSection.contains("hurtByTarget");
		this.interact = dataSection.contains("interact");
		this.jumpOnBlock = dataSection.contains("jumpOnBlock");
		this.leapAtTarget = dataSection.contains("leapAtTarget");
		this.lookAtPlayer = dataSection.contains("lookAtPlayer");
		this.lookAtTradingPlayer = dataSection.contains("lookAtTradingPlayer");
		this.makeLove = dataSection.contains("makeLove");
		this.meleeAttack = dataSection.contains("meleeAttack");
		this.moveIndoors = dataSection.contains("moveIndoors");
		this.moveThroughVillage = dataSection.contains("moveThroughVillage");
		this.moveTowardsRestriction = dataSection.contains("moveTowardsRestriction");
		this.moveTowardsTarget = dataSection.contains("moveTowardsTarget");
		this.nearestAttackableTarget = dataSection.contains("nearestAttackableTarget");
		this.ocelotAttack = dataSection.contains("ocelotAttack");
		this.offerFlower = dataSection.contains("offerFlower");
		this.openDoor = dataSection.contains("openDoor");
		this.ownerHurtByTarget = dataSection.contains("ownerHurtByTarget");
		this.panic = dataSection.contains("panic");
		this.passengerCarrotStick = dataSection.contains("passengerCarrotStick");
		this.play = dataSection.contains("play");
		this.randomLookaround = dataSection.contains("randomLookaround");
		this.randomStroll = dataSection.contains("randomStroll");
		this.randomTargetNonTamed = dataSection.contains("randomTargetNonTamed");
		this.restrictOpenDoor = dataSection.contains("restrictOpenDoor");
		this.restrictSun = dataSection.contains("restrictSun");
		this.sit = dataSection.contains("sit");
		this.swell = dataSection.contains("swell");
		this.takeFlower = dataSection.contains("takeFlower");
		this.tame = dataSection.contains("tame");
		this.target = dataSection.contains("target");
		this.tempt = dataSection.contains("tempt");
		this.tradeWithPlayer = dataSection.contains("tradeWithPlayer");
		this.stayNearHome = dataSection.contains("stayNearHome");
	}
	public boolean isMobile(){
		return (
				this.avoidTarget || 
				this.fleeSun || 
				this.followOwner || 
				this.followParent || 
				this.jumpOnBlock || 
				this.leapAtTarget || 
				this.moveIndoors || 
				this.moveThroughVillage || 
				this.moveTowardsRestriction || 
				this.moveTowardsTarget ||
				this.nearestAttackableTarget ||
				this.ownerHurtByTarget ||
				this.panic ||
				this.passengerCarrotStick ||
				this.play ||
				this.randomStroll ||
				this.randomTargetNonTamed ||
				this.restrictSun ||
				this.target ||
				this.stayNearHome);
	}
}
