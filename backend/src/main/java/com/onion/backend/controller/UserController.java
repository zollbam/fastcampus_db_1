package com.onion.backend.controller;

import com.onion.backend.controller.dto.TokenRequest;
import com.onion.backend.controller.dto.UserCreateRequest;
import com.onion.backend.dto.SignUpUser;
import com.onion.backend.entity.User;
import com.onion.backend.entity.UserNotificationHistory;
import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.service.CustomUserDetailsService;
import com.onion.backend.service.JwtBlacklistService;
import com.onion.backend.service.UserNotificationHistoryService;
import com.onion.backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final JwtBlacklistService jwtBlacklistService;

    private final UserNotificationHistoryService userNotificationHistoryService;

    @Autowired
    public UserController(UserService userService,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService,
                          JwtBlacklistService jwtBlacklistService,
                          UserNotificationHistoryService userNotificationHistoryService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtBlacklistService = jwtBlacklistService;
        this.userNotificationHistoryService = userNotificationHistoryService;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> createUser(@RequestBody SignUpUser signUpUser) {
        User user = userService.createUser(signUpUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws AuthenticationException {
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String token = jwtUtil.generateToken(userDetails.getUsername());
        Cookie cookie = new Cookie("onion_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        return token;
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("onion_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제
        response.addCookie(cookie);
    }

    @PostMapping("/logout/all")
    public void logout(@RequestParam(required = false) String requestToken, @CookieValue(value = "onion_token", required = false) String cookieToken, HttpServletRequest request, HttpServletResponse response) {
        String token = null;
        String bearerToken = request.getHeader("Authorization");
        if (requestToken != null) {
            token = requestToken;
        } else if (cookieToken != null) {
            token = cookieToken;
        } else if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }
        Instant instant = new Date().toInstant();
        LocalDateTime expirationTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        String username = jwtUtil.getUsernameFromToken(token);
        jwtBlacklistService.blacklistToken(token, expirationTime, username);
        Cookie cookie = new Cookie("onion_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제
        response.addCookie(cookie);
    }

    @PostMapping("/token/validation")
    public ResponseEntity<String> jwtValidate(@RequestBody TokenRequest tokenRequest) {
        if (jwtUtil.validateToken(tokenRequest.getToken())) {
            return ResponseEntity.ok("유효한 토큰입니다.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }
    }

    @PostMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    public void readHistory(@RequestParam String historyId) {
        userNotificationHistoryService.readNotification(historyId);
    }

    @GetMapping("/history")
    public ResponseEntity<List<UserNotificationHistory>> getHistoryList() {
        return ResponseEntity.ok(userNotificationHistoryService.getNotificationList());
    }
}

