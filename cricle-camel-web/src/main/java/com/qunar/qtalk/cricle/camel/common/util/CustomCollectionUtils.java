package com.qunar.qtalk.cricle.camel.common.util;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by haoling.wang on 2019/2/28.
 */
public class CustomCollectionUtils {

    /**
     * 取两集合交集
     *
     * @param s1
     * @param s2
     * @param <T>
     * @return
     */
    public static <T> Set<T> intersection(Set<T> s1, Set<T> s2) {
        HashSet<T> res = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(s1) || CollectionUtils.isNotEmpty(s2)) {
            if (CollectionUtils.isEmpty(s1)) {
                res.addAll(s2);
            } else if (CollectionUtils.isEmpty(s2)) {
                res.addAll(s1);
            } else {
                Set<T> intersection = Sets.intersection(s1, s2);
                res.addAll(intersection);
            }
        }
        return res;
    }

}
