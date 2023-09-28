package _util;

public class Formater {

    // uzima bajt i vraca string, tj ako je bajt 5 vraca "0101" , 4 bita.
    public static String toBinaryString(byte b) {
        String binary = String.format("%4s", Integer.toBinaryString(b)).replace(' ', '0');
        return binary;
    }

    public static int toInt(byte read) {
        return (int) read;
    }
}
