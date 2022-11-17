package com.myclass.algorithm.offer;

import com.myclass.common.entity.ListNode;
import com.myclass.common.utils.ListNodeUtils;

import java.util.Arrays;
import java.util.Stack;

/**
 * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。
 *
 * 示例 1：
 *
 * 输入：head = [1,3,2]
 * 输出：[2,3,1]
 * 限制：
 *
 * 0 <= 链表长度 <= 10000
 *
 * Related Topics
 * 栈
 * 递归
 * 链表
 * 双指针
 *
 * 👍 351
 * 👎 0
 */
public class Solution06 {

    public static int[] reversePrint(ListNode head) {
        Stack<Integer> stack = new Stack<>();
        while (head != null) {
            stack.push(head.val);
            head = head.next;
        }
        int[] result = new int[stack.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = stack.pop();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(reversePrint(ListNodeUtils.newListNode(1, 3, 2))));
        System.out.println(Arrays.toString(reversePrint(ListNodeUtils.newListNode(1, 2, 3))));
        System.out.println(Arrays.toString(reversePrint(ListNodeUtils.newListNode(1))));
        System.out.println(Arrays.toString(reversePrint(ListNodeUtils.newListNode())));
    }

}
