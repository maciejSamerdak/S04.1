

public class Process {	//proces generuj�cy odwo�ania

	int beg;	//indeks pierwszej ramki dla przydzia�u sta�ego
	int end;	//indeks ostatniej ramki dla przydzia�u sta�ego
	int errors;	//braki stron
	int pointers;	//maksymalna ilo�� odwo�a� dla przydzia�u proporcjonalnego

	public Process(int pointers){
		errors=0;
		this.pointers=pointers;
	}
}
