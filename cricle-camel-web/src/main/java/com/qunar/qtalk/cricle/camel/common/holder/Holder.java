package com.qunar.qtalk.cricle.camel.common.holder;

public class Holder<T> {

    private T t;

    public void set(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }
}
