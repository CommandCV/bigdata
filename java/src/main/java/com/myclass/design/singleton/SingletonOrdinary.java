package com.myclass.design.singleton;

/**
 * 饿汉式单例模式（最常见的单例模式）
 * 优点是简单，线程安全；缺点是类加载时就初始化浪费内存，并非懒加载。
 */
public class SingletonOrdinary implements Singleton{

    private static SingletonOrdinary instance = new SingletonOrdinary();

    private SingletonOrdinary() {
    }

    public static SingletonOrdinary getInstance(){
        return instance;
    }

}
