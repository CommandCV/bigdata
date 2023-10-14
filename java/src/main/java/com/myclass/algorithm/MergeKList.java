package com.myclass.algorithm;

import com.myclass.common.entity.ListNode;
import com.myclass.common.utils.ListNodeUtils;
import com.myclass.common.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class MergeKList {
    public static ListNode mergeKLists(ArrayList<ListNode> lists) {
        ListNode head = null;
        if (lists.isEmpty()) {
            return null;
        }
        return mergeKLists(lists, 0, lists.size() - 1);
    }

    public static ListNode mergeKLists(ArrayList<ListNode> lists, int start, int end) {
        if (start > end) {
            return null;
        } else if (start == end) {
            return lists.get(start);
        } else {
            int mid = (end - start) / 2 + start;
            ListNode left = mergeKLists(lists, start, mid);
            ListNode right = mergeKLists(lists, mid + 1, end);
            return mergeList(left, right);
        }
    }

    public static ListNode mergeList(ListNode list1, ListNode list2) {
        if (list1 == null && list2 == null) {
            return null;
        } else if (list1 == null) {
            return list2;
        } else if (list2 == null) {
            return list1;
        }
        ListNode head = new ListNode(-1);
        ListNode result = head;
        while (list1 != null && list2 != null) {
            while (list1 != null && list1.val <= list2.val) {
                head.next = list1;
                head = list1;
                list1 = list1.next;
            }
            while (list1 != null && list2 != null && list1.val >= list2.val) {
                head.next = list2;
                head = list2;
                list2 = list2.next;
            }
        }
        if (list1 != null) {
            head.next = list1;
        } else {
            head.next = list2;
        }
        return result.next;
    }

    public static void main(String[] args) {
//        ArrayList<ListNode> list = ListUtils.newArrayLists(
//                ListNodeUtils.newListNode(1, 2, 3),
//                ListNodeUtils.newListNode(4, 5),
//                ListNodeUtils.newListNode(7));
//        ArrayList<ListNode> list = ListUtils.newArrayLists(
//                ListNodeUtils.newListNode(1, 2, 3),
//                ListNodeUtils.newListNode(4, 5, 6, 7));
        ArrayList<ListNode> list = ListUtils.newArrayLists(
                ListNodeUtils.newListNode(1, 2),
                ListNodeUtils.newListNode(1, 4, 5),
                ListNodeUtils.newListNode(6));
        ListNodeUtils.printListNode(mergeKLists(list));
    }

}
