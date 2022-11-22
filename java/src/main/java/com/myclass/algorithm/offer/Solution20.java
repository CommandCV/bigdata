package com.myclass.algorithm.offer;

/**
 * 请实现一个函数用来判断字符串是否表示数值（包括整数和小数）。
 *
 * 数值（按顺序）可以分成以下几个部分：
 * 1.若干空格
 * 2.一个 小数 或者 整数
 * 3.（可选）一个 'e' 或 'E' ，后面跟着一个 整数
 * 4.若干空格
 *
 * 小数（按顺序）可以分成以下几个部分：
 * 1.（可选）一个符号字符（'+' 或 '-'）
 * 2.下述格式之一：
 *      1.至少一位数字，后面跟着一个点 '.'
 *      2.至少一位数字，后面跟着一个点 '.' ，后面再跟着至少一位数字
 *      3.一个点 '.' ，后面跟着至少一位数字
 *
 * 整数（按顺序）可以分成以下几个部分：
 * 1.（可选）一个符号字符（'+' 或 '-'）
 * 2.至少一位数字
 *
 * 部分数值列举如下：
 * ["+100", "5e2", "-123", "3.1416", "-1E-16", "0123"]
 * 部分非数值列举如下：
 * ["12e", "1a3.14", "1.2.3", "+-5", "12e+5.4"]
 *
 * 示例 1：
 *
 * 输入：s = "0"
 * 输出：true
 *
 * 示例 2：
 *
 * 输入：s = "e"
 * 输出：false
 *
 * 示例 3：
 *
 * 输入：s = "."
 * 输出：false
 *
 * 示例 4：
 *
 * 输入：s = "    .1  "
 * 输出：true
 *
 * 提示：
 *
 * 1 <= s.length <= 20
 * s 仅含英文字母（大写和小写），数字（0-9），加号 '+' ，减号 '-' ，空格 ' ' 或者点 '.' 。
 * Related Topics
 * 字符串
 *
 * 👍 403
 * 👎 0
 */
public class Solution20 {

    public static boolean isNumber(String s) {
        if (s != null && s.trim().length() > 0) {
            s = s.trim();
            int splitIndex;
            if ((splitIndex = s.indexOf('e')) == -1) {
                splitIndex = s.indexOf('E');
            }
            if (splitIndex != -1) {
                if (splitIndex == 0 || splitIndex == s.length() - 1) {
                    return false;
                }
                return (isDecimal(s.substring(0, splitIndex)) || isInteger(s.substring(0, splitIndex)))
                        && isInteger(s.substring(splitIndex + 1));
            } else {
                return isDecimal(s) || isInteger(s);
            }
        }
        return false;
    }

    private static boolean isDecimal(String s) {
        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
            s = s.substring(1);
        }
        int index = s.indexOf('.');
        if (index == -1 || s.length() < 2) {
            return false;
        } else {
            if (index == 0) {
                return checkNumber(s.substring(1));
            } else if (index == s.length() - 1) {
                return checkNumber(s.substring(0, index));
            } else {
                return checkNumber(s.substring(0, index)) && checkNumber(s.substring(index + 1));
            }
        }
    }

    private static boolean isInteger(String s) {
        if (s.length() == 0) {
            return false;
        }
        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
            s = s.substring(1);
        }
        return checkNumber(s);
    }

    private static boolean checkNumber(String s) {
        if (s.length() == 0) {
            return false;
        } else {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                    return false;
                }
            }
            return true;
        }
    }


    public static void main(String[] args) {
        System.out.println(isNumber(""));

        System.out.println("-----");
        System.out.println(isNumber("3"));
        System.out.println(isNumber("+3"));
        System.out.println(isNumber("-3"));
        System.out.println(isNumber("-3a"));
        System.out.println(isNumber("a"));

        System.out.println("-----");
        System.out.println(isNumber("3."));
        System.out.println(isNumber("3.3"));
        System.out.println(isNumber(".3"));
        System.out.println(isNumber("3.a"));
        System.out.println(isNumber("a."));

        System.out.println("-----");
        System.out.println(isNumber("1e3"));
        System.out.println(isNumber("+1e3"));
        System.out.println(isNumber("+1.e3"));
        System.out.println(isNumber("e"));
        System.out.println(isNumber("1e"));
        System.out.println(isNumber("+1.e"));
        System.out.println(isNumber("+1.e3."));
        System.out.println(isNumber("+1.e+3.."));

        System.out.println(isNumber("0.."));
        System.out.println(isNumber(".1"));
        System.out.println(isNumber(".1."));

        System.out.println(isNumber("2e0"));


    }

}
