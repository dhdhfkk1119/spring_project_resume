package com.join.spring_resume.mypage.controller;

import com.join.spring_resume.mypage.MenuItem;
import com.join.spring_resume.mypage.MyPageData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MyPageController {

    @GetMapping("/mypages")
    public String myPage(Model model) {

        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem("이력서 등록", "/resume/save-form", false));
        menuItems.add(new MenuItem("등록한 이력서 보기", "/resumes", false));
        menuItems.add(new MenuItem("자신이 작성한 게시판 관리", "/board/mine", false));
        menuItems.add(new MenuItem("나의 지원 현황", "/", false));
        menuItems.add(new MenuItem("찜한 게시판 목록", "/", false));
        menuItems.add(new MenuItem("찜한 구인 공고 목록", "/", false));
        menuItems.add(new MenuItem("댓글단 게시물 목록", "/", false));

        boolean isUserLoggedIn = false;

        MyPageData data = new MyPageData(menuItems, isUserLoggedIn);

        model.addAttribute("menuList", data.getMenuList());
        model.addAttribute("loggedInUser", data.isLoggedInUser());

        return "mypages";
    }

    @GetMapping("/comp/mypages")
    public String compMyPage(Model model) {

        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem("공고 등록하기", "/recruit/recruit-form", false));
        menuItems.add(new MenuItem("등록한 공고 목록보기", "/recruit/list", false));
        menuItems.add(new MenuItem("기업이 작성한 게시물", "/recruit/list", false));
        menuItems.add(new MenuItem("댓글이 달린 게시물", "/board/mine", false));
        menuItems.add(new MenuItem("댓글을 단 게시물", "/board/mine", false));

        boolean isUserLoggedIn = false;

        MyPageData data = new MyPageData(menuItems, isUserLoggedIn);

        model.addAttribute("menuList", data.getMenuList());
        model.addAttribute("loggedInUser", data.isLoggedInUser());

        return "mypages";
    }

}
