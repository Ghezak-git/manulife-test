package com.example.manulife.controllers;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manulife.entities.User;
import com.example.manulife.models.request.UserRequest;
import com.example.manulife.models.response.GeneralResponse;
import com.example.manulife.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        GeneralResponse generalResponse = new GeneralResponse(false, "Failed to get users", null);
        try {
            List<User> users = userService.findAll();
            generalResponse.setSuccess(true);
            generalResponse.setMessage("Success get users");
            generalResponse.setData(users);

            return ResponseEntity.ok(generalResponse);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ResponseEntity.internalServerError().body(generalResponse);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> save(@Valid @RequestBody UserRequest userRequest, BindingResult bindingResult, HttpServletResponse response) {
        System.out.println("UserRequest: " + userRequest);

        GeneralResponse generalResponse = new GeneralResponse(false, "Failed to save user", null);
        
        if (bindingResult.hasErrors()) {
            // Validation errors occurred
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            generalResponse.setMessage("Validation errors occurred");
            generalResponse.setData(errors);

            return ResponseEntity.badRequest().body(generalResponse);
        }
        
        try {
            if (userRequest.getPassword() == null) {
                generalResponse.setMessage("Password is required");
                return ResponseEntity.badRequest().body(generalResponse);
            }

            if (userRequest.getPassword().length() < 6) {
                generalResponse.setMessage("Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(generalResponse);
            }

            User user = new User();
            user.setFullName(userRequest.getFullName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setStatus(userRequest.getStatus());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userService.save(user);
            generalResponse.setSuccess(true);
            generalResponse.setMessage("Success saved user");
            generalResponse.setData(savedUser);

            return ResponseEntity.ok(generalResponse);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ResponseEntity.internalServerError().body(generalResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        GeneralResponse generalResponse = new GeneralResponse(false, "Failed to get user", null);
        try {
            User user = userService.findById(id);
            if (user == null) {
                generalResponse.setMessage("User not found");
                return ResponseEntity.badRequest().body(generalResponse);
            }
            generalResponse.setSuccess(true);
            generalResponse.setMessage("Success get user");
            generalResponse.setData(user);

            return ResponseEntity.ok(generalResponse);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ResponseEntity.internalServerError().body(generalResponse);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        GeneralResponse generalResponse = new GeneralResponse(false, "Failed to update user", null);
        try {
            User user = userService.findById(id);
            if (user == null) {
                generalResponse.setMessage("User not found");
                return ResponseEntity.badRequest().body(generalResponse);
            }

            user.setFullName(userRequest.getFullName());
            user.setEmail(userRequest.getEmail());
            user.setStatus(userRequest.getStatus());
            user.setUpdatedAt(LocalDateTime.now());

            if (userRequest.getPassword() != null && userRequest.getPassword().length() >= 6) {
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }

            User updatedUser = userService.save(user);
            generalResponse.setSuccess(true);
            generalResponse.setMessage("Success updated user");
            generalResponse.setData(updatedUser);

            return ResponseEntity.ok(generalResponse);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ResponseEntity.internalServerError().body(generalResponse);
        }
    }

    @GetMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        GeneralResponse generalResponse = new GeneralResponse(false, "Failed to delete user", null);
        try {
            User user = userService.findById(id);
            if (user == null) {
                generalResponse.setMessage("User not found");
                return ResponseEntity.badRequest().body(generalResponse);
            }

            userService.delete(user);
            generalResponse.setSuccess(true);
            generalResponse.setMessage("Success deleted user");

            return ResponseEntity.ok(generalResponse);
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ResponseEntity.internalServerError().body(generalResponse);
        }
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> generateUserReport() {
        try {
            ByteArrayInputStream reportStream = userService.generateUserReport();
            byte[] reportBytes = reportStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=users_report.pdf"); // Change inline to attachment

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportBytes);
        } catch (Exception e) {
            // Handle exceptions (e.g., log the error, return an error response)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
}
