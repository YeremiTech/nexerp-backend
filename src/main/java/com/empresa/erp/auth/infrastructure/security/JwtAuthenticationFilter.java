package com.empresa.erp.auth.infrastructure.security;

import com.empresa.erp.auth.application.service.TokenRevocationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRevocationService tokenRevocationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtTokenProvider.extractUsername(token);
            String tokenId = jwtTokenProvider.extractTokenId(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtTokenProvider.isTokenValid(token, username)
                    && !tokenRevocationService.isRevoked(tokenId)) {
                List<String> authorities = jwtTokenProvider.extractAuthorities(token);
                var grantedAuthorities = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                var principal = new User(username, "", grantedAuthorities);
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException ex) {
            // Token invalido o expirado: se deja continuar sin autenticacion.
        } catch (Exception ex) {
            // Cualquier error del token debe resolverse como request no autenticado.
        }
        filterChain.doFilter(request, response);
    }
}
