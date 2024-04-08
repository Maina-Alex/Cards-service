package com.amaina.cards.service;

import com.amaina.cards.model.AppUser;
import com.amaina.cards.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       AppUser appUser = appUserRepository.findByEmail(email)
               .orElseThrow(()->new UsernameNotFoundException("Invalid user credentials"));
       return fromAppUser(appUser);
    }

    @Override
    public AppUser findUserByEmail(String email) {
        return appUserRepository.findByEmail(email).orElse(null);
    }

    /**
     * Maps Model AppUser to Spring UserDetails
     * @param appUser AppUser
     * @return Spring security UserDetails
     */
    private UserDetails fromAppUser(AppUser appUser){
        return User.builder().username(appUser.getEmail())
                .authorities(appUser.getRole()
                        .name())
                .accountLocked(!appUser.isAccountActive())
                .password(appUser.getPassword()).build();
    }


}
