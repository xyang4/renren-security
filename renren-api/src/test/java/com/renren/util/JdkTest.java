package com.renren.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JdkTest {
    @Test
    public void filterTest(){
        List<String> users = Arrays.asList("1","2","3");
        System.out.println(users.stream().filter(v -> !"2".equals(v)).collect(Collectors.toSet()));
    }
}
