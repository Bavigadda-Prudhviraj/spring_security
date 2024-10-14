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

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicRoutes).permitAll()
                        .requestMatchers(HttpMethod.GET,"/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/posts/**").hasAnyRole(ADMIN.name(), CREATOR.name())
                        .anyRequest()
                        .authenticated())
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauthConfig-> oauthConfig
                        // Configure OAuth2 login with custom failure URL and success handler
                        .failureUrl("/login?error=true")
                        .successHandler(oauthSuccessHandler));

                // Enables form-based login (standard login form will be presented for authentication)
        //        .formLogin(Customizer.withDefaults());

        // Returns the configured security filter chain, which is used by Spring Security
        return  httpSecurity.build();
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
