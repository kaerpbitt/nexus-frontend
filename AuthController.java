package com.nexus.platform.controller;

import com.nexus.platform.dto.*;
import com.nexus.platform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. เข้าสู่ระบบแบบปกติ
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. ลงทะเบียน + ส่งรหัส OTP ชั่วคราว
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            authService.verifyOtpAndCreateUser(request);
            return ResponseEntity.ok(Map.of("message", "User registered successfully with OTP verification"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. API ขอรหัส OTP ใหม่ (ปุ่ม Resend OTP พร้อมตัวนับถอยหลัง)
    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {
        String destination = body.get("destination");
        try {
            String newOtp = authService.generateAndSendOtp(destination);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully", "destination", destination));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
