package com.myclass.algorithm;

import com.myclass.common.entity.TreeNode;
import com.myclass.common.utils.TreeNodeUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 给你二叉树的根节点 root ，返回其节点值的 锯齿形层序遍历 。（即先从左往右，再从右往左进行下一层遍历，以此类推，层与层之间交替进行）。
 * <p>
 * 示例 1：
 * <p>
 * <p>
 * 输入：root = [3,9,20,null,null,15,7]
 * 输出：[[3],[20,9],[15,7]]
 * 示例 2：
 * <p>
 * 输入：root = [1]
 * 输出：[[1]]
 * 示例 3：
 * <p>
 * 输入：root = []
 * 输出：[]
 * 提示：
 * <p>
 * 树中节点数目在范围 [0, 2000] 内
 * -100 <= Node.val <= 100
 * Related Topics
 * 树
 * 广度优先搜索
 * 二叉树
 * <p>
 * 👍 705
 * 👎 0
 */
public class Solution103 {

    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        Deque<TreeNode> deque = new ArrayDeque<>();
        boolean flag = true;
        if (root != null) {
            deque.offerFirst(root);
        }
        while (!deque.isEmpty()) {
            List<Integer> list = new ArrayList<>();
            int size = deque.size();
            Deque<TreeNode> nodes = new ArrayDeque<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = deque.pollFirst();
                if (node == null) {
                    return null;
                }
                if (flag) {
                    if (node.left != null) {
                        nodes.offerFirst(node.left);
                    }
                    if (node.right != null) {
                        nodes.offerFirst(node.right);
                    }
                } else {
                    if (node.right != null) {
                        nodes.offerFirst(node.right);
                    }
                    if (node.left != null) {
                        nodes.offerFirst(node.left);
                    }
                }
                list.add(node.val);
            }
            deque.addAll(nodes);
            result.add(list);
            flag = !flag;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(zigzagLevelOrder(TreeNodeUtils.newTreeNode(3, 9, 20, null, null, 15, 7)));
        System.out.println(zigzagLevelOrder(TreeNodeUtils.newTreeNode(3, 9, 20, 3, 2, 15, 7)));
        System.out.println(zigzagLevelOrder(TreeNodeUtils.newTreeNode(3, 9, 20, 3, null, 5, 7)));
        System.out.println(zigzagLevelOrder(TreeNodeUtils.newTreeNode(1)));
        System.out.println(zigzagLevelOrder(TreeNodeUtils.newTreeNode()));
    }
}
