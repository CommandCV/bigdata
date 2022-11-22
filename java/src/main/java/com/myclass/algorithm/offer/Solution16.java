package com.myclass.algorithm.offer;

import java.math.BigDecimal;

/**
 * 实现 pow(x, n) ，即计算 x 的 n 次幂函数（即，xn）。不得使用库函数，同时不需要考虑大数问题。
 *
 * 示例 1：
 *
 * 输入：x = 2.00000, n = 10
 * 输出：1024.00000
 * 示例 2：
 *
 * 输入：x = 2.10000, n = 3
 * 输出：9.26100
 * 示例 3：
 *
 * 输入：x = 2.00000, n = -2
 * 输出：0.25000
 * 解释：2-2 = 1/22 = 1/4 = 0.25
 * 提示：
 *
 * -100.0 < x < 100.0
 * -231 <= n <= 231-1
 * -104 <= xn <= 104
 * 注意：本题与主站 50 题相同：https://leetcode-cn.com/problems/powx-n/
 *
 * Related Topics
 * 递归
 * 数学
 *
 * 👍 363
 * 👎 0
 */
public class Solution16 {

    static double[] cache = null;

    public static double test(double x, int n) {
        if (cache == null) {
            cache = new double[Math.abs(n)];
        }
        if (n == 0) {
            return 1d;
        }
        double value = cache[Math.abs(n) - 1];
        if (value == 0) {
            if (n > 0) {
                value = x * myPow(x, n - 1);
            } else {
                value = (1 / x) * myPow(x, n + 1);
            }
            cache[Math.abs(n) - 1] = value;
        }
        return cache[Math.abs(n) - 1];
    }

    public static double myPow(double x, int n) {
        return n >=0 ? multiply(x, n) : 1 / multiply(x, -(long) n);
    }

    public static double multiply(double x, long n) {
        if (n == 0) {
            return 1d;
        } else if (n == 1) {
            return x;
        }
        double result = multiply(x, n / 2);
        return n % 2 == 0? result * result : result * result * x;
    }


    public static void main(String[] args) {
        System.out.println(myPow(2.00000, 10));
        System.out.println(myPow(2.10000, 3));
        System.out.println(myPow(2.00000, -2));
        System.out.println(myPow(0.00001, 2147483647));
    }

}
