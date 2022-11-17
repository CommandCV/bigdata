package com.myclass.algorithm;

/**
 * 已知函数 signFunc(x) 将会根据 x 的正负返回特定值：
 * <p>
 * 如果 x 是正数，返回 1 。
 * 如果 x 是负数，返回 -1 。
 * 如果 x 是等于 0 ，返回 0 。
 * 给你一个整数数组 nums 。令 product 为数组 nums 中所有元素值的乘积。
 * <p>
 * 返回 signFunc(product) 。
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [-1,-2,-3,-4,3,2,1]
 * 输出：1
 * 解释：数组中所有值的乘积是 144 ，且 signFunc(144) = 1
 * 示例 2：
 * <p>
 * 输入：nums = [1,5,0,2,-3]
 * 输出：0
 * 解释：数组中所有值的乘积是 0 ，且 signFunc(0) = 0
 * 示例 3：
 * <p>
 * 输入：nums = [-1,1,-1,1,-1]
 * 输出：-1
 * 解释：数组中所有值的乘积是 -1 ，且 signFunc(-1) = -1
 * 提示：
 * <p>
 * 1 <= nums.length <= 1000
 * -100 <= nums[i] <= 100
 * Related Topics
 * 数组
 * 数学
 * <p>
 * 👍 57
 * 👎 0
 */
public class Solution1822 {

    public static int arraySign(int[] nums) {
        int count = 0;
        for (int num : nums) {
            if (num == 0) {
                return 0;
            } else if (num < 0) {
                count++;
            }
        }
        return count % 2 == 0 ? 1 : -1;
    }

    public static void main(String[] args) {
        System.out.println(arraySign(new int[]{-1, -2, -3, -4, 3, 2, 1}));
        System.out.println(arraySign(new int[]{1, 5, 0, 2, -3}));
        System.out.println(arraySign(new int[]{-1, 1, -1, 1, -1}));
    }
}
