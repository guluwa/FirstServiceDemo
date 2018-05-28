package cn.guluwa.firstservicedemo.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by guluwa on 2018/5/28.
 */
public class UkUtils {

    int binToDec(String value) {
        int number = 0;
        int power = 1;
        for (int i = value.length() - 1; i > 0; i--) {
            number += power * (value.charAt(i) == '1' ? 1 : 0);
            power *= 2;
        }
        return number;
    }

    public static String ab(String a, String b) {
        for (int d = 0; d < b.length() - 2; d += 3) {
            char c = b.charAt(d + 2);
            int e = 'a' <= c ? c - 87 : c - 48;
            int f = '+' == b.charAt(d + 1) ? Integer.valueOf(a) >>> e : Integer.valueOf(a) << e;
            a = String.valueOf((int) ('+' == b.charAt(d) ? Integer.valueOf(a) + (f & 4294967295L) : Integer.valueOf(a) ^ f));
        }
        return a;
    }

    public static String tk(String a, String TKK) {
        String[] array = new String[2];
        array[0] = TKK.substring(0, TKK.indexOf("."));
        array[1] = TKK.substring(TKK.indexOf(".") + 1);
        int h = Integer.valueOf(array[0]);
        int[] g = new int[500];
        int d = 0;
        for (int f = 0; f < a.length(); f++) {
            char c = a.charAt(f);
            if (128 > c) {
                g[d++] = c;
            } else {
                if (2048 > c) {
                    g[d++] = c >> 6 | 192;
                } else {
                    if (55296 == (c & 64512) && f + 1 < a.length() && 56320 == (a.charAt(f + 1) & 64512)) {
                        int l = 65536 + ((c & 1023) << 10) + (a.charAt(++f) & 1023);
                        g[d++] = l >> 18 | 240;
                        g[d++] = l >> 12 & 63 | 128;
                    } else {
                        g[d++] = c >> 12 | 224;
                        g[d++] = c >> 6 & 63 | 128;
                        g[d++] = c & 63 | 128;
                    }
                }
            }
        }
        a = String.valueOf(h);
        for (d = 0; d < g.length; d++) {
            if (g[d] != 0) {
                a = String.valueOf(Integer.valueOf(a) + g[d]);
                a = ab(a, "+-a^+6");
            }
        }
        a = ab(a, "+-3^+b+-f");
        a = String.valueOf((int) (Long.valueOf(a) ^ Long.valueOf(array[1])));
        if (0 > Integer.valueOf(a)) {
            a = String.valueOf((Integer.valueOf(a) & 2147483647L) + 2147483648L);
        }
        a = String.valueOf((int) (Long.valueOf(a) % 1E6));
        return a + "." + (Integer.valueOf(a) ^ h);
    }
}
