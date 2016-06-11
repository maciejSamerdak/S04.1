

public class Process {	//proces generuj¹cy odwo³ania

	int beg;	//indeks pierwszej ramki dla przydzia³u sta³ego
	int end;	//indeks ostatniej ramki dla przydzia³u sta³ego
	int errors;	//braki stron
	int pointers;	//maksymalna iloœæ odwo³añ dla przydzia³u proporcjonalnego

	public Process(int pointers){
		errors=0;
		this.pointers=pointers;
	}
}
