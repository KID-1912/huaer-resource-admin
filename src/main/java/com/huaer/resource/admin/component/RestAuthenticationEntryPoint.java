package com.huaer.resource.admin.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.enums.StatusEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;


public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException)
    throws IOException, ServletException
    {
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(ResultResponse.error(StatusEnum.UNAUTHORIZED)));
        response.getWriter().flush();
    }
}
