package com.bestar.student.Data;

/**
 * Created by bestar on 2015/2/10.
 */
public class FamilyBean {
    public static String tbName = "familys";
    public static String tbCreateSql = "create table if not exists "+tbName+"" +
            "(_id Integer primary key autoincrement," +
            "ID varchar(100)," +
            "SchoolPersonnelID text," +
            "ContactTitle text," +
            "ContactName text," +
            "ContactTel text," +
            "IDCard text," +
            "PortraitPath text)";
    Long ID;
    Long SchoolPersonnelID;
    String ContactTitle;
    String ContactName;
    String ContactTel;
    String IDCard;
    String PortraitPath;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getSchoolPersonnelID() {
        return SchoolPersonnelID;
    }

    public void setSchoolPersonnelID(Long schoolPersonnelID) {
        SchoolPersonnelID = schoolPersonnelID;
    }

    public String getContactTitle() {
        return ContactTitle;
    }

    public void setContactTitle(String contactTitle) {
        ContactTitle = contactTitle;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactTel() {
        return ContactTel;
    }

    public void setContactTel(String contactTel) {
        ContactTel = contactTel;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getPortraitPath() {
        return PortraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        PortraitPath = portraitPath;
    }
}
