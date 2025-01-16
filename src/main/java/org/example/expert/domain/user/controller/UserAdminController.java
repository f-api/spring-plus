package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PatchMapping("/{userId}")
    public ResponseEntity<Void> changeUserRole(
        @PathVariable long userId,
        @RequestBody UserRoleChangeRequest userRoleChangeRequest
    ) {
        userAdminService.changeUserRole(userId, userRoleChangeRequest);

        return ResponseEntity.noContent().build();
    }
}
