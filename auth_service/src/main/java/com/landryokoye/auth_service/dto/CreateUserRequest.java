package com.landryokoye.auth_service.dto;


import com.landryokoye.auth_service.enums.Roles;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record CreateUserRequest(
    @NotNull
    String firstName,
    @NotNull
    String lastName,
    @NotNull
    String username,
    @NotNull
    String email,
    @NotNull
    String password,
    @NotNull
    String sex,
    @Nullable
    Roles roles

) {

}
