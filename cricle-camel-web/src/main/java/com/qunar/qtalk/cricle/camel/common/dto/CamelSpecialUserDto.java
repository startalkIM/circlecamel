package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CamelSpecialUserDto {

    List<OtherconfigEntity> data;

    public static class OtherconfigEntity {
        private String userName;
        private String userID;
        private String dep;

        public String getUserName() {
            return userName;
        }

        public String getUserID() {
            return userID;
        }

        public String getDep() {
            return dep;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public void setDep(String dep) {
            this.dep = dep;
        }
    }
}
