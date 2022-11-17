package com.myclass.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 给你一个整数 columnNumber ，返回它在 Excel 表中相对应的列名称。
 *
 * 例如：
 *
 * A -> 1
 * B -> 2
 * C -> 3
 * ...
 * Z -> 26
 * AA -> 27
 * AB -> 28
 * ...
 * 示例 1：
 *
 * 输入：columnNumber = 1
 * 输出："A"
 * 示例 2：
 *
 * 输入：columnNumber = 28
 * 输出："AB"
 * 示例 3：
 *
 * 输入：columnNumber = 701
 * 输出："ZY"
 * 示例 4：
 *
 * 输入：columnNumber = 2147483647
 * 输出："FXSHRXW"
 * 提示：
 *
 * 1 <= columnNumber <= 231 - 1
 * Related Topics
 * 数学
 * 字符串
 *
 * 👍 559
 * 👎 0
 */
public class Solution168 {

    static Map<Integer, String> map = new HashMap<>();
    static int scale = 26;
    static {
        map.put(0, "Z");
        for (int i = 0; i < scale - 1; i++) {
            Integer key = i + 1;
            char value = (char) (65 + i);
            map.put(key, String.valueOf(value));
        }
        System.out.println(map);
    }

    public static String convertToTitle(int columnNumber) {
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        int remainder;
        int num;
        while (columnNumber >= scale) {
            remainder = columnNumber % scale;
            num = flag ? remainder - 1 : remainder;
            sb.append(map.get(num));
            if (!flag && remainder == 0) flag = true;
            columnNumber = columnNumber / scale;
        }
        columnNumber = flag ? columnNumber - 1 : columnNumber;
        if (columnNumber > 0) {
            sb.append(map.get(columnNumber));
        }
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        System.out.println(convertToTitle(1));
        System.out.println(convertToTitle(28));
        System.out.println(convertToTitle(52));
        System.out.println(convertToTitle(78));
        System.out.println(convertToTitle(701));
        System.out.println(convertToTitle(702));
        System.out.println(convertToTitle(2147483647));
    }
}
