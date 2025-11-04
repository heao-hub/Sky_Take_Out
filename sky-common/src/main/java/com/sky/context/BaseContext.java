package com.sky.context;

public class BaseContext {

    //ThreadLocal 是Thread的局部变量。为每个线程提供的那独一份存储空间，可以存储同一线程中需要的局部变量，
    // 例如此处可将在拦截器处解析出来的jwt令牌中的用户id存入，之后在需要使用id时可直接调用get方法。
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
