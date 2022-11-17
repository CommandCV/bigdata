package com.myclass.design.singleton;

/**
 * 懒汉单例模式-线程不安全
 */
public class SingletonLazyUnsafe implements Singleton{

    private static SingletonLazyUnsafe instance;

    private SingletonLazyUnsafe(){}

    public static SingletonLazyUnsafe getInstance(){
        if (instance == null){
            instance = new SingletonLazyUnsafe();
        }
        return instance;
    }

}
