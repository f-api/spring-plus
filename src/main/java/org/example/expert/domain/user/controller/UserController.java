package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.ProfilePicResponseDto;
import org.example.expert.domain.user.dto.response.UserProfileResponseDto;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(userChangePasswordRequest);
    }

    @PostMapping("/users/profile/picture")
    public ResponseEntity<ProfilePicResponseDto> uploadProfilePic (@RequestParam("files")MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(userService.uploadProfilePicture(multipartFile));
    }
}
