package com.samvad.chat_app.configurations;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.IOException;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://chat-application-ui-black.vercel.app/") // Frontend origin
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true); // Allow cookies/auth
    }

//     This filter ensures SameSite=None; Secure for cookies
    @Configuration
    public static class SameSiteCookieFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            chain.doFilter(request, response);
            if (response instanceof HttpServletResponse res) {
                for (String header : res.getHeaders("Set-Cookie")) {
                    if (header.contains("JSESSIONID") && !header.contains("SameSite")) {
                        res.setHeader("Set-Cookie", header + "; SameSite=None; Secure");
                    }
                }
            }
        }
    }

    @Bean
    public CookieSameSiteSupplier applicationCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofNone().whenHasNameMatching("JSESSIONID");
    }

}
