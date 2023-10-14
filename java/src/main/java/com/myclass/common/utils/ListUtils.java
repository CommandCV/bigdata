package com.myclass.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class ListUtils {

    public static <T> ArrayList<T> newArrayLists(T... elements) {
        ArrayList<T> list = new ArrayList<>(elements.length);
        list.addAll(Arrays.asList(elements));
        return list;
    }

    public static <T> LinkedList<T> newLinkedList(T... elements) {
        return new LinkedList<T>(Arrays.asList(elements));
    }

}
