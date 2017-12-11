package ru.bvn13.voidforum.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bvn13 on 11.12.2017.
 */
public class PaginatorUtil {

    public static List<Integer> createPagesList(Integer from, Integer to, Integer pageSize) {
        List<Integer> result = new ArrayList<>();
        Integer lastPage = (int) Math.ceil(to / pageSize);
        for (int i=from; i<=lastPage; i++) {
            result.add(i);
        }
        return result;
    }

    public static List<Integer> createPagesList(Integer from, Integer to) {
        List<Integer> result = new ArrayList<>();
        for (int i=from; i<=to; i++) {
            result.add(i);
        }
        return result;
    }

}
