package ch.swisssmp.knightstournament;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.knightstournament.editor.ArenaDeleteSlot;
import ch.swisssmp.knightstournament.editor.ArenaNameSlot;
import ch.swisssmp.knightstournament.editor.BeginSoundSlot;
import ch.swisssmp.knightstournament.editor.CallSoundSlot;
import ch.swisssmp.knightstournament.editor.CenterWaypointSlot;
import ch.swisssmp.knightstournament.editor.EndSoundSlot;
import ch.swisssmp.knightstournament.editor.PosOneWaypointSlot;
import ch.swisssmp.knightstournament.editor.PosTwoWaypointSlot;
import ch.swisssmp.knightstournament.editor.TournamentOrganizerSlot;

public class KnightsArenaEditor extends CustomEditorView{
	
	private final KnightsArena arena;

	protected KnightsArenaEditor(Player player, KnightsArena arena) {
		super(player);
		this.arena = arena;
	}
	
	@Override
	protected Collection<EditorSlot> initializeEditor() {
		
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		
//		result.add(new ArenaTypeSlot(this,0)); //Allgemeines Symbol süsch, gibt ja keine Typen per se
		result.add(new ArenaNameSlot(this, 0));
		result.add(new PosOneWaypointSlot(this, 1));
		result.add(new PosTwoWaypointSlot(this, 2));
		result.add(new CenterWaypointSlot(this, 3));
		result.add(new TournamentOrganizerSlot(this,4)); //momentan noch ohne Funktion und eventhandler
		result.add(new CallSoundSlot(this, 10));
		result.add(new BeginSoundSlot(this, 11));
		result.add(new EndSoundSlot(this, 12));
		result.add(new ArenaDeleteSlot(this, 17, arena));
		
		return result;
	}
	
	public KnightsArena getArena() {
		return arena;
	}
	
	public static KnightsArenaEditor open(Player p, KnightsArena arena){ 
	KnightsArenaEditor editor = new KnightsArenaEditor(p, arena);
		editor.open();
		return editor;
	}
	
	@Override
	protected void onInventoryClicked(InventoryClickEvent arg0){
		this.arena.updateTokens();
	}
	
	@Override
	protected int getInventorySize() {
		return 18;
	}

	@Override
	public String getTitle() {
		return this.arena.getName();
	}

}
