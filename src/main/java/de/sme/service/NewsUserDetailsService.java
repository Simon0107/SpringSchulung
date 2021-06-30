package de.sme.service;

import de.sme.repo.NewsUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NewsUserDetailsService implements UserDetailsService {
    NewsUserRepository newsUserRepository;

    public NewsUserDetailsService(NewsUserRepository newsUserRepository) {
        this.newsUserRepository = newsUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        var newsUser =
                newsUserRepository.findByUsername(username);
        if (newsUser == null) {
            throw new UsernameNotFoundException("Unknown user");
        }
        return new User(
                newsUser.getUsername(),
                newsUser.getPassword(),
                new ArrayList<>());
    }

}
