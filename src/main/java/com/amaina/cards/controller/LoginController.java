package com.amaina.cards.controller;

import com.amaina.cards.dto.LoginRequest;
import com.amaina.cards.dto.LoginResponse;
import com.amaina.cards.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping("/api/login")
@RequiredArgsConstructor
@RestController
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     *  Logs in a user and returns a token
     * @param loginRequest Login
     * @return
     */
    @PostMapping
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),
                            loginRequest.password()));
            String email = authentication.getName();
            String authority = authentication.getAuthorities()
                    .stream().toList().get(0).getAuthority();
            return ok(jwtUtil.createToken(email,authority));

        } catch (BadCredentialsException e) {
            String ERROR_MSG = "Invalid credentials";
            LoginResponse loginResponse= LoginResponse.builder().error(ERROR_MSG).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponse);
        } catch (Exception e) {
            return badRequest().build();
        }
    }
}
