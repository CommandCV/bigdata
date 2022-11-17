package com.myclass.design.singleton;

public class SingletonMain {

    public static void test(Singleton instance1, Singleton instance2){
        System.out.println(instance1 == instance2);
    }

    public static void main(String[] args) {
        // 饿汉模式
        test(SingletonOrdinary.getInstance(), SingletonOrdinary.getInstance());
        // 懒汉模式-线程不安全
        test(SingletonLazyUnsafe.getInstance(), SingletonLazyUnsafe.getInstance());
        // 懒汉模式-线程安全
        test(SingletonLazySafe.getInstance(), SingletonLazySafe.getInstance());
        // 双检锁模式
        test(SingletonLazyDoubleCheck.getInstance(), SingletonLazyDoubleCheck.getInstance());
        // 登记法/静态内部类
        test(SingletonLazyHolder.getInstance(), SingletonLazyHolder.getInstance());
    }
}
