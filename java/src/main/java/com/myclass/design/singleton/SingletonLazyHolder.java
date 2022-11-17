package com.myclass.design.singleton;

/**
 * 登记法/静态内部类单例模式
 * 与double-check的功效一致。此方式利用了静态内部类的加载机制达到懒加载，只有显式调用getInstance时才会装在静态内部类
 */
public class SingletonLazyHolder implements Singleton {

    private SingletonLazyHolder(){}

    private static class InnerSingletonHolder{
        private static final SingletonLazyHolder instance = new SingletonLazyHolder();
    }

    public static SingletonLazyHolder getInstance(){
        return InnerSingletonHolder.instance;
    }

}
