import java.util.Random;

public class Process {	//proces generuj¹cy odwo³ania

	Pointer[] tasks;	//generowany ci¹g odwo³añ
	int beg;	//indeks pierwszej ramki dla przydzia³u sta³ego
	int end;	//indeks ostatniej ramki dla przydzia³u sta³ego
	int errors;	//braki stron
	int pointers;	//maksymalna iloœæ odwo³añ dla przydzia³u priorytetowego

	public Process(int tasks, int pointers){
		this.tasks = new Pointer[tasks];
		Random rn = new Random();
		for (int i=0; i<tasks; i++)
			this.tasks[i]=new Pointer(rn.nextInt(pointers)+1, this);
		errors=0;
		this.pointers=pointers;
	}
}
