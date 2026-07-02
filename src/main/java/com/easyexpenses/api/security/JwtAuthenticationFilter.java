package com.easyexpenses.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;


    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwt = authHeader.substring(7);

        try {
            String subject = jwtService.extractSubject(jwt);


            if (subject != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {


                UserDetails userDetails =
                        customUserDetailsService.loadUserById(subject);


                if (jwtService.isValid(jwt, (CustomUserDetails) userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }


            filterChain.doFilter(request, response);
        }catch (AuthenticationException ex){
            authenticationEntryPoint.commence(
                    request,
                    response,
                    ex
            );
        }
    }
}
