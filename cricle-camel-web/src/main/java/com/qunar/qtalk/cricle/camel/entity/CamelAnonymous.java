package com.qunar.qtalk.cricle.camel.entity;

public class CamelAnonymous {
    /**
     * null
     */
    private Integer id;

    /**
     * 匿名名称
     */
    private String anonymous;

    /**
     * 匿名头像
     */
    private String anonymousPhoto;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getAnonymousPhoto() {
        return anonymousPhoto;
    }

    public void setAnonymousPhoto(String anonymousPhoto) {
        this.anonymousPhoto = anonymousPhoto;
    }
}