package com.mydaytodo.sfa.asset.security;

import com.mydaytodo.sfa.asset.model.AssetUser;
import com.mydaytodo.sfa.asset.model.Role;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            if(userRepository.getUserByUsername(username).isPresent()) {
                AssetUser user = userRepository.getUserByUsername(username).get();
                return new org.springframework.security.core.userdetails.User(user.getEmail(),
                        user.getPassword(),
                        mapRolesToAuthorities(user.getRoleList()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private Collection < ? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
