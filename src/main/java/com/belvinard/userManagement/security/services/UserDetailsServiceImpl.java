package com.belvinard.userManagement.security.services;

import com.belvinard.userManagement.exceptions.CustomUsernameNotFoundException;
import com.belvinard.userManagement.model.User;
import com.belvinard.userManagement.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws CustomUsernameNotFoundException {
        logger.info("Loading user with username: {}", username);

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new CustomUsernameNotFoundException("Utilisateur introuvable avec le nom : " + username);
                });

        logger.info("User found: {}", user.getUserName());
        return UserDetailsImpl.build(user);
    }
}
