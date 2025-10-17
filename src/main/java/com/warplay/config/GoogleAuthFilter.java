package com.warplay.config;

import com.warplay.entity.User;
import com.warplay.repository.UserRepository;
import com.warplay.service.SessionTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class GoogleAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionTokenService sessionTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Validate JWT session token
            Optional<User> userOpt = sessionTokenService.validateSessionToken(token);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("sub", user.getGoogleId()); // Use Google ID as subject for consistency
                attributes.put("email", user.getEmail());
                attributes.put("name", user.getName());
                attributes.put("userId", user.getId()); // Add internal user ID for authorization
                
                // Create OAuth2User
                DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "sub"
                );
                
                // Create authentication token with OAuth2User
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        oauth2User, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
