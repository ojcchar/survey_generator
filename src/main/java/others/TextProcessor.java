package others;

public class TextProcessor {


    public static boolean isSpecialChar(String str) {
        String[] split = str.split("[^a-zA-Z0-9]");
        boolean b = split.length != 1;
        return b;
    }

}
