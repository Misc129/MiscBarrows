package MiscBarrows;

import java.util.ArrayList;

import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;

public class CryptRoom {

		public CryptRoom(String name, Area area, CryptDoor[] doors) {
			// TODO Auto-generated constructor stub
			_name = name;
			_area = area;
			_centerTile = area.getCentralTile();
			_doors = doors;
			_edges = new ArrayList<CryptEdge>();
		}

		private String _name;
		private Area _area;
		private CryptDoor[] _doors;
		private Tile _centerTile;
		private ArrayList<CryptEdge> _edges;

		public Area getArea() {
			return _area;
		}

		public Tile getCenterTile() {
			return _centerTile;
		}

		public CryptDoor[] getDoors() {
			return _doors;
		}

		public ArrayList<CryptEdge> getEdgesCW() {
			return _edges;
		}

		public ArrayList<CryptEdge> getEdgesCCW() {
			// TODO
			return null;
		}

		public void addEdge(CryptEdge edge) {
			_edges.add(edge);
		}

		public void removeAllEdge(CryptEdge remove) {
			ArrayList<CryptEdge> toRemove = new ArrayList<CryptEdge>();
			for (CryptEdge edge : _edges) {
				if (edge.equals(remove))
					toRemove.add(edge);
			}
			for (CryptEdge removeEdge : toRemove)
				_edges.remove(removeEdge);
			return;
		}

		public void clearEdges() {
			_edges.clear();
		}

		public String toString() {
			return _name;
		}
}
