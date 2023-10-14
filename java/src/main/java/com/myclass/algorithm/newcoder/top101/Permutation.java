package com.myclass.algorithm.newcoder.top101;

import java.util.ArrayList;
import java.util.Collections;

/**
 * BM58 字符串的排列
 */
public class Permutation {

    static ArrayList<String> result = new ArrayList<String>();

    public static ArrayList<String> Permutation(String str) {
        if (str.isEmpty()) {
            result.add(str);
        }
        ArrayList<Character> list = new ArrayList<>(str.length());
        for (int i = 0; i < str.length(); i++) {
            list.add(str.charAt(i));
        }
        find(list, new StringBuilder(), str);
        return result;
    }

    public static void find(ArrayList<Character> list, StringBuilder sb , String str) {
        if (sb.length() == str.length() && !result.contains(sb.toString())) {
            result.add(sb.toString());
            return;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!list.contains(str.charAt(i))) {
                continue;
            }
            sb.append(str.charAt(i));
            list.remove((Object) str.charAt(i));
            find(list, sb, str);
            sb.delete(sb.length() - 1, sb.length());
            list.add(str.charAt(i));
        }
    }

    public static void main(String[] args) {
        System.out.println(Permutation("qwertyuio"));
    }
}
