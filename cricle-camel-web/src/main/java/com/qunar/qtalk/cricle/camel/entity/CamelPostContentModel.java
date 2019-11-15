package com.qunar.qtalk.cricle.camel.entity;

import java.util.List;

public class CamelPostContentModel {


    /**
     * content : 分享了一条链接!
     * exContent :
     * imgList : []
     * linkContent : {"auth":false,"desc":"【转发致敬！民警徒手夺刀救人：没时间给你去考虑个人安危[赞]】近日，广西壮族自治区南宁市秀灵村一铺面门前发生突发案件，一男子持凶器挟持一女子。危急时刻，南宁市公安局西乡塘分局副局长韦毅挺身而出，飞身徒手夺刀，迅速制服歹徒，解救了人质。最终，犯罪嫌疑人被当场抓获。http://t.cn/Ai9GwG7I \u200b\u200b\u200bhttps://weibointl.api.weibo.cn/share/74208837.html?weibo_id=4378847816059798","img":"http://t.cn/favicon.ico","linkurl":"http://t.cn/Ai9GwG7I","showas667":false,"showbar":true,"title":""}
     * type : 2
     */

    private String content;
    private String exContent;
    private LinkContentBean linkContent;
    private int type;
    private List<Object> imgList;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExContent() {
        return exContent;
    }

    public void setExContent(String exContent) {
        this.exContent = exContent;
    }

    public LinkContentBean getLinkContent() {
        return linkContent;
    }

    public void setLinkContent(LinkContentBean linkContent) {
        this.linkContent = linkContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<?> getImgList() {
        return imgList;
    }

    public void setImgList(List<Object> imgList) {
        this.imgList = imgList;
    }

    public static class LinkContentBean {
        /**
         * auth : false
         * desc : 【转发致敬！民警徒手夺刀救人：没时间给你去考虑个人安危[赞]】近日，广西壮族自治区南宁市秀灵村一铺面门前发生突发案件，一男子持凶器挟持一女子。危急时刻，南宁市公安局西乡塘分局副局长韦毅挺身而出，飞身徒手夺刀，迅速制服歹徒，解救了人质。最终，犯罪嫌疑人被当场抓获。http://t.cn/Ai9GwG7I ​​​https://weibointl.api.weibo.cn/share/74208837.html?weibo_id=4378847816059798
         * img : http://t.cn/favicon.ico
         * linkurl : http://t.cn/Ai9GwG7I
         * showas667 : false
         * showbar : true
         * title :
         */

        private boolean auth;
        private String desc;
        private String img;
        private String linkurl;
        private boolean showas667;
        private boolean showbar;
        private String title;

        public boolean isAuth() {
            return auth;
        }

        public void setAuth(boolean auth) {
            this.auth = auth;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }

        public boolean isShowas667() {
            return showas667;
        }

        public void setShowas667(boolean showas667) {
            this.showas667 = showas667;
        }

        public boolean isShowbar() {
            return showbar;
        }

        public void setShowbar(boolean showbar) {
            this.showbar = showbar;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}