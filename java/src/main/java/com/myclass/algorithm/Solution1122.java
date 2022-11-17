package com.myclass.algorithm;

import java.util.Arrays;

/**
 * 给你两个数组，arr1 和 arr2，arr2 中的元素各不相同，arr2 中的每个元素都出现在 arr1 中。
 * <p>
 * 对 arr1 中的元素进行排序，使 arr1 中项的相对顺序和 arr2 中的相对顺序相同。未在 arr2 中出现过的元素需要按照升序放在 arr1 的末尾。
 * <p>
 * 示例 1：
 * <p>
 * 输入：arr1 = [2,3,1,3,2,4,6,7,9,2,19], arr2 = [2,1,4,3,9,6]
 * 输出：[2,2,2,1,4,3,3,9,6,7,19]
 * 示例 2:
 * <p>
 * 输入：arr1 = [28,6,22,8,44,17], arr2 = [22,28,8,6]
 * 输出：[22,28,8,6,17,44]
 * 提示：
 * <p>
 * 1 <= arr1.length, arr2.length <= 1000
 * 0 <= arr1[i], arr2[i] <= 1000
 * arr2 中的元素 arr2[i] 各不相同
 * arr2 中的每个元素 arr2[i] 都出现在 arr1 中
 * Related Topics
 * 数组
 * 哈希表
 * 计数排序
 * 排序
 * <p>
 * 👍 242
 * 👎 0
 */
public class Solution1122 {

    public static int[] relativeSortArray(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length];
        int[] table = new int[1001];
        for (int i : arr1) {
            table[i]++;
        }
        int index = 0;
        for (int i : arr2) {
            while (table[i] > 0) {
                result[index++] = i;
                table[i]--;
            }
        }
        for (int i = 0; i < table.length; i++) {
            while (table[i] > 0) {
                result[index++] = i;
                table[i]--;
            }
        }
        return result;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.toString(relativeSortArray(new int[]{2, 3, 1, 3, 2, 4, 6, 7, 9, 2, 19}, new int[]{2, 1, 4, 3, 9, 6})));
        System.out.println(Arrays.toString(relativeSortArray(new int[]{28, 6, 22, 8, 44, 17}, new int[]{22, 28, 8, 6})));
        System.out.println(Arrays.toString(relativeSortArray(new int[]{28, 6, 22, 8, 44, 17}, new int[]{})));
        System.out.println(Arrays.toString(relativeSortArray(new int[]{}, new int[]{22, 28, 8, 6})));
        System.out.println(Arrays.toString(relativeSortArray(new int[]{}, new int[]{})));
    }


}
