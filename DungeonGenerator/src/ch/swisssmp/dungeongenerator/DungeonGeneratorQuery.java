package ch.swisssmp.dungeongenerator;

public class DungeonGeneratorQuery {
	private final DungeonGenerator dungeonGenerator;
	private final int generator_id;
	private final boolean itemIsGeneratorToken;
	
	protected DungeonGeneratorQuery(DungeonGenerator dungeonGenerator, int generator_id, boolean isGeneratorToken){
		this.dungeonGenerator = dungeonGenerator;
		this.generator_id = generator_id;
		this.itemIsGeneratorToken = isGeneratorToken;
	}
	/**
	 * @return The dungeon generator associated with the item stack. If no generator was found this value is null.
	 */
	public DungeonGenerator getGenerator(){
		return this.dungeonGenerator;
	}
	/**
	 * @return The generator id. If no id was found, this value is -1.
	 */
	public int getGeneratorId(){
		return this.generator_id;
	}
	/**
	 * @return Whether this item stack is associated with a dungeon generator (independant of whether the generator was actually found)
	 */
	public boolean isGeneratorToken(){
		return this.itemIsGeneratorToken;
	}
}
