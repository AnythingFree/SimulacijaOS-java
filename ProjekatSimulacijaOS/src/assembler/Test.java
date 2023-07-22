package assembler;

public class Test {

	public static void main(String[] args) {
		Asembler a = new Asembler();
		String s = "neko_ime.asm";
		a.create_machine_code(s, "masinski_kod_neko_ime"); // ocekuje se da napravi .txt fajl sa masinskim kodom
		//a.execute_code();
	}

}
