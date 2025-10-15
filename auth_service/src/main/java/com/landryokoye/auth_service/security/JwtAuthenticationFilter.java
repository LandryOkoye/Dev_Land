package com.landryokoye.auth_service.security;

import com.landryokoye.auth_service.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/auth/register");  // don't filter this endpoint
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String usernameOrEmail = null;

        if(header != null && header.startsWith("Bearer ")){
            token = header.substring(7);


            try {
                usernameOrEmail = jwtService.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unable to get details from the token b/cos --  " + e.getCause());
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your session has expired try logging again" + e.getCause());
                throw new IllegalStateException("Expired Jwt Token");
            } catch (UnsupportedJwtException e) {
//                System.out.println("JWT Token is unsupported for local Auth");
                logger.debug("Unsupported Jwt Token");
                throw new IllegalStateException("Unsupported Jwt Token");
            } catch (MalformedJwtException e) {
                System.out.println("Invalid JWT Token");
                throw new IllegalStateException("Invalid Jwt Token(Malformed)");
            } catch (SignatureException e) {
                System.out.println("Invalid JWT Signature");
                throw new IllegalStateException("Invalid Jwt Token Signature");
            }

            if (usernameOrEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameOrEmail);
                if (jwtService.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        filterChain.doFilter(request, response);


    }
}
