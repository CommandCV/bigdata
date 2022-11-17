package com.myclass.design.singleton;

/**
 * DCL单例模式
 *
 */
public class SingletonLazyDoubleCheck implements Singleton{

    private static volatile SingletonLazyDoubleCheck instance;

    private SingletonLazyDoubleCheck(){}

    public static SingletonLazyDoubleCheck getInstance(){
        if (instance == null){
            synchronized (SingletonLazyDoubleCheck.class){
                instance = new SingletonLazyDoubleCheck();
            }
        }
        return instance;
    }

}
