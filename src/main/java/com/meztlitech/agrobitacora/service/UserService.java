package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        try {
            return username -> userRepository.findByUserName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (Exception e){
            log.info(e.getMessage());
        }
        return null;
    }
}
