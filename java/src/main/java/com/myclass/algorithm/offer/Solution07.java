package com.myclass.algorithm.offer;

import com.myclass.common.entity.TreeNode;
import com.myclass.common.utils.TreeNodeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 输入某二叉树的前序遍历和中序遍历的结果，请构建该二叉树并返回其根节点。
 * <p>
 * 假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
 * <p>
 * 示例 1:
 * <p>
 * <p>
 * Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
 * Output: [3,9,20,null,null,15,7]
 * 示例 2:
 * <p>
 * Input: preorder = [-1], inorder = [-1]
 * Output: [-1]
 * 限制：
 * <p>
 * 0 <= 节点个数 <= 5000
 * <p>
 * 注意：本题与主站 105 题重复：https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
 * <p>
 * Related Topics
 * 树
 * 数组
 * 哈希表
 * 分治
 * 二叉树
 * <p>
 * 👍 938
 * 👎 0
 */
public class Solution07 {

    private static Map<Integer, Integer> map;

    public static TreeNode buildTree(int[] preorder, int[] inorder) {
        map = new HashMap<>(inorder.length);
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        return buildTree(preorder, 0, preorder.length - 1, 0, inorder.length - 1);
    }

    private static TreeNode buildTree(int[] preorder, int preLeft, int preRight, int inLeft, int inRight) {
        if (preLeft > preRight || inLeft > inRight) {
            return null;
        }
        TreeNode root = new TreeNode(preorder[preLeft]);
        int inorderRootIndex = map.get(preorder[preLeft]);
        root.left = buildTree(preorder, preLeft + 1, preLeft + (inorderRootIndex - inLeft), inLeft, inorderRootIndex - 1);
        root.right = buildTree(preorder, preLeft + (inorderRootIndex - inLeft) + 1, preRight, inorderRootIndex + 1, inRight);
        return root;
    }

    public static void main(String[] args) {
        TreeNodeUtils.printWithBFS(buildTree(new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7}));
    }

}
