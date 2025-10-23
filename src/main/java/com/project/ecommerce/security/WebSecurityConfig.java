package com.project.ecommerce.security;

import com.project.ecommerce.appuser.role.UserRole;
import com.project.ecommerce.appuser.role.UserRoleRepo;
import com.project.ecommerce.appuser.user.AppUser;
import com.project.ecommerce.appuser.user.AppUserRepo;
import com.project.ecommerce.security.jwt.AuthEntryPointJwt;
import com.project.ecommerce.security.jwt.AuthTokenFilter;
import com.project.ecommerce.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Set;

import static com.project.ecommerce.appuser.role.UserRole.Role.*;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                //.requestMatchers("/api/admin/**").permitAll()
                                //.requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/api/admin/**").permitAll()
                                .requestMatchers("/api/seller/**").permitAll()
                                //.requestMatchers("/api/user/cart/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }


    @Bean
    public CommandLineRunner initData(UserRoleRepo roleRepository, AppUserRepo userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            UserRole userRole = roleRepository.findByRoleName(ROLE_USER)
                    .orElseGet(() -> {
                        UserRole newUserRole = new UserRole(ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            UserRole sellerRole = roleRepository.findByRoleName(ROLE_SELLER)
                    .orElseGet(() -> {
                        UserRole newSellerRole = new UserRole(ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            UserRole adminRole = roleRepository.findByRoleName(ROLE_ADMIN)
                    .orElseGet(() -> {
                        UserRole newAdminRole = new UserRole(ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<UserRole> userRoles = Set.of(userRole);
            Set<UserRole> sellerRoles = Set.of(sellerRole);
            Set<UserRole> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                AppUser user1 = new AppUser("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                AppUser seller1 = new AppUser("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                AppUser admin = new AppUser("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles.stream().toList());
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles.stream().toList());
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles.stream().toList());
                userRepository.save(admin);
            });
        };
    }

}