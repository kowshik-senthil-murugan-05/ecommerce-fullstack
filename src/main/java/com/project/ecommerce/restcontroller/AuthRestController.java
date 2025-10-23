package com.project.ecommerce.restcontroller;

import com.project.ecommerce.appuser.role.UserRole;
import com.project.ecommerce.appuser.role.UserRoleRepo;
import com.project.ecommerce.appuser.user.AppUser;
import com.project.ecommerce.appuser.user.AppUserRepo;
import com.project.ecommerce.security.jwt.JwtUtils;
import com.project.ecommerce.security.request.LoginRequest;
import com.project.ecommerce.security.request.SignupRequest;
import com.project.ecommerce.security.response.MessageResponse;
import com.project.ecommerce.security.response.UserInfoResponse;
import com.project.ecommerce.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final AppUserRepo userRepository;
    private final UserRoleRepo roleRepository;
    private final PasswordEncoder encoder;

    public AuthRestController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, AppUserRepo appUserRepo,
                              UserRoleRepo userRoleRepo, PasswordEncoder passwordEncoder)
    {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = appUserRepo;
        this.roleRepository = userRoleRepo;
        this.encoder = passwordEncoder;
    }

    //Sign in
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generate JWT cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // Collect roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Return user info + set cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(
                        userDetails.getId(),
                        userDetails.getUsername(),
                        roles,
                        jwtCookie.getValue()
                ));
    }

    // ✅ Sign up
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user
        AppUser user = new AppUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        // Save first to generate ID
        user = userRepository.save(user);

        // Assign roles
        Set<String> strRoles = signUpRequest.getRole();
        Set<UserRole> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            UserRole userRole = roleRepository.findByRoleName(UserRole.Role.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            for (String role : strRoles) {
                UserRole.Role enumRole;
                switch (role.toLowerCase()) {
                    case "admin" -> enumRole = UserRole.Role.ROLE_ADMIN;
                    case "seller" -> enumRole = UserRole.Role.ROLE_SELLER;
                    default -> enumRole = UserRole.Role.ROLE_USER;
                }
                UserRole userRole = roleRepository.findByRoleName(enumRole)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            }
        }

        user.setRoles(new ArrayList<>(roles));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    // ✅ Get current logged-in user info
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Unauthorized"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles,
                userDetails.getEmail()

        ));
    }

    // ✅ Sign out
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}



////package com.ecommerce.app.restcontroller;
////
////import com.ecommerce.app.appuser.role.UserRole;
////import com.ecommerce.app.appuser.role.UserRoleRepo;
////import com.ecommerce.app.appuser.user.AppUser;
////import com.ecommerce.app.appuser.user.AppUserRepo;
////import com.ecommerce.app.security.jwt.JwtUtils;
////import com.ecommerce.app.security.request.LoginRequest;
////import com.ecommerce.app.security.request.SignupRequest;
////import com.ecommerce.app.security.response.MessageResponse;
////import com.ecommerce.app.security.response.UserInfoResponse;
////import com.ecommerce.app.security.services.UserDetailsImpl;
////import jakarta.validation.Valid;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.HttpHeaders;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseCookie;
////import org.springframework.http.ResponseEntity;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.GrantedAuthority;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.*;
////import java.util.stream.Collectors;
////
////@RestController
////@RequestMapping("/api/auth")
////public class AuthRestController {
////
////    @Autowired
////    private JwtUtils jwtUtils;
////
////    @Autowired
////    private AuthenticationManager authenticationManager;
////
////    @Autowired
////    private AppUserRepo userRepository;
////
////    @Autowired
////    private UserRoleRepo roleRepository;
////
////    @Autowired
////    private PasswordEncoder encoder;
////
////    @PostMapping("/signin")
////    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
////        Authentication authentication = authenticationManager
////                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
////
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////
////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
////
////        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
////
////        return ResponseEntity.ok()
////                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())   // ✅ real Set-Cookie header
////                .body(new UserInfoResponse(
////                        userDetails.getId(),
////                        userDetails.getUsername(),
////                        userDetails.getEmail(),
////                        userDetails.getAuthorities().stream()
////                                .map(GrantedAuthority::getAuthority)
////                                .toList()
////                ));
////    }
////
////
//////    @PostMapping("/signin")
//////    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//////        Authentication authentication;
//////        try {
//////            authentication = authenticationManager.authenticate(
//////                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//////        } catch (AuthenticationException exception) {
//////            Map<String, Object> map = new HashMap<>();
//////            map.put("message", "Bad credentials");
//////            map.put("status", false);
//////            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
//////        }
//////
//////        SecurityContextHolder.getContext().setAuthentication(authentication);
//////
//////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//////
//////        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
//////
//////        List<String> roles = userDetails.getAuthorities().stream()
//////                .map(item -> item.getAuthority())
//////                .collect(Collectors.toList());
//////
//////        UserInfoResponse response = new UserInfoResponse(
//////                userDetails.getId(),
//////                userDetails.getUsername(),
//////                roles,
//////                jwtCookie.toString()
//////        );
//////
//////        return ResponseEntity.ok()
//////                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
//////                .body(response);
//////    }
////
////    @PostMapping("/signup")
////    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
////        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
////            return ResponseEntity.badRequest()
////                    .body(new MessageResponse("Error: Username is already taken!"));
////        }
////
////        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
////            return ResponseEntity.badRequest()
////                    .body(new MessageResponse("Error: Email is already in use!"));
////        }
////
////        // Create new user's account
////        AppUser user = new AppUser(
////                signUpRequest.getUsername(),
////                signUpRequest.getEmail(),
////                encoder.encode(signUpRequest.getPassword())
////        );
////
////        // Save user first to generate id
////        user = userRepository.save(user);
////
////        // Assign roles
////        Set<String> strRoles = signUpRequest.getRole();
////        Set<UserRole> roles = new HashSet<>();
////
////        if (strRoles == null || strRoles.isEmpty()) {
////            UserRole userRole = roleRepository.findByRoleName(UserRole.Role.ROLE_USER)
////                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
////            roles.add(userRole);
////        } else {
////            for (String role : strRoles) {
////                UserRole.Role enumRole;
////                switch (role.toLowerCase()) {
////                    case "admin":
////                        enumRole = UserRole.Role.ROLE_ADMIN;
////                        break;
////                    case "seller":
////                        enumRole = UserRole.Role.ROLE_SELLER;
////                        break;
////                    default:
////                        enumRole = UserRole.Role.ROLE_USER;
////                }
////                UserRole userRole = roleRepository.findByRoleName(enumRole)
////                        .orElseThrow(() -> new RuntimeException("Error: Role not found."));
////                roles.add(userRole);
////            }
////        }
////
////        user.setRoles(new ArrayList<>(roles));
////        userRepository.save(user);
////
////        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
////    }
////
////    @GetMapping("/username")
////    public String currentUserName(Authentication authentication) {
////        if (authentication != null) {
////            return authentication.getName();
////        }
////        return "";
////    }
////
////    @GetMapping("/user")
////    public ResponseEntity<?> getUserDetails(Authentication authentication) {
////        if (authentication == null) {
////            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
////                    .body(new MessageResponse("Error: Unauthorized"));
////        }
////
////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
////
////        List<String> roles = userDetails.getAuthorities().stream()
////                .map(item -> item.getAuthority())
////                .collect(Collectors.toList());
////
////        UserInfoResponse response = new UserInfoResponse(
////                userDetails.getId(),
////                userDetails.getUsername(),
////                roles
////        );
////
////        return ResponseEntity.ok(response);
////    }
////
////    @PostMapping("/signout")
////    public ResponseEntity<?> signoutUser() {
////        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
////        return ResponseEntity.ok()
////                .header(HttpHeaders.SET_COOKIE, cookie.toString())
////                .body(new MessageResponse("You've been signed out!"));
////    }
////}
////
////
////
////
////
////
////
////
////
////
////
////
//////package com.ecommerce.app.restcontroller;
//////
//////import com.ecommerce.app.appuser.role.UserRole;
//////import com.ecommerce.app.appuser.role.UserRoleRepo;
//////import com.ecommerce.app.appuser.user.AppUser;
//////import com.ecommerce.app.appuser.user.AppUserRepo;
//////import com.ecommerce.app.security.jwt.JwtUtils;
//////import com.ecommerce.app.security.request.LoginRequest;
//////import com.ecommerce.app.security.request.SignupRequest;
//////import com.ecommerce.app.security.response.MessageResponse;
//////import com.ecommerce.app.security.response.UserInfoResponse;
//////import com.ecommerce.app.security.services.UserDetailsImpl;
//////import jakarta.validation.Valid;
//////import org.springframework.beans.factory.annotation.Autowired;
//////import org.springframework.http.HttpHeaders;
//////import org.springframework.http.HttpStatus;
//////import org.springframework.http.ResponseCookie;
//////import org.springframework.http.ResponseEntity;
//////import org.springframework.security.authentication.AuthenticationManager;
//////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//////import org.springframework.security.core.Authentication;
//////import org.springframework.security.core.AuthenticationException;
//////import org.springframework.security.core.context.SecurityContextHolder;
//////import org.springframework.security.crypto.password.PasswordEncoder;
//////import org.springframework.web.bind.annotation.*;
//////
//////import java.util.*;
//////import java.util.stream.Collectors;
//////
//////@RestController
//////@RequestMapping("/api/auth")
//////public class AuthRestController {
//////
//////    @Autowired
//////    private JwtUtils jwtUtils;
//////
//////    @Autowired
//////    private AuthenticationManager authenticationManager;
//////
//////    @Autowired
//////    private AppUserRepo userRepository;
//////
//////    @Autowired
//////    private UserRoleRepo roleRepository;
//////
//////    @Autowired
//////    private PasswordEncoder encoder;
//////
//////    @PostMapping("/signin")
//////    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
//////        Authentication authentication;
//////        try {
//////            authentication = authenticationManager
//////                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//////        } catch (AuthenticationException exception) {
//////            Map<String, Object> map = new HashMap<>();
//////            map.put("message", "Bad credentials");
//////            map.put("status", false);
//////            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
//////        }
//////
//////        SecurityContextHolder.getContext().setAuthentication(authentication);
//////
//////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//////
//////        ResponseCookie jwtCookie = jwtUtils..generateJwtCookie(userDetails);
//////
//////        List<String> roles = userDetails.getAuthorities().stream()
//////                .map(item -> item.getAuthority())
//////                .collect(Collectors.toList());
//////
//////        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
//////                userDetails.getUsername(), roles, jwtCookie.toString());
//////
//////        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
//////                jwtCookie.toString())
//////                .body(response);
//////    }
//////
//////    @PostMapping("/signup")
//////    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
////////        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
////////            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
////////        }
//////
//////        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//////            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
//////        }
//////
//////        // Create new user's account
//////        AppUser user = new AppUser(signUpRequest.getUsername(),
//////                signUpRequest.getEmail(),
//////                encoder.encode(signUpRequest.getPassword()));
//////
//////        // Save user first
//////        user = userRepository.save(user);
//////
//////        // Assign roles
//////        Set<String> strRoles = signUpRequest.getRole();
//////        Set<UserRole> roles = new HashSet<>();
//////
//////        if (strRoles == null) {
//////            roles.add(roleRepository.findByRoleName(UserRole.Role.ROLE_USER)
//////                    .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
//////        } else {
//////            strRoles.forEach(role -> {
//////                UserRole.Role enumRole = switch (role) {
//////                    case "admin" -> UserRole.Role.ROLE_ADMIN;
//////                    case "seller" -> UserRole.Role.ROLE_SELLER;
//////                    default -> UserRole.Role.ROLE_USER;
//////                };
//////                roles.add(roleRepository.findByRoleName(enumRole)
//////                        .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
//////            });
//////        }
//////
//////        user.setRoles(roles.stream().toList());
//////        userRepository.save(user);
//////
//////
//////        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//////    }
//////
//////    @GetMapping("/username")
//////    public String currentUserName(Authentication authentication){
//////        if (authentication != null)
//////            return authentication.getName();
//////        else
//////            return "";
//////    }
//////
//////
//////    @GetMapping("/user")
//////    public ResponseEntity<?> getUserDetails(Authentication authentication){
//////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//////
//////        List<String> roles = userDetails.getAuthorities().stream()
//////                .map(item -> item.getAuthority())
//////                .collect(Collectors.toList());
//////
//////        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
//////                userDetails.getUsername(), roles);
//////
//////        return ResponseEntity.ok().body(response);
//////    }
//////
//////    @PostMapping("/signout")
//////    public ResponseEntity<?> signoutUser(){
//////        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
//////        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
//////                        cookie.toString())
//////                .body(new MessageResponse("You've been signed out!"));
//////    }
//////}
