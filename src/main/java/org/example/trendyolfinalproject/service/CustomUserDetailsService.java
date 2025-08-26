package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(authority))
                .disabled(!Boolean.TRUE.equals(user.getIsActive()))
                .build();
    }


//        List<GrantedAuthority> authorities = u.getRoles().stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//
//        return new SecurityProperties.User(
//                u.getUsername(),
//                u.getPassword(),
//                authorities
//        );

    }

