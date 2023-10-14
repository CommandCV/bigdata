package com.myclass.algorithm.offer;

import com.myclass.common.entity.TreeNode;
import com.myclass.common.utils.TreeNodeUtils;

/**
 * 输入两棵二叉树A和B，判断B是不是A的子结构。(约定空树不是任意一个树的子结构)
 * B是A的子结构， 即 A中有出现和B相同的结构和节点值。
 *
 * 例如:
 * 给定的树 A:
 *      3
 *      / \
 *     4   5
 *    / \
 *   1   2
 * 给定的树 B：
 *    4
 *    /
 *   1
 * 返回 true，因为 B 与 A 的一个子树拥有相同的结构和节点值。
 *
 * 示例 1：
 * 输入：A = [1,2,3], B = [3,1]
 * 输出：false
 *
 * 示例 2：
 * 输入：A = [3,4,5,1,2], B = [4,1]
 * 输出：true
 * 限制：
 *
 * 0 <= 节点个数 <= 10000
 * Related Topics
 * 树
 * 深度优先搜索
 * 二叉树
 *
 * 👍 661
 * 👎 0
 *
 */
public class Solution26 {

    public static boolean isSubStructure(TreeNode node1, TreeNode node2) {
        if (node1 == null || node2 == null) {
            return false;
        }
        return dfs(node1, node2) || isSubStructure(node1.left, node2) || isSubStructure(node1.right, node2);
    }

    public static boolean dfs(TreeNode node1, TreeNode node2) {
        if (node2 == null) return true;
        if (node1 == null) return false;
        return node1.val == node2.val && dfs(node1.left, node2.left) && dfs(node1.right, node2.right);
    }

    public static void main(String[] args) {
        System.out.println(isSubStructure(TreeNodeUtils.newTreeNode(1, 2, 3), TreeNodeUtils.newTreeNode(3, 1)));
        System.out.println(isSubStructure(TreeNodeUtils.newTreeNode(3, 4, 5, 1, 2), TreeNodeUtils.newTreeNode(4, 1)));
        System.out.println(isSubStructure(TreeNodeUtils.newTreeNode(4, 2, 3, 4, 5, 6, 7, 8, 9), TreeNodeUtils.newTreeNode(4, 8, 9)));
    }
}
