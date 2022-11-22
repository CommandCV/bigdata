package com.myclass.algorithm.offer;

import java.util.Arrays;

/**
 * 输入数字 n，按顺序打印出从 1 到最大的 n 位十进制数。比如输入 3，则打印出 1、2、3 一直到最大的 3 位数 999。
 *
 * 示例 1:
 *
 * 输入: n = 1
 * 输出: [1,2,3,4,5,6,7,8,9]
 * 说明：
 *
 * 用返回一个整数列表来代替打印
 * n 为正整数
 * Related Topics
 * 数组
 * 数学
 *
 * 👍 264
 * 👎 0
 */
public class Solution17 {

    public static int[] printNumbers(int n) {
        int num = 1;
        while (n > 0) {
            num *= 10;
            n--;
        }
        int[] result = new int[num - 1];
        for (int i = 1; i <= num - 1; i++) {
            result[i - 1] = i;
        }
        return result;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.toString(printNumbers(1)));
    }

}
