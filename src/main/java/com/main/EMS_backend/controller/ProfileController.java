package com.main.EMS_backend.controller;

import com.main.EMS_backend.dto.ChangePasswordRequest;
import com.main.EMS_backend.dto.ProfileResponse;
import com.main.EMS_backend.dto.UpdateProfileRequest;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.repository.UserRepository;
import com.main.EMS_backend.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/profile")
@Slf4j
public class ProfileController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public ProfileController(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload-profile")
    public ResponseEntity<?> uploadProfile(@RequestParam("file") MultipartFile file,
                                           Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Authentication is required");
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select an image to upload");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body("Only JPG, PNG, WEBP, or GIF images are allowed");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadAvatar(file, String.valueOf(user.getId()));
        } catch (RuntimeException ex) {
            log.warn("Cloudinary profile upload failed for user {}. Falling back to local storage.", user.getId(), ex);
            try {
                imageUrl = saveProfileImageLocally(file, user.getId());
            } catch (IOException ioException) {
                log.error("Local profile upload failed for user {}", user.getId(), ioException);
                return ResponseEntity.internalServerError().body("Profile image upload failed");
            }
        }

        user.setProfilePicture(imageUrl);
        userRepository.save(user);

        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setEmail(user.getEmail());
        profileResponse.setUsername(user.getUsername());
        profileResponse.setBranch(user.getBranch());
        profileResponse.setInstituteID(user.getInstituteID());
        profileResponse.setAddress(user.getAddress());
        profileResponse.setRole(user.getRole());
        profileResponse.setContact(user.getContact());
        profileResponse.setProfilePicture(user.getProfilePicture());

        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        if (request.getEmail() != null && !Objects.equals(request.getEmail(), user.getEmail())) {
            User existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser != null && !Objects.equals(existingUser.getId(), user.getId())) {
                return ResponseEntity.badRequest().body("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setBranch(request.getBranch());
        user.setInstituteID(request.getInstituteID());
        user.setContact(request.getContact());
        user.setAddress(request.getAddress());
        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest request) {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset successfully");
    }

    private String saveProfileImageLocally(MultipartFile file, Long userId) throws IOException {
        Path uploadDir = Paths.get("uploads", "profiles");
        Files.createDirectories(uploadDir);

        String fileName = userId + "_" + System.currentTimeMillis() + resolveImageExtension(file.getContentType());
        Path targetPath = uploadDir.resolve(fileName).normalize();
        Files.copy(file.getInputStream(), targetPath);

        return "/uploads/profiles/" + fileName;
    }

    private String resolveImageExtension(String contentType) {
        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }
        if ("image/webp".equalsIgnoreCase(contentType)) {
            return ".webp";
        }
        if ("image/gif".equalsIgnoreCase(contentType)) {
            return ".gif";
        }
        return ".jpg";
    }
}
