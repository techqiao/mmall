package com.mmall.test;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 17:09
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private int id;
    private String name;
}
