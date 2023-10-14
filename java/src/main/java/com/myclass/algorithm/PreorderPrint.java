package com.myclass.algorithm;

import com.myclass.common.entity.TreeNode;
import com.myclass.common.utils.TreeNodeUtils;

import java.util.Arrays;

public class PreorderPrint {


    public int[] preorderTraversal (TreeNode root) {
        // write code here
        if (root == null) {
            return new int[0];
        }
        int index = 0;
        int[] result = new int[100];
        preorder(root, result, index);
        return result;
    }

    public void preorder(TreeNode node, int[] result, int index) {
        result[index] = node.val;
        if (node.left != null) {
            preorder(node.left, result, ++index);
        }
        if (node.right != null) {
            preorder(node.right, result, ++index);
        }
    }


    public static void main(String[] args) {
        System.out.println(Arrays.toString(new PreorderPrint().preorderTraversal(TreeNodeUtils.newTreeNode(1, null, 2, 3))));
    }
}
