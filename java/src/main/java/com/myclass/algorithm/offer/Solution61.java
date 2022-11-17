package com.myclass.algorithm.offer;

/**
 * 从若干副扑克牌中随机抽 5 张牌，判断是不是一个顺子，即这5张牌是不是连续的。
 * 2～10为数字本身，A为1，J为11，Q为12，K为13，而大、小王为 0 ，可以看成任意数字。A 不能视为 14。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,3,4,5]
 * 输出: True
 * 示例 2:
 * <p>
 * 输入: [0,0,1,2,5]
 * 输出: True
 * 限制：
 * <p>
 * 数组长度为 5
 * <p>
 * 数组的数取值为 [0, 13] .
 * <p>
 * Related Topics
 * 数组
 * 排序
 * <p>
 * 👍 274
 * 👎 0
 */
public class Solution61 {

    public static boolean isStraight(int[] nums) {
        int n = 0;
        int temp;
        int min;
        int index;
        for (int i = 0; i < nums.length - 1; i++) {
            min = nums[i];
            index = i;
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] < min) {
                    min = nums[j];
                    index = j;
                }
            }
            temp = nums[i];
            nums[i] = min;
            nums[index] = temp;
            if (nums[i] == 0) {
                n++;
            }
        }
        for (int i = nums.length - 1; i > 0; i--) {
            if (nums[i] != 0 && nums[i - 1] != 0) {
                if (nums[i] ==  nums[i - 1]) {
                    return false;
                } else if (nums[i] - nums[i - 1] > 1) {
                    int diff = nums[i] - nums[i - 1];
                    if (n - diff + 1 < 0) {
                        return false;
                    }
                    n -= diff;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(isStraight(new int[]{3, 1, 0, 9, 9}));
        System.out.println(isStraight(new int[]{1, 2, 3, 4, 5}));
        System.out.println(isStraight(new int[]{0, 0, 1, 2, 5}));
        System.out.println(isStraight(new int[]{0, 0, 2, 2, 5}));
        System.out.println(isStraight(new int[]{2, 2, 2, 2, 5}));
        System.out.println(isStraight(new int[]{0, 0, 0, 0, 5}));
        System.out.println(isStraight(new int[]{0, 0, 0, 0, 0}));
    }
}
