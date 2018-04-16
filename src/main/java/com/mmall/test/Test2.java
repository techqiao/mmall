package com.mmall.test;

import java.util.HashSet;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 17:21
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class Test2 {
    public static void main(String[] args) {
        HashSet<Person> persons=new HashSet<Person>();
        persons.add(new Person("jingtian1",21));
        persons.add(new Person("jingtian2",22));
        persons.add(new Person("jingtian3",23));
        persons.add(new Person("jingtian3",24));
        persons.add(new Person("jingtian2",22));
        for(Person person:persons)
            System.out.println(person.getName()+"--------------"+person.getAge());
    }
}

class Person{
    private String name;
    private int age;
    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        Person person=(Person)obj;
        return this.name.equals(person.getName());
    }

}
