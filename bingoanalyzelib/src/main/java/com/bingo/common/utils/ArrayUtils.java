package com.bingo.common.utils;

import java.util.regex.Pattern;

public class ArrayUtils {

    public static <T> int indexOf(T[] arr, T v) {
        for (int i = 0; i < arr.length; i++) {
            if (v == null && arr[i] == null) {
                return i;
            } else if (v != null) {
                if (v.equals(arr[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    
    

    public static <T> String join(T[] array, String cement) {
        StringBuilder builder = new StringBuilder();

        if(array == null || array.length == 0) {
            return null;
        }

        for (T t : array) {
            builder.append(t).append(cement);
        }

        builder.delete(builder.length() - cement.length(), builder.length());

        return builder.toString();
    }
    
    
}
