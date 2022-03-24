package com.test.mediaplayerdemo;

public class AudioBean {

    public static final int STOP = 1;
    public static final int START = 2;

    private String url; // 播放的url
    private int status; // 播放状态
    private boolean isNeed; // 这个状态表示需要处理，true， 需要处理，false 不需要处理

    public AudioBean() {
    }

    public AudioBean(String url, int status) {
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isNeed() {
        return isNeed;
    }

    public void setNeed(boolean need) {
        isNeed = need;
    }
}
