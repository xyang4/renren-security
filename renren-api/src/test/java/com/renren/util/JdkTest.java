package com.renren.util;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class JdkTest {
    @Test
    public void filterTest() {
        List<String> users = Arrays.asList("1", "2", "3");
        System.out.println(users.stream().filter(v -> !"2".equals(v)).collect(Collectors.toSet()));
    }

    @Test
    public void treeSortTest() {
//        Map<Integer, Integer> agentDepth = new TreeMap<>(Comparator.comparingInt(k -> k));
        Map<Integer, Integer> agentDepth = new HashMap<>();
        Map<Integer, Integer> map = new TreeMap<>();
        agentDepth.put(1, 2);
        agentDepth.put(3, 20);
        agentDepth.put(2, 200);
        agentDepth.put(5, 1);
        List<Integer> collect = agentDepth.entrySet().stream()
                .sorted((Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) -> o2.getValue() - o1.getValue())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void treeSortByValTest() {

    }
}
