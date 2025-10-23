package com.project.ecommerce.security.services;

import com.project.ecommerce.appuser.user.AppUser;
import com.project.ecommerce.appuser.user.AppUserRepo;
import com.project.ecommerce.exceptionhandler.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AppUserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    @Transactional //method will get fully executed or it will get revert back fully.
    public UserDetails loadUserByEmail(String email) throws ResourceNotFoundException
    {
        AppUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email id", email));

        return UserDetailsImpl.build(user);
    }


}