package com.kaarigar.backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class FirebaseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🔍 Guard: Checking request for: " + request.getRequestURI());

        String header = request.getHeader("Authorization");
        if (header == null) {
            System.out.println("✅ Guard: No token found, allowing request to proceed to permitAll checks...");
        }

        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                // Verify the token with Firebase Admin SDK
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

                // If verified, set the user in the Security Context
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        decodedToken.getUid(), null, new ArrayList<>());

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Token is invalid
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // This tells the filter: "If the request is for /api/auth, just skip the token check"
        return path.startsWith("/api/auth/");
    }
}