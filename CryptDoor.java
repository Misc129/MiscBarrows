package MiscBarrows;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.wrappers.Tile;

public class CryptDoor {
	CryptDoor(String name, int id, Tile location, int bitSetting) {
		_name = name;
		_id = id;
		_location = location;
		_bitSetting = bitSetting;
	}

	String _name;
	private int _id;
	private Tile _location;
	boolean _ruledOut = false;
	int _bitSetting;

	boolean _visited = false;

	public void reset() {
		_ruledOut = _visited = false;
	}

	public void setRuledOut() {
		_ruledOut = true;
	}

	public void setVisited() {
		_visited = true;
	}

	public boolean beenVisited() {
		return _visited;
	}

	public boolean isRuledOut() {
		return _ruledOut;
	}

	public int getId() {
		return _id;
	}

	public Tile getLocation() {
		return _location;
	}

	public boolean isOpenable() {
		return (Settings.get(452, _bitSetting, 0x1) == 0);
	}

	public String toString() {
		return _name;
	}
}