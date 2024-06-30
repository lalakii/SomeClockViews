package cn.lalaki.desk.view.global;

import java.util.HashMap;

public class ClockUtils {
    private final static HashMap<Integer, String> romanMap = new HashMap<>() {
        {
            put(12, "XII");
            put(11, "XI");
            put(10, "X");
            put(9, "IX");
            put(8, "VIII");
            put(7, "VII");
            put(6, "VI");
            put(5, "V");
            put(4, "IV");
            put(3, "III");
            put(2, "II");
            put(1, "I");
        }
    };
    private final static HashMap<Integer, String> arabicMap = new HashMap<>() {{
        put(12, "١٢");
        put(11, "١١");
        put(10, "١٠\t");
        put(9, "٩");
        put(8, "٨");
        put(7, "٧");
        put(6, "٦");
        put(5, "٥");
        put(4, "٤");
        put(3, "٣");
        put(2, "٢");
        put(1, "١");
    }};

    public static String toRoman(int number) {
        return romanMap.get(number);
    }

    public static String toArabic(int number) {
        return arabicMap.get(number);
    }
}