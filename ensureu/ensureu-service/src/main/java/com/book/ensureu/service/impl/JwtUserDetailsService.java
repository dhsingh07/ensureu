package com.book.ensureu.service.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.User;
import com.book.ensureu.repository.UserRepository;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(JwtUserDetailsService.class);

	@Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).get();

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username .", username));
        } else {
            return com.book.ensureu.security.JwtUserFactory.createJwtUser(user);
        }
    }
}
