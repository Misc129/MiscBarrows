package MiscBarrows;

import java.util.LinkedList;

import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;

public enum Brother {
	DHAROK(2026),
	VERAC(2030),
	TORAG(2029),
	KARIL(2028),
	GUTHAN(2027),
	AHRIM(2025);
	//AKRISAE(14297);
	
	private int _id;
	private LinkedList<Integer> _gearSet;
	private boolean _autocast;
	private AttackStyles.StandardSpells _spell;

	private Brother(int id) {
		_id = id;
		_gearSet = new LinkedList<Integer>();
		_autocast = false;
	}
	
	public void addGearIds(int id) {
		_gearSet.add(id);
	}

	public void addGearIds(Integer... ids) {
		for (int id : ids)
			_gearSet.add(id);
	}

	public LinkedList<Integer> getGearIds() {
		return _gearSet;
	}
	
	public LinkedList<Integer> getMissingGearIds(){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i : _gearSet){
			if(!Equipment.isWearing(i))
				result.add(i);
		}
		return result;
	}
	
	public void setAutocast(AttackStyles.StandardSpells spell){
		_autocast = true;
		_spell = spell;
	}
	
	public boolean isAutocasting(){
		return _autocast;
	}
	
	public AttackStyles.StandardSpells getSpell(){
		return _spell;
	}

	public int getId() {
		return _id;
	}

	public static NPC getMyBrother() {
		NPC brother = NPCs.getNearest(new Filter<NPC>() {
			@Override
			public boolean accept(NPC npc) {
				if (npc.getInteracting() == null)
					return false;
				for(Brother b : Brother.values()){
					if(b.getId() == npc.getId())
						return (npc.getInteracting().getId() == Players.getLocal().getId());
				}
				return false;
			}
		});
		return brother;
	}
	
	public static NPC getNearestBrother(){
		return null;
	}
}