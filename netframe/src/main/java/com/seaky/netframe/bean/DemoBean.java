package com.seaky.netframe.bean;

public class DemoBean {

    private boolean isOriginal;
    private String author;

    public DemoBean(){}

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
