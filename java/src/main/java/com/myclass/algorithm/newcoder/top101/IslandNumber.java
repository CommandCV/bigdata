package com.myclass.algorithm.newcoder.top101;

public class IslandNumber {

    /**
     * 判断岛屿数量
     * @param grid char字符型二维数组
     * @return int整型
     */
    public static int solve (char[][] grid) {
        // write code here
        if (grid == null) {
            return 0;
        }
        boolean[][] visit = new boolean[grid.length][grid[0].length];
        int num = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (!visit[i][j] && grid[i][j] == '1' && move(grid, i, j, visit)) {
                    num++;
                }
            }
        }
        return num;
    }

    public static boolean move(char[][] grid, int i, int j, boolean[][] visit) {
        if (i < grid.length && j < grid[i].length && grid[i][j] == '1') {
            visit[i][j] = true;
            boolean right = move(grid, i, j + 1, visit);
            boolean down = move(grid, i + 1, j, visit);
            return right || down;
        }
        return false;
    }

    public static void main(String[] args) {
        char[][] grid = new char[][]{
                {'1', '1', '0', '0', '0'},
                {'0', '1', '0', '1', '1'},
                {'0', '0', '0', '1', '1'},
                {'0', '0', '0', '0', '0'},
                {'0', '0', '1', '1', '1'}
        };
        System.out.println(solve(grid));
    }

}
