package com.onion.backend.controller;

import com.onion.backend.dto.WriteDeviceDto;
import com.onion.backend.entity.Device;
import com.onion.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final UserService userService;

    @Autowired
    public DeviceController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<Device>> getDevices() {
        return ResponseEntity.ok(userService.getDevices());
    }

    @PostMapping("")
    public ResponseEntity<Device> addDevice(@RequestBody WriteDeviceDto writeDeviceDto) {
        return ResponseEntity.ok(userService.addDevice(writeDeviceDto));
    }
}