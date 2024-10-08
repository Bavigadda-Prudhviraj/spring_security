package com.prudhviraj.security.security.filters;

import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.exceptions.ResourceNotFoundException;
import com.prudhviraj.security.security.repository.UserRepository;
import com.prudhviraj.security.security.service.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtServiceImpl jwtService;
    private final  UserRepository userRepository;
    @Autowired
    @Qualifier("handlerExceptionResolver")// This annotation injects a specific implementation of HandlerExceptionResolver into this field.
    private HandlerExceptionResolver handlerExceptionResolver;



    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String tokenFromHeader = request.getHeader("Authorization");
            // Log the incoming request and token status
            log.debug("Processing authentication for request: {}", request.getRequestURI());

            if (tokenFromHeader == null || !tokenFromHeader.startsWith("Bearer") || tokenFromHeader.isEmpty()) {
                log.warn("Authorization header is missing or invalid: {}", tokenFromHeader);
                filterChain.doFilter(request, response);
                return;
            }

            // Extract token after "Bearer"
            String token = tokenFromHeader.split("Bearer ")[1];
            log.debug("Extracted token: {}", token);

            // Get the user ID from the token
            Long userId = jwtService.getUserIdFromToken(token);
            log.debug("Extracted User ID from token: {}", userId);

            if (userId != null || SecurityContextHolder.getContext().getAuthentication() != null) {
                // Fetch the user from the repository
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("User not found with ID: {}", userId);
                            return new ResourceNotFoundException("No user account associated with the provided User Id: "
                                    + userId + ". Please check your UserId and try again.");
                        });

                log.info("User with ID: {} successfully authenticated", userId);

                // Create the authentication token and set the authentication in the context
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, null);
                // Set additional details like the IP address and session ID for the authentication token
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("Authentication token details set: IP = {}, Session ID = {}", request.getRemoteAddr(), request.getRequestedSessionId());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                log.debug("Authentication token set in SecurityContext for user: {}", user.getUsername());
            }
            // Pass the request and response to the next filter in the chain
            filterChain.doFilter(request, response);
            log.info("Request successfully passed through the filter chain.");
        } catch (Exception exception) {
            // This block catches any Exception that may occur during the request processing.
            // Use the injected handlerExceptionResolver to handle the caught exception.
            // This allows for centralized exception handling through the provided resolver.
            // It constructs a proper HTTP response for the exception.
            handlerExceptionResolver.resolveException(request,response,null,exception);
        }
    }


}

