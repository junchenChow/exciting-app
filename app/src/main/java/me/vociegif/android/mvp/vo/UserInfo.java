package me.vociegif.android.mvp.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserInfo implements Serializable {
    private int code;
    private String uid;
    private String id;
    private String name;
    private String contentid;
    private int type;
    private int order;
    private int page;
    private int taskid;
    private int topicid;
    private int shoptype;
    private int seriesid;
    private int stickerid;
    private String invitecode;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInvitecode() {
        return invitecode;
    }

    public void setInvitecode(String invitecode) {
        this.invitecode = invitecode;
    }

    public int getStickerid() {
        return stickerid;
    }

    public void setStickerid(int stickerid) {
        this.stickerid = stickerid;
    }

    public int getShoptype() {
        return shoptype;
    }

    public void setShoptype(int shoptype) {
        this.shoptype = shoptype;
    }

    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public int getTopicid() {
        return topicid;
    }

    public void setTopicid(int topicid) {
        this.topicid = topicid;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
