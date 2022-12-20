package com.myclass.algorithm.offer;

import com.myclass.common.entity.ListNode;
import com.myclass.common.utils.ListNodeUtils;

/**
 * 输入一个链表，输出该链表中倒数第k个节点。为了符合大多数人的习惯，本题从1开始计数，即链表的尾节点是倒数第1个节点。
 *
 * 例如，一个链表有 6 个节点，从头节点开始，它们的值依次是 1、2、3、4、5、6。这个链表的倒数第 3 个节点是值为 4 的节点。
 *
 * 示例：
 *
 * 给定一个链表: 1->2->3->4->5, 和 k = 2.
 *
 * 返回链表 4->5.
 * Related Topics
 * 链表
 * 双指针
 *
 * 👍 417
 * 👎 0
 */
public class Solution22 {

    public static ListNode getKthFromEnd(ListNode head, int k) {
        ListNode first;
        ListNode current = head;
        while (head != null && k > 0) {
            head = head.next;
            k--;
        }
        first = head;
        while (first != null) {
            first = first.next;
            current = current.next;
        }
        return current;
    }

    public static void main(String[] args) {
        ListNodeUtils.printListNode(getKthFromEnd(ListNodeUtils.newListNode(1, 2, 3, 4, 5), 2));
        ListNodeUtils.printListNode(getKthFromEnd(ListNodeUtils.newListNode(1, 2, 3, 4, 5, 6), 3));
        ListNodeUtils.printListNode(getKthFromEnd(ListNodeUtils.newListNode(1), 1));
    }

}
