import java.util.ArrayDeque;
import java.util.Random;

public class Main {

	static int memorySize = 20;		//iloœæ ramek w pamiêci fizycznej
	static int procQuantity = 10;	//iloœæ procesów
	
	
	static Pointer[] memory = new Pointer[memorySize];		//pamiêæ
	static long[] time = new long[memorySize];				//licznik czasu dla LRU
	
	public static void main(String[]args){
		Random rn = new Random();
		
		Process[] processes = new Process[procQuantity];
		Process[] processes2 = new Process[procQuantity];
		Process[] processes3 = new Process[procQuantity];
		Process[] processes4 = new Process[procQuantity];
		
		for (int i=0; i<processes.length; i++){				//tworzenie procesów
			int pointers = rn.nextInt(6)+5;
			int tasksAm = pointers*3;
			processes[i]=new Process(tasksAm, pointers);
			//System.out.println(processes[i].pointers);
			processes2[i]=new Process(tasksAm, pointers);
			//System.out.println(processes2[i].pointers);
			processes3[i]=new Process(tasksAm, pointers);
			//System.out.println(processes3[i].pointers);
			processes4[i]=new Process(tasksAm, pointers);
			//System.out.println(processes4[i].pointers);
		}
			
		ArrayDeque<Pointer> tasks=new ArrayDeque<Pointer>();	//ci¹g stron
		ArrayDeque<Pointer> tasks2=new ArrayDeque<Pointer>();
		ArrayDeque<Pointer> tasks3=new ArrayDeque<Pointer>();
		ArrayDeque<Pointer> tasks4=new ArrayDeque<Pointer>();

		//generowanie ci¹gu stron: proces1[0]; proces2[0]; ... procesn[0]; proces1[1]; proces2[1]; ...	procesn[m];
		for (int n=0; n<256; n++){								
			for (int i=0; i<processes.length; i++)
				if(n<processes[i].tasks.length){
					tasks.add(processes[i].tasks[n]);
					tasks2.add(processes2[i].tasks[n]);
					tasks3.add(processes3[i].tasks[n]);
					tasks4.add(processes4[i].tasks[n]);
				}
		}
		//clou programu
		przydz_rowny(tasks, processes);
		//przydz_rowny(tasks2, processes2);
		przydz_proporcjonalny(tasks3, processes3, new int[]{7, 10});
		przydz_priorytetowy(tasks4, processes4, 5);
		
	}
	
	//resetujemy pamiêæ dla kolejnych operacji przydzia³u
	static void resetMemory(){
		for (int i=0; i<memory.length; i++)
			memory[i]=null;
	}
	
	static void przydz_rowny(ArrayDeque<Pointer> tasks, Process[] processes){
		
		int space = memory.length/processes.length;	//proces przydzia³u równej liczby ramek
		
		int n = 0;
		for(int i=0; i<processes.length; i++){
			processes[i].beg=n;
			processes[i].end=n+space-1;
			n+=space;
		}
		int last=n;
		
		while(!tasks.isEmpty()){					//LRU
			
			Pointer current = tasks.poll();			
			//sprawdz, czy istnieje wolna ramka, b¹dz czy aktualna strona znajduje siê w pamiêci
			n=current.parent.beg;
			boolean placed=false;
			while(n<=current.parent.end && placed==false){
				if(memory[n]==null){
					memory[n]=current;
					time[n]=System.nanoTime();
					placed=true;
				}
				if(memory[n]==current){
					time[n]=System.nanoTime();
					placed=true;
				}
				n++;
			}
			if (placed==false){
				// sprawdzanie ramek nie przydzielonych (przydzia³ równy)
				if(last<memory.length){
					n=last;
					while(n<memory.length && placed==false){
						if(memory[n]==null){
							memory[n]=current;
							time[n]=System.nanoTime();
							placed=true;
						}
						if(memory[n]==current){
							time[n]=System.nanoTime();
							placed=true;
						}
						n++;
					}
				}
				else{
					// wyszukiwanie ostatniej u¿ywanej strony
					long min=time[current.parent.beg];
					int minInd=current.parent.beg;
					for(int i=current.parent.beg+1; i<current.parent.end; i++){
						if(time[i]<min){
							min=time[i];
							minInd=i;
						}
					}
					for(int i=last; i<time.length; i++){
						if(time[i]<min){
							min=time[i];
							minInd=i;
						}
					}
					memory[minInd]=current;
					time[minInd]=System.nanoTime();
					current.parent.errors++;
				}
			}	
		}													//Koniec LRU
		int totalErrors=0;
		System.out.println("Braki stron dla przydzia³u równego:");
		for (int i=0; i<processes.length; i++){
			System.out.println("Proces "+(i+1)+".: "+processes[i].errors);
			totalErrors+=processes[i].errors;
		}
		System.out.println("Razem: "+totalErrors);
		
		resetMemory();		//reset
	}
	
	static void przydz_priorytetowy(ArrayDeque<Pointer> tasks, Process[] processes, int max){
		while(!tasks.isEmpty()){
		
		Pointer current = tasks.poll();
		//przydzielamy ramki na zasadzie przydzia³u globalnego
		boolean placed=false;
		int n=0;
		while(n<memory.length && placed==false){	//LRU dla przydzia³u priorytetowego
			if(memory[n]==null){
				memory[n]=current;
				time[n]=System.nanoTime();
				placed=true;
			}
			if(memory[n]==current){
				time[n]=System.nanoTime();
				placed=true;
			}
			n++;
		}
		if (placed==false){
			//jeœli liczba braków przekroczy³a ustalony limit, zabieramy ramkê procesowi z najmniejsz¹ iloœci¹ braków
			if (current.parent.errors%max==0){
				int minErr = current.parent.errors;
				for(int i=0; i<processes.length; i++)		//wyszukiwanie najmniejszej iloœci braków
					if(processes[i].errors<minErr)
						minErr=processes[i].errors;
				
				long min=time[0];							//wyszukiwanie ostatniej u¿ywanej strony wœród procesów o najmniejszej iloœci braków
				int minInd=0;
				for(int i=0; i<memory.length; i++){
					if(time[i]<min && memory[i].parent.errors==minErr){
							min=time[i];
							minInd=i;
						}
					}
				memory[minInd]=current;
				time[minInd]=System.nanoTime();
				current.parent.errors++;
			}
			else{
				long min=time[0];
				int minInd=0;
				for(int i=0; i<memory.length; i++){
					if(time[i]<min){
							min=time[i];
							minInd=i;
						}
					}
				
				memory[minInd]=current;
				time[minInd]=System.nanoTime();
				current.parent.errors++;
				}
			}
		}												//koniec LRU
		int totalErrors=0;
		System.out.println("Braki stron dla przydzia³u priorytetowego (wg. czêstoœci braków):");
		for (int i=0; i<processes.length; i++){
			System.out.println("Proces "+(i+1)+".: "+processes[i].errors);
			totalErrors+=processes[i].errors;
		}
		System.out.println("Razem: "+totalErrors);
		
		resetMemory();
	}
	
	static void przydz_proporcjonalny(ArrayDeque<Pointer> tasks, Process[] processes, int[] borders){
		//tablica, której komórki przechowuj¹ liczbê ramek do przydzia³u dla odpowiadaj¹cego procesu,
		//suma wartoœci komórek nie mo¿e przekraczaæ iloœci dostêpnych ramek w pamiêci
		int[] proportions = new int[processes.length];
		
		int totalProp = 0;		//suma wykorzystanych ramek
		
		//proces przydzielania iloœci ramek wed³ug iloœci ró¿nych odwo³añ w tablicy proportions
		for (int i=0; i<proportions.length; i++){
			proportions[i]=1;
			totalProp++;
		}
		
		for (int n=0; n<borders.length; n++){
			for(int i=0; i<proportions.length && totalProp<memory.length; i++){
				if(processes[i].pointers>=borders[n]){
					proportions[i]++;
					totalProp++;
				}
			}
			for (int i=0; i<proportions.length && totalProp<memory.length; i++){
				proportions[i]++;
				totalProp++;
			}
		}
		
		//faktyczny proces przydzielania iloœci ramek ka¿demu procesowi
		int n = 0;
		for(int i=0; i<processes.length; i++){
			processes[i].beg=n;
			processes[i].end=n+proportions[i]-1;
			n+=proportions[i];
		}
		while(!tasks.isEmpty()){			//LRU
			
			Pointer current = tasks.poll();
			n=current.parent.beg;
			boolean placed=false;
			while(n<=current.parent.end && placed==false){
				if(memory[n]==null){
					memory[n]=current;
					time[n]=System.nanoTime();
					placed=true;
				}
				if(memory[n]==current){
					time[n]=System.nanoTime();
					placed=true;
				}
				n++;
			}
			if (placed==false){
				long min=time[current.parent.beg];
				int minInd=current.parent.beg;
				for(int i=current.parent.beg+1; i<current.parent.end; i++){
					if(time[i]<min){
						min=time[i];
						minInd=i;
					}
				}
				memory[minInd]=current;
				time[minInd]=System.nanoTime();
				current.parent.errors++;
			}	
		}
		int totalErrors=0;
		System.out.println("Braki stron dla przydzia³u proporcjonalnego:");
		for (int i=0; i<processes.length; i++){
			System.out.println("Proces "+(i+1)+".: "+processes[i].errors);
			totalErrors+=processes[i].errors;
		}
		System.out.println("Razem: "+totalErrors);
		
		resetMemory();
	}
}
