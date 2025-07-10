package com.join.spring_resume.board;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class BoardHtmlSanitizer {

    public static String sanitizeExceptImg(String html) {
        Safelist safelist = Safelist.none().addTags("img").addAttributes("img", "src", "alt", "title", "width", "height");
        return Jsoup.clean(html, safelist);
    }
}
