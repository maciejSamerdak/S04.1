
public class Pointer {	//odwo³ania

	int id;		//nr odwo³ania
	Process parent;//proces, ktory je wywo³a³
	
	public Pointer(int id, Process parent){
		this.id = id;
		this.parent=parent;
	}
}
