package com.example.forms.controller;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.forms.entity.User;
import com.example.forms.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user) {
		if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null || user.getRole() == null) {
			return ResponseEntity.badRequest().body("Missing required fields");
		}

		// Generate OTP
		String otp = generateOTP();
		user.setOtp(otp);
		user.setVerified(false);

		try {
			// Save user to the database
			userRepository.save(user);

			// Send OTP email
			sendOTPEmail(user.getUsername(), user.getEmail(), otp);

			return ResponseEntity.ok("User registered successfully. Please check your email for OTP verification.");
		} catch (Exception e) {
			// Handle database or email sending errors
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user. Please try again later.");
		}
	}

	// Method to generate OTP
	private String generateOTP() {
		Random random = new Random();
		int otpLength = 6;
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < otpLength; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	// Method to send OTP email
	private void sendOTPEmail(String username, String email, String otp) {
		try {
			String websiteName = "CodeNest";

			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(email);
			mailMessage.setSubject("OTP Verification - " + websiteName);
			mailMessage.setText("Dear " + username + ",\n\n"
					+ "Thank you for registering on " + websiteName + ". Your OTP for registration is: " + otp + "\n\n"
					+ "Regards,\n"
					+ websiteName + " Team");
			javaMailSender.send(mailMessage);
		} catch (Exception e) {
			// Handle email sending errors
			// Log the error
			e.printStackTrace();
		}
	}

	// Endpoint to verify OTP
	@GetMapping("/verify")
	public ResponseEntity<String> verifyUser(@RequestParam String email, @RequestParam String otp) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.badRequest().body("User not found");
		}

		if (otp.equals(user.getOtp())) {
			// OTP verification successful
			// Mark user as verified
			user.setVerified(true);
			user.setOtp(null); // Clear OTP after verification
			userRepository.save(user);
			return ResponseEntity.ok("User verified successfully.");
		} else {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
		User user = userRepository.findByEmail(loginUser.getEmail());
		if (user != null && user.getPassword().equals(loginUser.getPassword())) {
			if (user.isVerified()) {
				return ResponseEntity.ok("Login successful");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is not verified. Please verify your account.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logoutUser() {
		return ResponseEntity.ok("Logout successful");
	}

	@GetMapping("/login")
	public ResponseEntity<User> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String username = authentication.getName();

		User user = userRepository.findByUsername(username);

		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}


	@GetMapping("/user-details")
	public ResponseEntity<User> getUserDetailsByEmail(@RequestParam String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
