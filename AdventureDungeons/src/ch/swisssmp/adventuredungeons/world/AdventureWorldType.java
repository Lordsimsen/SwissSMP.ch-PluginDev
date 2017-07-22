package ch.swisssmp.adventuredungeons.world;

public enum AdventureWorldType {
OPEN_WORLD,
DUNGEON_TEMPLATE,
DUNGEON_INSTANCE;
public boolean camps_despawn_mobs(){
	switch(this){
	case OPEN_WORLD:
		return true;
	case DUNGEON_TEMPLATE:
		return true;
	case DUNGEON_INSTANCE:
		return false;
	default:
		return true;
	}
}
}
