package com.bingo.common.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trung {

    public static ArrayList<String> getTrung2(boolean preventRemove, List<String> nums1, List<String> nums2) {
        ArrayList<String> numbers = new ArrayList<>();
        if (preventRemove)
        {
            numbers.addAll(nums1);
            numbers.addAll(nums2);
            return numbers;
        }
        else
        {
            String num2s = Arrays.toString(nums2.toArray());

            for (String num : nums1)
            {
                if (num2s.contains(num))
                {
                    numbers.add(num);
                }
            }

            return numbers;
        }
    }

    public static ArrayList<String> getTrung(boolean removeDuplicate, boolean preventRemove, List<String>... nums) {
        ArrayList<String> numbers = new ArrayList<>();

        int size = nums.length;
        if (size < 1)
        {
            return numbers;
        }

        if (size == 1)
        {
            if (removeDuplicate)
            {
                Set<String> removeDuplicateList = new HashSet<>();
                removeDuplicateList.addAll(nums[0]);
                return new ArrayList<>(removeDuplicateList);
            }
            else
            {
                return new ArrayList<>(nums[0]);
            }
        }

        for (int i = 0; i < size - 1; i++)
        {
            if (numbers.size() == 0)
            {
                numbers = getTrung2(preventRemove, nums[i], nums[i + 1]);
            }
            else
            {
                numbers = getTrung2(preventRemove, numbers, nums[i + 1]);
            }
        }

        if (removeDuplicate)
        {
            numbers = removeDuplicate(numbers);
        }

        return numbers;
    }

    private static ArrayList<String> removeDuplicate(ArrayList<String> resource) {
        Set<String> removeDuplicateList = new HashSet<>();
        removeDuplicateList.addAll(resource);
        resource.clear();
        resource.addAll(removeDuplicateList);
        removeDuplicateList.clear();
        return resource;
    }
}
