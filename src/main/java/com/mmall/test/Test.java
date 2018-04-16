package com.mmall.test;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 17:10
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class Test {
    public static void main(String[] args) {
        List<Student> list = Lists.newArrayList(
                new Student(1, "张三"),
                new Student(1, "李四"),
                new Student(2, "张三"),
                new Student(3, "张三"),
                new Student(3, "张三"),
                new Student(2, "张三")
        );

//        Set<Object> result = new HashSet<>();
//        result.addAll(list);
//        for (Object o : result) {
//            System.out.println(o.toString());
//        }

        System.out.println(".....................");
        Set<Student> result2 = new HashSet<>();
        result2.addAll(list);
        for (Student  s: result2) {
            System.out.println(s.toString());
        }
    }
}
