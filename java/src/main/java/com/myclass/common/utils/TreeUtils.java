package com.myclass.common.utils;

import com.myclass.common.entity.TreeNode;

public class TreeUtils {

    public static TreeNode newTreeNode(Integer... values) {
        TreeNode root = null;
        if (values.length > 0 ) {
            root = new TreeNode(values[0], null, null);
            buildTreeNode(root, 0, values);
        }
        return root;
    }

    private static void buildTreeNode(TreeNode root, int index, Integer...values) {
        if (index < (values.length - 1) / 2) {
            if (index * 2 + 1 < values.length && values[index * 2 + 1] != null) {
                root.left = new TreeNode(values[index * 2 + 1], null, null);
                buildTreeNode(root.left, index * 2 + 1, values);
            }
            if (index * 2 + 2 < values.length && values[index * 2 + 2] != null) {
                root.right = new TreeNode(values[index * 2 + 2], null, null);
                buildTreeNode(root.right, index * 2 + 2, values);
            }
        }
    }

    public static void preorder(TreeNode root) {
        if (root != null) {
            preorderPrint(root);
        }
        System.out.println();
    }

    private static void preorderPrint(TreeNode root) {
        System.out.print(root.val);
        if (root.left != null) {
            System.out.print(",");
            preorderPrint(root.left);
        }
        if (root.right != null) {
            System.out.print(",");
            preorderPrint(root.right);
        }
    }

    public static void inorder(TreeNode root) {
        if (root != null) {
            inorderPrint(root);
        }
        System.out.println();
    }

    private static void inorderPrint(TreeNode root) {
        if (root.left != null) {
            inorderPrint(root.left);
        }
        System.out.println(root.val);
        if (root.right != null) {
            inorderPrint(root.right);
        }
    }

    public static void postorder(TreeNode root) {
        if (root != null) {
            postorderPrint(root);
        }
        System.out.println();
    }

    private static void postorderPrint(TreeNode root) {
        if (root.left != null) {
            postorderPrint(root.left);
        }
        if (root.right != null) {
            postorderPrint(root.right);
        }
        System.out.println(root.val);
    }

}
