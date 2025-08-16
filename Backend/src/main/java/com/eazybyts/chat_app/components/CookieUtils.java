package com.eazybyts.chat_app.components;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


public class CookieUtils {

    public static void addHttpOnlyCookie(HttpServletResponse response,
                                         String name,
                                         String value,
                                         int maxAge,
                                         String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // For HTTPS only
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");  // Prevent CSRF

        response.addCookie(cookie);
    }
}