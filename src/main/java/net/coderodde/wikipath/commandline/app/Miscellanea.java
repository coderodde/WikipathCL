package net.coderodde.wikipath.commandline.app;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class contains various utilities.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jul 26, 2016)
 */
public final class Miscellanea {

    private static final String BAR;
    
    static {
        final StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < 80; ++i) {
            sb.append('-');
        }
        
        BAR = sb.toString();
    }
    
    private Miscellanea() {}
    
    public static String nth(final int number) {
        return number == 1 ? "" : "s";
    }
    
    public static <T> T removeLast(final List<T> list) {
        return list.remove(list.size() - 1);
    }
    
    public static void print(final PrintStream out, final String text) {
        if (out != null) {
            out.println(text);
        }
    }
    
    public static int parseInt(final String integerString) {
        try {
            return Integer.parseInt(integerString);
        } catch (final NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Cannot convert \"" + integerString + "\" to an integer.");
        }
    }
    
    public static void bar() {
        System.out.println(BAR);
    }
}
