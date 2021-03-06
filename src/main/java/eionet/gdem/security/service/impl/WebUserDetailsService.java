package eionet.gdem.security.service.impl;

import eionet.gdem.security.model.WebUser;
import eionet.gdem.utils.SecurityUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component("webuserdetailsservice")
public class WebUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        WebUser user = new WebUser();
        user.setUser(s);
        return user;
    }
}
