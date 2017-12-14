package ru.bvn13.voidforum.services;

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
import ru.bvn13.voidforum.error.EmailExistsException;
import ru.bvn13.voidforum.error.NicknameExistsException;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.Privilege;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.Role;
import ru.bvn13.voidforum.models.support.AppLocale;
import ru.bvn13.voidforum.models.support.PostFormat;
import ru.bvn13.voidforum.repositories.PrivilegeRepository;
import ru.bvn13.voidforum.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bvn13.voidforum.repositories.RoleRepository;
import ru.bvn13.voidforum.utils.DTOUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@JadeHelper("userService")
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private SessionFactory sessionFactory;



    @PostConstruct
    protected void initialize() {
        //getSuperUser();
    }

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        org.springframework.security.core.userdetails.User springUser = null;

        User user = userRepository.findByEmail(emailOrNickname);
        if (user == null) {
            user = userRepository.findByNickname(emailOrNickname);
        }

        if (user == null) {
            springUser = new org.springframework.security.core.userdetails.User(
                    "", "", true, true, true, true,
                    getAuthorities(Arrays.asList(
                            roleRepository.findByName(RoleService.ROLE_VISITOR))
                    )
            );
        } else {
            List<Role> roles = roleRepository.findAllByUser(user);
            springUser = new org.springframework.security.core.userdetails.User(
                    user.getEmail(), user.getPassword(), !user.getDisabled(), true, true,
                    true, getAuthorities(roles)
            );
        }

        return springUser;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    public List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            //role.setPrivileges(privilegeRepository.findAllByRole(role));
            collection.addAll(privilegeRepository.findAllByRole(role));
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }


    public User currentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || auth instanceof AnonymousAuthenticationToken){
            return null;
        }

        String email = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();

        return userRepository.findByEmail(email);
    }

    public Boolean isCurrentUserAdmin() {
        User user = this.currentUser();
        Boolean isAdmin = user != null ? this.hasPrivilege(user, PrivilegeService.PRIVILEGE_ADMIN) : false;
        return isAdmin;
    }

    public boolean changePassword(User user, String password, String newPassword){
        if (password == null || newPassword == null || password.isEmpty() || newPassword.isEmpty())
            return false;

        logger.info("" + passwordEncoder.matches(password, user.getPassword()));
        boolean match = passwordEncoder.matches(password, user.getPassword());
        if (!match)
            return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("User @"+user.getEmail() + " changed password.");

        return true;
    }


    public Boolean currentUserHasPrivilege(String privilege) {
        User user = this.currentUser();
        return this.hasPrivilege(user, privilege);
    }

    public Boolean currentUserCanWrite() {
        return this.currentUserHasPrivilege(PrivilegeService.PRIVILEGE_WRITE);
    }

    public Boolean currentUserCanWriteCommentToPost(Post post) {
        User user = currentUser();
        return hasPrivilege(user, PrivilegeService.PRIVILEGE_OWNER)
                || post.getUser().getId().equals(user.getId()) ? false : post.getDisableCommenting();
    }


    public User registerNewUserAccount(User user) throws EmailExistsException, NicknameExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + user.getEmail());
        }
        if (nicknameExists(user.getNickname())) {
            throw new NicknameExistsException("There is an account with that nickname: " + user.getNickname());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDisabled(false);

        user.setRoles(Arrays.asList(roleRepository.findByName(RoleService.ROLE_USER)));
        return userRepository.save(user);
    }


    public Boolean emailExist(String email) {
        return this.userRepository.findByEmail(email) != null;
    }

    public Boolean nicknameExists(String nickname) {
        return this.userRepository.findByNickname(nickname) != null;
    }

    public Boolean hasRole(User user, String role) {
        if (user == null) {
            return false;
        }
        AtomicReference<Boolean> hasRole = new AtomicReference<>(false);
        user.getRoles().forEach(r -> {
            if (r.getName().equals(role)) {
                hasRole.set(true);
            }
        });
        return hasRole.get();
    }

    public Boolean hasPrivilege(User user, String privilege) {
        if (user == null) {
            return false;
        }
        AtomicReference<Boolean> hasPrivilege = new AtomicReference<>(false);
        user.getRoles().forEach(r -> {
            r.getPrivileges().forEach(p -> {
                if (p.getName().equals(privilege)) {
                    hasPrivilege.set(true);
                }
            });
        });
        return hasPrivilege.get();
    }


    public List<PostFormat> getAvailablePostFormats(User user) {
        List<PostFormat> availableFormats = new ArrayList<>();
        for (PostFormat postFormat : PostFormat.values()) {
            if (postFormat.equals(PostFormat.HTML)) {
                if (hasPrivilege(user, PrivilegeService.PRIVILEGE_WRITE_HTML)) {
                    availableFormats.add(postFormat);
                }
            } else {
                availableFormats.add(postFormat);
            }
        }
        return availableFormats;
    }

}
