package com.myclass.algorithm;

/**
 * 给定两个字符串str1和str2，输出两个字符串的最长公共子序列。如果最长公共子序列为空，则返回"-1"。目前给出的数据，仅仅会存在一个最长的公共子序列
 */
public class LongestCommonSubsequence2 {

    /**
     * longest common subsequence
     * @param s1 string字符串 the string
     * @param s2 string字符串 the string
     * @return string字符串
     */
    public static String LCS (String s1, String s2) {
        // write code here
        if (s1.length() == 0 || s2.length() == 0) {
            return "-1";
        }
        String[][] dp = new String[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = "";
        }
        for (int i = 0; i <= s2.length(); i++) {
            dp[0][i] = "";
        }
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + s1.charAt(i -1);
                } else {
                    dp[i][j] = dp[i - 1][j].length() >= dp[i][j - 1].length() ? dp[i - 1][j] : dp[i][j - 1];
                }
            }
        }
        return dp[s1.length()][s2.length()] == "" ? "-1" : dp[s1.length()][s2.length()];
    }

    public static void main(String[] args) {
        System.out.println(LCS("1A2C3D4B56", "B1D23A456A"));
    }
}
