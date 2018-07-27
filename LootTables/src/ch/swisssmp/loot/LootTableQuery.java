package ch.swisssmp.loot;

public class LootTableQuery {
	private final LootTable lootTable;
	private final int loot_table_id;
	private final boolean itemIsLootTableToken;
	
	protected LootTableQuery(LootTable lootTable, int loot_table_id, boolean isLootTableToken){
		this.lootTable = lootTable;
		this.loot_table_id = loot_table_id;
		this.itemIsLootTableToken = isLootTableToken;
	}
	/*
	 * Returns the loot table associated with the item stack. If no table was found this value is null.
	 */
	public LootTable getLootTable(){
		return this.lootTable;
	}
	/*
	 * Returns the loot table id. If no id was found, this value is -1.
	 */
	public int getLootTableId(){
		return this.loot_table_id;
	}
	/*
	 * Returns whether this item stack is associated with a loot table (independant of whether the loot table was actually found)
	 */
	public boolean isLootTableToken(){
		return this.itemIsLootTableToken;
	}
}
