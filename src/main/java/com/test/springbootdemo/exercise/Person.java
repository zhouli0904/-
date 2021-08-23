package com.test.springbootdemo.exercise;

public class Person {
    String name;
    int age;

    public Person() {
        System.out.println("person 类的空参构造器");
    }


    public Person(String name) {
        this.name = name;
    }
}
