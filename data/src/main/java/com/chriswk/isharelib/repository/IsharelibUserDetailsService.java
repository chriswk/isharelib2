package com.chriswk.isharelib.repository;

import com.chriswk.isharelib.data.Movie;
import com.chriswk.isharelib.data.Rating;
import com.chriswk.isharelib.data.User;
import com.chriswk.isharelib.service.IsharelibUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface IsharelibUserDetailsService extends UserDetailsService {
    IsharelibUserDetails loadUserByUsername(String login) throws UsernameNotFoundException;
    User getUserFromSession();

    @Transactional
    Rating rate(Movie movie, User user, int stars, String comment);

    @Transactional
    User register(String login, String name, String password);

    @Transactional
    void addFriend(String login, final User userFromSession);
}
