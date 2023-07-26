package memory_management;

public class RAM {
	// ovo pretstavlja memmoriju, pozicija u nizu je njegova adresa,
	public static int[] niz = new int[100];

	public static void print_status() {
		System.out.println(niz);
	}

}
