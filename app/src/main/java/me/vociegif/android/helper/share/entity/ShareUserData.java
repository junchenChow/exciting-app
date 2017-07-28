package me.vociegif.android.helper.share.entity;

/**
 * Created by design on 2016/3/10.
 */
public class ShareUserData {
    public String nickname = "";
    public int gender = 0;
    public String head = "";
    public String sign = "";
    public int pf = 0;
    public String uid = "";
    public String city = "";
    public String province = "";
    public String country = "";

    @Override
    public String toString() {
        return "ShareUserData{" +
                "nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", head='" + head + '\'' +
                ", sign='" + sign + '\'' +
                ", pf=" + pf +
                ", uid='" + uid + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
