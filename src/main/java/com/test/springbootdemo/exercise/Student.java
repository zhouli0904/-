package com.test.springbootdemo.exercise;

public class Student extends Person{

    String school;

//    public Student(String name) {
//        super(name);
//    }


    public Student(String name, String school) {
        super(name);
        this.school = school;
    }

    public Student() {
    }


    public static void main(String[] args) {
        Student student = new Student();
    }
}
