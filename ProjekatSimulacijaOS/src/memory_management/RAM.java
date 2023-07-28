package memory_management;

public class RAM {
	// ovo pretstavlja memmoriju, pozicija u nizu je njegova adresa,
	public static int[] niz = new int[100];


	public static void printMemory() {
		// prikazuju se informacije o zauzecu RAM memorije na racunaru
		System.out.println("RAM memory usage: ");
		for (int i = 0; i < niz.length; i++) {
			if (niz[i] != 0) {
				System.out.println("Address: " + i + " Value: " + niz[i]);
			}
		}
	}

}
