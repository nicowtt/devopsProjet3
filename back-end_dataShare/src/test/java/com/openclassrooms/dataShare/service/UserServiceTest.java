package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "password";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    @Test
    public void test_create_already_exist_user_throws_IllegalArgumentException() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.register(user));
    }

    @Test
    public void test_register_user() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // WHEN
        userService.register(user);

        // THEN
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void test_login_user() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("OK");

        // WHEN
        String result = userService.login(user);

        // THEN
        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void test_login_with_bad_password_throws_BadCredentialsException() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // THEN
        Assertions.assertThrows(BadCredentialsException.class,
            () -> userService.login(user));
    }
}
