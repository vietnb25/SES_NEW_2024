package vn.ses.s3m.plus.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dto.User;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    /**
     * Hàm set User detail.
     *
     * @param String username.
     * @return UserDetails userDetails.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {
        User user = this.userMapper.getUserByUsername(username);
        org.springframework.security.core.userdetails.User u = null;
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = this.userMapper.getRoleNames((long) user.getId());
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        u = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        return u;
    }
}
