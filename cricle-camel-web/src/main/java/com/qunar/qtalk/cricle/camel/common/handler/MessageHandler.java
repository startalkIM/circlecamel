package com.qunar.qtalk.cricle.camel.common.handler;

/**
 * Author : mingxing.shao
 * Date : 16-4-1
 */
public interface MessageHandler {

    void handle(String key, String msg);
}
