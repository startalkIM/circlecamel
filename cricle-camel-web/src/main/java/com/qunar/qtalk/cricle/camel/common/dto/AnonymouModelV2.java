package com.qunar.qtalk.cricle.camel.common.dto;

import java.util.ArrayList;
import java.util.List;

public class AnonymouModelV2 {
    private List<Model> anonymous;

    public List<Model> getAnonymous() {
        return anonymous;
    }

    public AnonymouModelV2() {
        this.anonymous = new ArrayList<>();
    }

    public void setAnonymous(List<Model> anonymous) {
        this.anonymous = anonymous;
    }

    public static class Model {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
