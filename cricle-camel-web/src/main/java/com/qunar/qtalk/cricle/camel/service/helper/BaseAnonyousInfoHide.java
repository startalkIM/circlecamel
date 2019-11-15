package com.qunar.qtalk.cricle.camel.service.helper;

public abstract class BaseAnonyousInfoHide<T> implements AnonymousInfoHide<T> {

    @Override
    public void doHideInfo(T t) {
        if (isNeedHide(t)) {
            hide(t);
        }
    }

}
