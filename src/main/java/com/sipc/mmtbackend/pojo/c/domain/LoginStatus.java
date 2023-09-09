package com.sipc.mmtbackend.pojo.c.domain;

/**
 * 登录状态param
 */
public class LoginStatus {
    private String key;
    private String value;
    private String userAgent;
    private Integer organizationId;

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }



    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LoginStatus{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
