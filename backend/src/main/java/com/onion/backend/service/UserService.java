package com.onion.backend.service;

import com.onion.backend.controller.dto.UserCreateRequest;
import com.onion.backend.dto.SignUpUser;
import com.onion.backend.dto.WriteDeviceDto;
import com.onion.backend.entity.Device;
import com.onion.backend.entity.User;
import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User createUser(SignUpUser signUpUser) {
        User user = new User();
        user.setUsername(signUpUser.getUsername());
        user.setPassword(passwordEncoder.encode(signUpUser.getPassword()));
        user.setEmail(signUpUser.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<Device> getDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            return user.get().getDeviceList();
        } else {
            return new ArrayList<>();
        }
    }

    public Device addDevice(WriteDeviceDto writeDeviceDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            Device device = new Device();
            device.setDeviceName(writeDeviceDto.getDeviceName());
            device.setToken(writeDeviceDto.getToken());
            user.get().getDeviceList().add(device);
            userRepository.save(user.get());
            return device;
        }
        return null;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}