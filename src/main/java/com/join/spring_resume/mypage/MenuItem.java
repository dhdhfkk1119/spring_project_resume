package com.join.spring_resume.mypage;

public class MenuItem {
    private String text;
    private String link;
    private boolean isNew;

    public MenuItem(String text, String link, boolean isNew) {
        this.text = text;
        this.link = link;
        this.isNew = isNew;
    }

    public String getText() { return text; }

    public String getLink() {
        return link;
    }

    public boolean isNew() {
        return isNew;
    }
}