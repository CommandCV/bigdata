package com.myclass.algorithm.offer;

import com.myclass.common.entity.ListNode;
import com.myclass.common.utils.ListNodeUtils;

/**
 * 输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。
 *
 * 示例1：
 *
 * 输入：1->2->4, 1->3->4
 * 输出：1->1->2->3->4->4
 * 限制：
 *
 * 0 <= 链表长度 <= 1000
 *
 * 注意：本题与主站 21 题相同：https://leetcode-cn.com/problems/merge-two-sorted-lists/
 *
 * Related Topics
 * 递归
 * 链表
 *
 * 👍 299
 * 👎 0
 */
public class Solution25 {

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode node;
        if (l1 == null && l2 == null) {
            return null;
        } else if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        }
        if (l1.val < l2.val) {
            node = l1;
            node.next = mergeTwoLists(l1.next, l2);
        } else {
            node = l2;
            node.next = mergeTwoLists(l1, l2.next);
        }
        return node;
    }

    public static void main(String[] args) {
        ListNodeUtils.printListNode(mergeTwoLists(ListNodeUtils.newListNode(1, 2, 4), ListNodeUtils.newListNode(1, 3, 4)));
        ListNodeUtils.printListNode(mergeTwoLists(ListNodeUtils.newListNode(1, 2, 4), ListNodeUtils.newListNode(1)));
        ListNodeUtils.printListNode(mergeTwoLists(ListNodeUtils.newListNode(1, 2, 4), ListNodeUtils.newListNode()));
    }

}
