package com.xperience.hero.service;

import com.xperience.hero.dto.AuthResponse;
import com.xperience.hero.dto.LoginRequest;
import com.xperience.hero.dto.RegisterRequest;
import com.xperience.hero.exception.UnauthorizedException;
import com.xperience.hero.model.Host;
import com.xperience.hero.repository.HostRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final HostRepository hostRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(HostRepository hostRepository) {
        this.hostRepository = hostRepository;
    }

    public AuthResponse register(RegisterRequest req) {
        if (hostRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        Host host = new Host();
        host.setName(req.getName());
        host.setEmail(req.getEmail());
        host.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        String token = UUID.randomUUID().toString();
        host.setSessionToken(token);
        host = hostRepository.save(host);
        return new AuthResponse(host.getId(), host.getName(), host.getEmail(), token);
    }

    public AuthResponse login(LoginRequest req) {
        Host host = hostRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), host.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = UUID.randomUUID().toString();
        host.setSessionToken(token);
        hostRepository.save(host);
        return new AuthResponse(host.getId(), host.getName(), host.getEmail(), token);
    }

    public Host validateToken(String token) {
        return hostRepository.findBySessionToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired session. Please log in again."));
    }

    public void logout(String token) {
        hostRepository.findBySessionToken(token).ifPresent(host -> {
            host.setSessionToken(null);
            hostRepository.save(host);
        });
    }
}
