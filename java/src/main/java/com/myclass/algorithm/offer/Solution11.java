package com.myclass.algorithm.offer;

/**
 * 把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。
 * <p>
 * 给你一个可能存在 重复 元素值的数组 numbers ，它原来是一个升序排列的数组，并按上述情形进行了一次旋转。请返回旋转数组的最小元素。例如，数组 [3,4,5,1,2] 为 [1,2,3,4,5] 的一次旋转，该数组的最小值为 1。
 * <p>
 * 注意，数组 [a[0], a[1], a[2], ..., a[n-1]] 旋转一次 的结果为数组 [a[n-1], a[0], a[1], a[2], ..., a[n-2]] 。
 * <p>
 * 示例 1：
 * <p>
 * 输入：numbers = [3,4,5,1,2]
 * 输出：1
 * 示例 2：
 * <p>
 * 输入：numbers = [2,2,2,0,1]
 * 输出：0
 * 提示：
 * <p>
 * n == numbers.length
 * 1 <= n <= 5000
 * -5000 <= numbers[i] <= 5000
 * numbers 原来是一个升序排序的数组，并进行了 1 至 n 次旋转
 * 注意：本题与主站 154 题相同：https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array-ii/
 * <p>
 * Related Topics
 * 数组
 * 二分查找
 * <p>
 * 👍 726
 * 👎 0
 */
public class Solution11 {

    public static int minArray(int[] numbers) {
        int left = 0;
        int right = numbers.length - 1;
        int mid = (right - left) / 2;
        while (left < right) {
            int endValue = numbers[right];
            int value = numbers[mid];
            if (value < endValue) {
                right = mid;
            } else if (value > endValue) {
                left = mid + 1;
            } else {
                right -= 1;
            }
            mid = (right - left) / 2 + left;
        }
        return numbers[mid];
    }


    public static void main(String[] args) {
        System.out.println(minArray(new int[]{3, 4, 5, 1, 2}));
        System.out.println(minArray(new int[]{2, 2, 2, 0, 1}));
        System.out.println(minArray(new int[]{5, 1, 2, 3, 4}));
        System.out.println(minArray(new int[]{3, 5, 7}));
        System.out.println(minArray(new int[]{3, 1}));
        System.out.println(minArray(new int[]{1}));
        System.out.println(minArray(new int[]{1, 1}));
        System.out.println(minArray(new int[]{2, 2, 2, 0, 1}));
        System.out.println(minArray(new int[]{1, 1, 1}));
        System.out.println(minArray(new int[]{3, 1, 1}));
    }
}
