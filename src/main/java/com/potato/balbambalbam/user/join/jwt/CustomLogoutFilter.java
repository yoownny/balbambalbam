package com.potato.balbambalbam.user.join.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.exception.ExceptionDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper;

    public CustomLogoutFilter(JWTUtil jwtUtil,
                              RefreshRepository refreshRepository,
                              ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.objectMapper = objectMapper;
    }

    private String extractSocialIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return socialId;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // refresh
      /*  String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "ExpiredJwtException", "refresh 토큰이 만료되었습니다."); //401
            return;
        }

        String access = request.getHeader("access");
        String socialID = extractSocialIdFromToken(access);
        String refresh = refreshRepository.findRefreshBySocialId(socialID);

        if (refreshRepository.existsByRefresh(refresh)) {
            refreshRepository.deleteByRefresh(refresh);
        } else {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "InvalidRefreshToken", "refresh 토큰이 데이터베이스에 없습니다."); // 400
            return;
        }*/

        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().print("로그아웃이 완료되었습니다.");

        response.setStatus(HttpServletResponse.SC_OK); //200
    }

    private void sendError(HttpServletResponse response, int statusCode, String exceptionName, String message) throws IOException {
        ExceptionDto exceptionDto = new ExceptionDto(statusCode, exceptionName, message);
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(objectMapper.writeValueAsString(exceptionDto));
        writer.flush();
    }
}
