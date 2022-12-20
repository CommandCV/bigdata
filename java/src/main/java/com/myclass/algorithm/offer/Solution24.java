package com.myclass.algorithm.offer;

import com.myclass.common.entity.ListNode;
import com.myclass.common.utils.ListNodeUtils;

import java.util.Stack;

/**
 * 定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。
 *
 * 示例:
 *
 * 输入: 1->2->3->4->5->NULL
 * 输出: 5->4->3->2->1->NULL
 * 限制：
 *
 * 0 <= 节点个数 <= 5000
 *
 * 注意：本题与主站 206 题相同：https://leetcode-cn.com/problems/reverse-linked-list/
 *
 * Related Topics
 * 递归
 * 链表
 *
 * 👍 511
 * 👎 0
 */
public class Solution24 {

    public static ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode tail = null;
        while (head != null) {
            ListNode current = head;
            head = head.next;
            current.next = tail;
            tail = current;
        }
        return tail;
    }

    public static ListNode reverseList2(ListNode head) {
        if (head == null) {
            return null;
        }
        Stack<ListNode> stack = new Stack<>();
        while (head != null) {
            stack.push(head);
            head = head.next;
        }
        ListNode result = new ListNode();
        ListNode newHead = result;
        while (!stack.empty()) {
            result.next = stack.pop();
            result = result.next;
        }
        result.next = null;
        return newHead.next;
    }

    public static ListNode reverseList3(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode node = reverseList(head.next);
        if (node != null) {
            ListNode current = node;
            while (current.next != null) {
                current = current.next;
            }
            current.next = head;
        } else {
            node = head;
        }
        head.next = null;
        return node;
    }

    public static void main(String[] args) {
        ListNodeUtils.printListNode(reverseList(ListNodeUtils.newListNode(1, 2, 3, 4, 5)));
        ListNodeUtils.printListNode(reverseList(ListNodeUtils.newListNode(1)));
        ListNodeUtils.printListNode(reverseList(ListNodeUtils.newListNode()));
    }

}
