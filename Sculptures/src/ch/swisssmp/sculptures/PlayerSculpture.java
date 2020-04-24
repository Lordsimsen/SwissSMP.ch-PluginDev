package ch.swisssmp.sculptures;

import org.bukkit.entity.ArmorStand;

public class PlayerSculpture extends Sculpture {
	
	private HeadPart head;
	private LegPart legs;
	private ArmPart arms;
	private TorsoPart torso;
	private EquipmentPart equipment;
	
	public HeadPart getHead() {
		return head;
	}
	
	public LegPart getLegs() {
		return legs;
	}
	
	public ArmPart getArms() {
		return arms;
	}
	
	public TorsoPart getTorso() {
		return torso;
	}
	
	public EquipmentPart getEquipment() {
		return equipment;
	}
	
	public class HeadPart extends SculpturePart{

		protected HeadPart(ArmorStand armorStand, Quaternion pose) {
			super(armorStand, pose);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public class LegPart extends SculpturePart{

		protected LegPart(ArmorStand armorStand, Quaternion pose) {
			super(armorStand, pose);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public class ArmPart extends SculpturePart{

		protected ArmPart(ArmorStand armorStand, Quaternion pose) {
			super(armorStand, pose);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public class TorsoPart extends SculpturePart{

		protected TorsoPart(ArmorStand armorStand, Quaternion pose) {
			super(armorStand, pose);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public class EquipmentPart extends SculpturePart{

		protected EquipmentPart(ArmorStand armorStand, Quaternion pose) {
			super(armorStand, pose);
			// TODO Auto-generated constructor stub
		}
		
	}
}
