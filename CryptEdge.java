package MiscBarrows;

public class CryptEdge {
	public CryptEdge(CryptDoor enter, CryptDoor exit, CryptRoom start,
			CryptRoom end) {
		_enter = enter;
		_exit = exit;
		_start = start;
		_end = end;
	}

	private CryptDoor _enter;
	private CryptDoor _exit;
	private CryptRoom _start;
	private CryptRoom _end;

	public CryptDoor getEnter() {
		return _enter;
	}

	public CryptDoor getExit() {
		return _exit;
	}

	public CryptRoom getStart() {
		return _start;
	}

	public CryptRoom getEnd() {
		return _end;
	}

	public CryptEdge getReverse() {
		return new CryptEdge(_exit, _enter, _end, _start);
	}

	public String toString() {
		return ("Enter:" + _enter.toString() + ", Exit:" + _exit.toString()
				+ "//Start:" + _start.toString() + ", End:" + _end
					.toString());
	}

	public boolean equals(CryptEdge e) {
		return (e.getEnter() == _enter && e.getExit() == _exit
				&& e.getStart() == _start && e.getEnd() == _end);
	}
}