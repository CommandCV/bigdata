package com.myclass.common.utils;

import com.myclass.common.entity.ListNode;

public class ListNodeUtils {

    public static ListNode newListNode(int... values) {
        ListNode head = new ListNode(-1, null);
        ListNode result = head;
        for (int value : values) {
            head.next = new ListNode(value, null);
            head = head.next;
        }
        return result.next;
    }

    public static void printListNode(ListNode listNode) {
        while (listNode != null) {
            System.out.print(listNode.val);
            if (listNode.next != null) {
                System.out.print(", ");
            }
            listNode = listNode.next;
        }
        System.out.println();
    }

}
