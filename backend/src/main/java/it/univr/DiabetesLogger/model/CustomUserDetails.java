package it.univr.DiabetesLogger.model;

import java.util.Collection;
import java.util.Collections;

import it.univr.DiabetesLogger.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails{

    private User user;

    private Integer profileId;

    public CustomUserDetails(){}

    public CustomUserDetails(User user, Integer profileId){
        this.user = user;
        this.profileId = profileId;
    }

    public Boolean checkId(Integer id){
        return id.equals(profileId);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
       String role = user.getRole() != null
               ? user.getRole().toString()
               : "NONE";

        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    public String getPassword(){
        return user.getPassword();
    }

    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired(){ return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        if(user.getRole() == Role.ADMIN){
            return true; // admin non deve aspettare verifica
        }
        return user.isVerified();
    }
}
