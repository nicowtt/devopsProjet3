package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.LoginDTO;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(User user) {
        Assert.notNull(user, "User must not be null");
        log.info("Registering new user");

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new IllegalArgumentException("Le compte " + user.getEmail() + " existe déjà.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String login(User user) {
        Assert.notNull(user.getEmail(), "Login email must not be null");
        Assert.notNull(user.getPassword(), "Password must not be null");
        Optional<User> userDb = userRepository.findByEmail(user.getEmail());
        if (userDb.isPresent() && passwordEncoder.matches(user.getPassword(), userDb.get().getPassword())) {
            return jwtService.generateToken(userDb.get());
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
