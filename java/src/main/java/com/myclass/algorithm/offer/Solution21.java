package com.myclass.algorithm.offer;

import java.util.Arrays;

/**
 * 输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数在数组的前半部分，所有偶数在数组的后半部分。
 *
 * 示例：
 *
 * 输入：nums = [1,2,3,4]
 * 输出：[1,3,2,4]
 * 注：[3,1,2,4] 也是正确的答案之一。
 * 提示：
 *
 * 0 <= nums.length <= 50000
 * 0 <= nums[i] <= 10000
 * Related Topics
 * 数组
 * 双指针
 * 排序
 *
 * 👍 268
 * 👎 0
 */
public class Solution21 {

    public static int[] exchange(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return nums;
        }
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if ((nums[i] & 1) != 1) {
                while (index < i || (index < nums.length && (nums[index] & 1) != 1)) {
                    index++;
                }
                if (index >= nums.length) {
                    break;
                } else {
                    int tmp = nums[index];
                    nums[index] = nums[i];
                    nums[i] = tmp;
                }
            }
        }
        return nums;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(exchange(new int[]{1, 2, 3, 4})));
        System.out.println(Arrays.toString(exchange(new int[]{1, 2, 3, 4, 5})));
        System.out.println(Arrays.toString(exchange(new int[]{1})));
        System.out.println(Arrays.toString(exchange(new int[]{1, 3, 5})));
        System.out.println(Arrays.toString(exchange(new int[]{2, 4, 6})));
        System.out.println(Arrays.toString(exchange(new int[]{1, 3, 2, 6})));
        System.out.println(Arrays.toString(exchange(new int[]{})));
    }

}
