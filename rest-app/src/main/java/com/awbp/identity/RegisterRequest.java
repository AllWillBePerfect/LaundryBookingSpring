package com.awbp.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank String displayName,
        @NotBlank @Size(min = 8) String password
) {
}
