package com.myclass.algorithm.offer;

/**
 * 在一个 n * m 的二维数组中，每一行都按照从左到右 非递减 的顺序排序，每一列都按照从上到下 非递减 的顺序排序。请完成一个高效的函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
 * <p>
 * 示例:
 * <p>
 * 现有矩阵 matrix 如下：
 * <p>
 * [
 * [1,   4,  7, 11, 15],
 * [2,   5,  8, 12, 19],
 * [3,   6,  9, 16, 22],
 * [10, 13, 14, 17, 24],
 * [18, 21, 23, 26, 30]
 * ]
 * 给定 target = 5，返回 true。
 * <p>
 * 给定 target = 20，返回 false。
 * <p>
 * 限制：
 * <p>
 * 0 <= n <= 1000
 * <p>
 * 0 <= m <= 1000
 * <p>
 * 注意：本题与主站 240 题相同：https://leetcode-cn.com/problems/search-a-2d-matrix-ii/
 * <p>
 * Related Topics
 * 数组
 * 二分查找
 * 分治
 * 矩阵
 * <p>
 * 👍 822
 * 👎 0
 */
public class Solution04 {

    public static boolean findNumberIn2DArray(int[][] matrix, int target) {
        for (int i = 0; i < matrix.length; i++) {
            int left = 0;
            int right = matrix[i].length - 1;
            int mid = (left + right) / 2;
            while (left <= right) {
                if (matrix[i][mid] == target) {
                    return true;
                } else if (target < matrix[i][mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
                mid = (right - left) / 2 + left;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] test1 = new int[][]{
                {1, 4, 7, 11, 15},
                {2, 5, 8, 12, 19},
                {3, 6, 9, 16, 22},
                {10, 13, 14, 17, 24},
                {18, 21, 23, 26, 30}
        };
        System.out.println(findNumberIn2DArray(test1, 5));
        System.out.println(findNumberIn2DArray(test1, 20));
        System.out.println(findNumberIn2DArray(test1, 100));
        System.out.println(findNumberIn2DArray(test1, 0));
        System.out.println(findNumberIn2DArray(test1, 1));
    }

}
