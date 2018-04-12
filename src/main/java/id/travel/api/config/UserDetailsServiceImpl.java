package id.travel.api.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;

import static java.util.Collections.emptyList;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository applicationUserRepository;
    public UserDetailsServiceImpl(UserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User applicationUser = applicationUserRepository.findUserByUserId(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(applicationUser.getUserid(), applicationUser.getPassword(), emptyList());
    }
}