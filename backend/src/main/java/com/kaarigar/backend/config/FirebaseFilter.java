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

        String header = request.getHeader("Authorization");

        // 🛡️ Concept: Bearer Token Pattern
        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                // 1. Verify the token with Firebase Admin SDK
                FirebaseToken decodedToken = FirebaseToken.class.cast(
                        com.google.firebase.auth.FirebaseAuth.getInstance().verifyIdToken(idToken)
                );

                // 2. Get the real UID from the token
                String uid = decodedToken.getUid();

                // 3. Tell Spring Security who this user is
                // We create an 'Authentication' object and put it in the "Context"
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        uid, null, new ArrayList<>()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // If token is expired or fake, the Guard stays closed
                System.err.println("❌ Auth Error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // This tells the filter: "If the request is for /api/auth, just skip the token check"
        return path.startsWith("/api/auth/") || path.startsWith("/api/profiles/worker/") || path.startsWith("/api/profiles/") ||
                path.startsWith("/api/jobs/") ||path.startsWith("/api/reviews/") ;
    }
}