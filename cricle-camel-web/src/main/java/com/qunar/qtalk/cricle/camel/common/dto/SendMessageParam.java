package com.qunar.qtalk.cricle.camel.common.dto;


import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SendMessageParam {
    /**
     * system : test
     * from : zhu886
     * fromhost : ejabhost1
     * to : [{"host":"ejabhost1","user":"sdsad"}]
     * extendinfo :
     * type : chat
     * msgtype : 1
     * content : xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     */
    private String system;
    private String from;
    private String fromhost;
    private List<ToEntity> to;
    private String extendinfo="";
    private String type;
    private String msgtype;
    private String content;
    private String auto_reply = "false";
    private String backupinfo = "";

    public void setBackupinfo(Object backupinfo) {
        this.backupinfo = JacksonUtils.obj2String(backupinfo);
    }

    @Getter
    @Setter
    public static class ToEntity {
        /**
         * host : ejabhost1
         * user : lffan.liu
         */
        private String host;
        private String user;
    }

}
