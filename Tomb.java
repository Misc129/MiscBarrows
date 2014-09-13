package MiscBarrows;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.Player;


enum Tomb {
	DHAROK(63177, 6703, 66116, new Tile(3575, 3298, 0), new Tile(3556,9716, 3), 
			new Area(new Tile(3549, 9719, 3), new Tile(3560,9710, 3))), 
	VERAC(66016, 6707, 66115, new Tile(3557, 3298, 0),new Tile(3575, 9706, 3), 
			new Area(new Tile(3568, 9710, 3),new Tile(3579, 9702, 3))), 
	TORAG(66019, 6706, 66116,new Tile(3554, 3282, 0), new Tile(3569, 9684, 3), 
			new Area(new Tile(3564, 9692, 3), new Tile(3575, 9682, 3))), 
	KARIL(66018, 6705, 66115, new Tile(3564, 3277, 0), new Tile(3549,9684, 3), 
			new Area(new Tile(3545, 9688, 3), new Tile(3557, 9678, 3))), 
	GUTHAN(66020, 6704, 66115, new Tile(3576, 3281, 0), new Tile(3537, 9704, 3), 
			new Area(new Tile(3533, 9708, 3), new Tile(3545, 9699, 3))), 
	AHRIM(66017, 6702, 66116, new Tile(3567, 3288, 0), new Tile(3556, 9701, 3),
			new Area(new Tile(3550, 9704, 3), new Tile(3561, 9694, 3))),
	VERAC_QUEST(66016,6707,66115, new Tile(3557,3298,0), new Tile(4074,5710,0), 
			new Area(new Tile(4067,5727,0), new Tile(4078,5706,0))),
	AKRISAE(66189,6707,66115, new Tile(3557,3298,0), new Tile(4072,5721,0), 
			new Area(new Tile(4067,5727,0), new Tile(4078,5706,0)));
	private int _tombId;
	private int _stairsId;
	private int _spadeId;
	private Tile _moundTile;
	private Tile _tombTile;
	private Area _area;
	Tomb(int tombId, int stairsId, int spadeId, Tile moundTile,
			Tile tombTile, Area area) {
		_tombId = tombId;
		_stairsId = stairsId;
		_spadeId = spadeId;
		_moundTile = moundTile;
		_tombTile = tombTile;
		_area = area;
	}
	public int getTombId() {
		return _tombId;
	}
	public int getStairsId() {
		return _stairsId;
	}
	public int getSpadeId() {
		return _spadeId;
	}
	public Tile getMoundTile() {
		return _moundTile;
	}
	public Tile getTombTile() {
		return _tombTile;
	}
	public Area getArea() {
		return _area;
	}
	public static Tomb getTomb() {
		Player me = Players.getLocal();
		if (Tomb.DHAROK._area.contains(me))
			return Tomb.DHAROK;
		if (Tomb.VERAC._area.contains(me))
			return Tomb.VERAC;
		if (Tomb.TORAG._area.contains(me))
			return Tomb.TORAG;
		if (Tomb.KARIL._area.contains(me))
			return Tomb.KARIL;
		if (Tomb.GUTHAN._area.contains(me))
			return Tomb.GUTHAN;
		if (Tomb.AHRIM._area.contains(me))
			return Tomb.AHRIM;
		return null;
	}
}