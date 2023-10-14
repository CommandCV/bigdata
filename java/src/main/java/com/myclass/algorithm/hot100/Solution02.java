package com.myclass.algorithm.hot100;

import com.myclass.common.entity.ListNode;
import com.myclass.common.utils.ListNodeUtils;

import java.util.Stack;

/**
 * 给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。
 *
 * 请你将两个数相加，并以相同形式返回一个表示和的链表。
 *
 * 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 * 示例 1：
 *
 *
 * 输入：l1 = [2,4,3], l2 = [5,6,4]
 * 输出：[7,0,8]
 * 解释：342 + 465 = 807.
 * 示例 2：
 *
 * 输入：l1 = [0], l2 = [0]
 * 输出：[0]
 * 示例 3：
 *
 * 输入：l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
 * 输出：[8,9,9,9,0,0,0,1]
 * 提示：
 *
 * 每个链表中的节点数在范围 [1, 100] 内
 * 0 <= Node.val <= 9
 * 题目数据保证列表表示的数字不含前导零
 * Related Topics
 * 递归
 * 链表
 * 数学
 *
 * 👍 9245
 * 👎 0
 */
public class Solution02 {

    public static ListNode addTwoNumbers(ListNode node1, ListNode node2) {
        if (node1 == null) {
            return node2;
        } else if (node2 == null) {
            return node1;
        }
        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        while (node1 != null) {
            stack1.push(node1.val);
            node1 = node1.next;
        }
        while (node2 != null) {
            stack2.push(node2.val);
            node2 = node2.next;
        }
        int addition = 0;
        ListNode head = new ListNode();
        ListNode node = head;
        while (!stack1.empty() && !stack2.empty()) {
            int value = stack1.pop() + stack2.pop() + addition;
            addition = value / 10;
            value = value % 10;
            node.next = new ListNode(value, null);
            node = node.next;
        }
        while (!stack1.empty()) {
            int value = stack1.pop() + addition;
            addition = value / 10;
            value = value % 10;
            node.next = new ListNode(value, null);
            node = node.next;
        }
        while (!stack2.empty()) {
            int value = stack2.pop() + addition;
            addition = value / 10;
            value = value % 10;
            node.next = new ListNode(value, null);
            node = node.next;
        }
        if (addition > 0) {
            node.next = new ListNode(addition, null);
        }
        return head.next;
    }

    public static void main(String[] args) {
        ListNodeUtils.printListNode(addTwoNumbers(ListNodeUtils.newListNode(2, 4, 3), ListNodeUtils.newListNode(5, 6, 4)));
        ListNodeUtils.printListNode(addTwoNumbers(ListNodeUtils.newListNode(0), ListNodeUtils.newListNode(0)));
        ListNodeUtils.printListNode(addTwoNumbers(ListNodeUtils.newListNode(9, 9, 9, 9, 9, 9, 9), ListNodeUtils.newListNode(9, 9, 9, 9)));
    }


}
