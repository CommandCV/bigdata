package com.myclass.algorithm.offer;

/**
 * 给定一个 m x n 二维字符网格 board 和一个字符串单词 word 。如果 word 存在于网格中，返回 true ；否则，返回 false 。
 * <p>
 * 单词必须按照字母顺序，通过相邻的单元格内的字母构成，其中“相邻”单元格是那些水平相邻或垂直相邻的单元格。同一个单元格内的字母不允许被重复使用。
 * <p>
 * 例如，在下面的 3×4 的矩阵中包含单词 "ABCCED"（单词中的字母已标出）。
 * <p>
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
 * 输出：true
 * 示例 2：
 * <p>
 * 输入：board = [["a","b"],["c","d"]], word = "abcd"
 * 输出：false
 * 提示：
 * <p>
 * m == board.length
 * n = board[i].length
 * 1 <= m, n <= 6
 * 1 <= word.length <= 15
 * board 和 word 仅由大小写英文字母组成
 * 注意：本题与主站 79 题相同：https://leetcode-cn.com/problems/word-search/
 * <p>
 * Related Topics
 * 数组
 * 回溯
 * 矩阵
 * <p>
 * 👍 706
 * 👎 0
 */
public class Solution12 {

    public static boolean exist(char[][] board, String word) {
        int row = board.length;
        int column = board[0].length;
        int[][] table = new int[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (check(board, table, i, j, word, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean check(char[][] board, int[][] table, int i, int j, String word, int index) {
        if (word.charAt(index) != board[i][j]) {
            return false;
        } else if (index == word.length() - 1) {
            return true;
        }

        table[i][j] = 1;
        if (checkIndex(board, table, i, j + 1) && check(board, table, i, j + 1, word, index + 1)) {
            return true;
        }
        if (checkIndex(board, table, i + 1, j) && check(board, table, i + 1, j, word, index + 1)) {
            return true;
        }
        if (checkIndex(board, table, i, j - 1) && check(board, table, i, j - 1, word, index + 1)) {
            return true;
        }
        if (checkIndex(board, table, i - 1, j) && check(board, table, i - 1, j, word, index + 1)) {
            return true;
        }
        table[i][j] = 0;
        return false;
    }

    private static boolean checkIndex(char[][] board, int[][] table, int i, int j) {
        return i >= 0 && i < board.length && j >= 0 && j < board[i].length && table[i][j] == 0;
    }

    public static void main(String[] args) {
        System.out.println(exist(new char[][]{{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}}, "ABCCED"));
        System.out.println(exist(new char[][]{{'a', 'b'}, {'c', 'd'}}, "abcd"));
    }

}
