package com.awbp.auth;

import com.awbp.identity.application.AuthService;
import com.awbp.identity.domain.TokenPair;
import com.awbp.security.RefreshRequest;
import com.awbp.security.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegisterRequest request) {
        TokenPair pair = authService.register(
                request.username(), request.displayName(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(TokenResponse.of(pair));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenPair pair = authService.login(request.username(), request.password());
        return ResponseEntity.ok(TokenResponse.of(pair));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        TokenPair pair = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(TokenResponse.of(pair));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
