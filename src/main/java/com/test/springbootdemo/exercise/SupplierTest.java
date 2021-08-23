package com.test.springbootdemo.exercise;

import java.util.function.Supplier;

public class SupplierTest {
    public static void main(String[] args) {
        Supplier<String> runnable = String::new;
        System.out.println(runnable.get());
        Supplier<Emp> supplierNew = Emp::new;
        Emp emp = supplierNew.get();
        emp.setName("zhouli");
        System.out.println(emp.getName());

    }
}

class Emp {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
