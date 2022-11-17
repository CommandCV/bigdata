package com.myclass.algorithm;

/**
 * 编写一个函数来查找字符串数组中的最长公共前缀。
 * <p>
 * 如果不存在公共前缀，返回空字符串 ""。
 * <p>
 * 示例 1：
 * <p>
 * 输入：strs = ["flower","flow","flight"]
 * 输出："fl"
 * 示例 2：
 * <p>
 * 输入：strs = ["dog","racecar","car"]
 * 输出：""
 * 解释：输入不存在公共前缀。
 * 提示：
 * <p>
 * 1 <= strs.length <= 200
 * 0 <= strs[i].length <= 200
 * strs[i] 仅由小写英文字母组成
 * Related Topics
 * 字符串
 * <p>
 * 👍 2483
 * 👎 0
 */
public class Solution14 {

    public static String longestCommonPrefix(String[] strs) {
        StringBuilder result = new StringBuilder();
        String minStr = strs[0];
        int min = minStr.length();
        for (String str : strs) {
            for (int i = 0; i < minStr.length() && i < str.length(); i++) {

            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(longestCommonPrefix(new String[]{"flower", "flow", "flight"}));
        System.out.println(longestCommonPrefix(new String[]{"flower", "flow", ""}));
        System.out.println(longestCommonPrefix(new String[]{"dog", "racecar", "car"}));
        System.out.println(longestCommonPrefix(new String[]{"dog", "racecar", ""}));
        System.out.println(longestCommonPrefix(new String[]{""}));
        System.out.println(longestCommonPrefix(new String[]{"", ""}));
    }

}
