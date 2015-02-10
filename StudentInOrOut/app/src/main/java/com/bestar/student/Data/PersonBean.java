package com.bestar.student.Data;

/**
 * Created by bestar on 2015/2/10.
 */
public class PersonBean {
    public static String tbName = "students";
    public static String tbCreateSql = "create table if not exists "+tbName+"" +
            "(_id Integer primary key autoincrement," +
            "ClassID varchar(100)," +
            "ID varchar(100)," +
            "OrgClassID text," +
            "OrgDepID text," +
            "RealName text," +
            "UserID text," +
            "PetName text," +
            "PortraitPath text," +
            "UserCode varchar(100)," +
            "UserDescribe text," +
            "UserSerialNum text)";
    Long ClassID;
    Long ID;
    Long OrgClassID;
    Long OrgDepID;
    String RealName;
    Long UserID;
    String PetName;
    String PortraitPath;
    String UserCode;
    String UserDescribe;
    String UserSerialNum;

    public Long getClassID() {
        return ClassID;
    }

    public void setClassID(Long classID) {
        ClassID = classID;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getOrgClassID() {
        return OrgClassID;
    }

    public void setOrgClassID(Long orgClassID) {
        OrgClassID = orgClassID;
    }

    public Long getOrgDepID() {
        return OrgDepID;
    }

    public void setOrgDepID(Long orgDepID) {
        OrgDepID = orgDepID;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public Long getUserID() {
        return UserID;
    }

    public void setUserID(Long userID) {
        UserID = userID;
    }

    public String getPetName() {
        return PetName;
    }

    public void setPetName(String petName) {
        PetName = petName;
    }

    public String getPortraitPath() {
        return PortraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        PortraitPath = portraitPath;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getUserDescribe() {
        return UserDescribe;
    }

    public void setUserDescribe(String userDescribe) {
        UserDescribe = userDescribe;
    }

    public String getUserSerialNum() {
        return UserSerialNum;
    }

    public void setUserSerialNum(String userSerialNum) {
        UserSerialNum = userSerialNum;
    }
}
