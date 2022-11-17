package com.myclass.design.singleton;

/**
 * 懒汉单例模式-线程安全
 */
public class SingletonLazySafe implements Singleton{

    private static SingletonLazySafe instance;

    private SingletonLazySafe(){}

    public static synchronized SingletonLazySafe getInstance(){
        if (instance == null){
            instance = new SingletonLazySafe();
        }
        return instance;
    }

}
