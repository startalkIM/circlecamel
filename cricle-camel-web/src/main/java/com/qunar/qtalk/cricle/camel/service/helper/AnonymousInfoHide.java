package com.qunar.qtalk.cricle.camel.service.helper;

/**
 * 匿名信息隐藏
 */
public interface AnonymousInfoHide<T> {

    boolean isNeedHide(T t);

    void doHideInfo(T t);

    void hide(T t);
}
