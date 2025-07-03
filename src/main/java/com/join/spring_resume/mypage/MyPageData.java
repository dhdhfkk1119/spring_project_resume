package com.join.spring_resume.mypage;

import java.util.List;

public class MyPageData {
    private List<MenuItem> menuList;
    private boolean loggedInUser;

    public MyPageData(List<MenuItem> menuList, boolean loggedInUser) {
        this.menuList = menuList;
        this.loggedInUser = loggedInUser;
    }

    public List<MenuItem> getMenuList() {
        return menuList;
    }

    public boolean isLoggedInUser() {
        return loggedInUser;
    }
}