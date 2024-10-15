package com.prudhviraj.security.security.config;
import com.prudhviraj.security.security.filters.JwtAuthFilter;
import com.prudhviraj.security.security.handlers.OauthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.prudhviraj.security.security.entities.enums.Permissions.*;
import static com.prudhviraj.security.security.entities.enums.Role.ADMIN;
import static com.prudhviraj.security.security.entities.enums.Role.CREATOR;

@Configuration
// Enables Spring Security and allows configuration of security features for the application
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final OauthSuccessHandler oauthSuccessHandler;
    public WebSecurityConfig(JwtAuthFilter jwtAuthFilter, OauthSuccessHandler oauthSuccessHandler){
        this.jwtAuthFilter = jwtAuthFilter;
        this.oauthSuccessHandler = oauthSuccessHandler;
    }

    private static final String[] publicRoutes = {"/auth/**","/home.html", "/error"};

    /**
     * Configures the security filter chain for the application.
     *
     * This method defines the authorization rules for various HTTP requests,
     * including role-based and permission-based access control.
     *
     * @param httpSecurity The HttpSecurity object used for configuring security
     * @return A configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to specified routes
                        .requestMatchers(publicRoutes).permitAll()
                        // Allow public GET requests to posts
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        // Restrict POST requests to users with ADMIN or CREATOR roles
                        .requestMatchers(HttpMethod.POST, "/posts/**")
                            .hasAnyRole(ADMIN.name(), CREATOR.name())
                        // Restrict POST requests based on specific POST_CREATE authority
                        .requestMatchers(HttpMethod.POST, "/posts/**")
                            .hasAnyAuthority(POST_CREATE.name())
                        // Restrict GET requests based on specific POST_VIEW authority
                        .requestMatchers(HttpMethod.GET, "/posts/**")
                            .hasAnyAuthority(POST_VIEW.name())
                        // Restrict PUT requests based on specific POST_UPDATE authority
                        .requestMatchers(HttpMethod.PUT, "/posts/**")
                            .hasAnyAuthority(POST_UPDATE.name())
                        // Restrict DELETE requests based on specific POST_DELETE authority
                        .requestMatchers(HttpMethod.DELETE, "/posts/**")
                            .hasAnyAuthority(POST_DELETE.name())
                        // Require authentication for any other requests
                        .anyRequest().authenticated())
                // Disable CSRF protection (consider re-enabling for non-API applications)
                .csrf(csrfConfig -> csrfConfig.disable())
                // Configure session management to be stateless
                .sessionManagement(sessionConfig -> sessionConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add custom JWT authentication filter before the default username/password authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Configure OAuth2 login with a custom failure URL and success handler
                .oauth2Login(oauthConfig -> oauthConfig
                        .failureUrl("/login?error=true")
                        .successHandler(oauthSuccessHandler));

        // Returns the configured security filter chain used by Spring Security
        return httpSecurity.build();
    }


//    @Bean
//    UserDetailsService InMemoryUserDetails() {
//        // Creates an in-memory user with the username "user", password "password", and "USER" role
//        UserDetails user = User
//                .withUsername("user")
//                .password(passwordEncoder().encode("password"))  // Encodes the password using BCrypt
//                .roles("USER")
//                .build();
//
//        // Creates another in-memory user with the username "admin", password "admin", and "ADMIN" role
//        UserDetails adminUser = User
//                .withUsername("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        // Creates a third in-memory user with the username "guest", password "guest", and a custom "GUEST USER" role
//        UserDetails guestUser = User
//                .withUsername("guest")
//                .password(passwordEncoder().encode("guest"))
//                .roles("GUEST USER")
//                .build();
//
//        // Returns an InMemoryUserDetailsManager that holds the defined in-memory users
//        return new InMemoryUserDetailsManager(user, adminUser, guestUser);
//    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return  config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // Defines a BCryptPasswordEncoder bean, used to encode passwords securely
        return new BCryptPasswordEncoder();
    }
}
