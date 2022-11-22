package com.myclass.algorithm.offer;

/**
 * 一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
 *
 * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
 *
 * 示例 1：
 *
 * 输入：n = 2
 * 输出：2
 * 示例 2：
 *
 * 输入：n = 7
 * 输出：21
 * 示例 3：
 *
 * 输入：n = 0
 * 输出：1
 * 提示：
 *
 * 0 <= n <= 100
 * 注意：本题与主站 70 题相同：https://leetcode-cn.com/problems/climbing-stairs/
 *
 * Related Topics
 * 记忆化搜索
 * 数学
 * 动态规划
 *
 * 👍 340
 * 👎 0
 */
public class Solution10_2 {

    private static int[] cache = new int[101];

    public static int numWays(int n) {
        if (n == 0 || n == 1) {
            return 1;
        } else {
            int value = cache[n];
            if (value == 0) {
                value = numWays(n - 1) + numWays(n - 2);
            }
            cache[n] = value % 1000000007;
            return cache[n];
        }
    }

    public static void main(String[] args) {
        System.out.println(numWays(2));
        System.out.println(numWays(7));
        System.out.println(numWays(0));
    }

}
