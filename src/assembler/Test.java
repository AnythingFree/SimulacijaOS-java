package assembler;

public class Test {

	public static void main(String[] args) {
		Asembler a = new Asembler();
		String s = "program.asm";
		try {
			a.create_machine_code(s, "masinski_kod_program");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			a.execute_code("masinski_kod_program");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
