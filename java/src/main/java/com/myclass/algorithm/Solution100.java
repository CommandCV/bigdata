package com.myclass.algorithm;

import com.myclass.common.entity.TreeNode;
import com.myclass.common.utils.TreeUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 给你两棵二叉树的根节点 p 和 q ，编写一个函数来检验这两棵树是否相同。
 * <p>
 * 如果两个树在结构上相同，并且节点具有相同的值，则认为它们是相同的。
 * <p>
 * 示例 1：
 * <p>
 * <p>
 * 输入：p = [1,2,3], q = [1,2,3]
 * 输出：true
 * 示例 2：
 * <p>
 * <p>
 * 输入：p = [1,2], q = [1,null,2]
 * 输出：false
 * 示例 3：
 * <p>
 * <p>
 * 输入：p = [1,2,1], q = [1,1,2]
 * 输出：false
 * 提示：
 * <p>
 * 两棵树上的节点数目都在范围 [0, 100] 内
 * -104 <= Node.val <= 104
 * Related Topics
 * 树
 * 深度优先搜索
 * 广度优先搜索
 * 二叉树
 * <p>
 * 👍 920
 * 👎 0
 */
public class Solution100 {

    public static boolean isSameTree(TreeNode p, TreeNode q) {
        Queue<TreeNode> queue1 = new LinkedList<>();
        Queue<TreeNode> queue2 = new LinkedList<>();
        if (p != null) {
            queue1.offer(p);
        }
        if (q != null) {
            queue2.offer(q);
        }
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            int size1 = queue1.size();
            int size2 = queue2.size();
            for (int i = 0; i < size1 && i < size2; i++) {
                TreeNode node1 = queue1.poll();
                TreeNode node2 = queue2.poll();
                if ((node1 == null && node2 != null) || (node1 != null && node2 == null)
                        || (node1 != null && node2 != null && node1.val != node2.val)) {
                    return false;
                }
                if (node1 != null) {
                    queue1.offer(node1.left);
                    queue1.offer(node1.right);
                }
                if (node2 != null) {
                    queue2.offer(node2.left);
                    queue2.offer(node2.right);
                }
            }
        }
        return queue1.isEmpty() && queue2.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println(isSameTree(TreeUtils.newTreeNode(1, 2, 3), TreeUtils.newTreeNode(1, 2, 3)));
        System.out.println(isSameTree(TreeUtils.newTreeNode(1, 2), TreeUtils.newTreeNode(1, null, 2)));
        System.out.println(isSameTree(TreeUtils.newTreeNode(1, 2, 1), TreeUtils.newTreeNode(1, 1, 2)));
        System.out.println(isSameTree(TreeUtils.newTreeNode(), TreeUtils.newTreeNode(1, 1, 2)));
        System.out.println(isSameTree(TreeUtils.newTreeNode(), TreeUtils.newTreeNode()));
        System.out.println(isSameTree(TreeUtils.newTreeNode(1, null, 2), TreeUtils.newTreeNode(1, 2)));
        System.out.println(isSameTree(TreeUtils.newTreeNode(1, null, 2), TreeUtils.newTreeNode(1, null, 2)));
    }
}
