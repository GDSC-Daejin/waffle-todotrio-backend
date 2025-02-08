package com.example.todo.service;

import com.example.todo.dto.UserDto;
import com.example.todo.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");

        if (!userService.findByUsername(email).isPresent()) {
            UserDto userDto = new UserDto();
            userDto.setUsername(email);
            userDto.setEmail(email);
            userDto.setPassword(UUID.randomUUID().toString());
            userService.signup(userDto);
        }

        return oauth2User;
    }
}