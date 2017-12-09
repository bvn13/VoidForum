package ru.bvn13.voidforum.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.bvn13.voidforum.Constants;
import ru.bvn13.voidforum.models.Privilege;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.Role;
import ru.bvn13.voidforum.repositories.PrivilegeRepository;
import ru.bvn13.voidforum.repositories.UserRepository;
import ru.bvn13.voidforum.repositories.RoleRepository;
import ru.bvn13.voidforum.services.PrivilegeService;
import ru.bvn13.voidforum.services.RoleService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        Privilege ownerPrivilege = createPrivilegeIfNotFound(PrivilegeService.PRIVILEGE_OWNER);
        Privilege adminPrivilege = createPrivilegeIfNotFound(PrivilegeService.PRIVILEGE_ADMIN);
        Privilege readPrivilege = createPrivilegeIfNotFound(PrivilegeService.PRIVILEGE_READ);
        Privilege writePrivilege = createPrivilegeIfNotFound(PrivilegeService.PRIVILEGE_WRITE);
        Privilege writeHtmlPrivilege = createPrivilegeIfNotFound(PrivilegeService.PRIVILEGE_WRITE_HTML);
        Privilege blockedPrivilege = createPrivilegeIfNotFound(PrivilegeService.PRIVILEGE_BLOCKED);


        Role roleOwner = createRoleIfNotFound(RoleService.ROLE_OWNER, Arrays.asList(ownerPrivilege, adminPrivilege, writePrivilege, writeHtmlPrivilege, readPrivilege));

        createRoleIfNotFound(RoleService.ROLE_ADMIN, Arrays.asList(adminPrivilege, readPrivilege, writePrivilege));
        createRoleIfNotFound(RoleService.ROLE_ADMIN_WITH_HTML_EDITOR, Arrays.asList(adminPrivilege, readPrivilege, writePrivilege, writeHtmlPrivilege));
        createRoleIfNotFound(RoleService.ROLE_USER, Arrays.asList(readPrivilege, writePrivilege));
        createRoleIfNotFound(RoleService.ROLE_USER_WITH_HTML_EDITOR, Arrays.asList(readPrivilege, writePrivilege, writeHtmlPrivilege));
        createRoleIfNotFound(RoleService.ROLE_VISITOR, Arrays.asList(readPrivilege));
        createRoleIfNotFound(RoleService.ROLE_BLOCKED, Arrays.asList(readPrivilege, blockedPrivilege));


        if (userRepository.findAllByOneRoleByNameOrderById(RoleService.ROLE_OWNER).size() == 0) {
            User user = new User();
            user.setNickname(Constants.DEFAULT_ADMIN_NICKNAME);
            user.setEmail(Constants.DEFAULT_ADMIN_EMAIL);
            user.setPassword(passwordEncoder.encode(Constants.DEFAULT_ADMIN_PASSWORD));
            user.setRoles(Arrays.asList(roleOwner));
            user.setDisabled(false);
            userRepository.save(user);
        }

        alreadySetup = true;
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
        } else {
            // add new privileges
            for (Privilege privilege : privileges) {
                boolean found = false;
                for (Privilege p : role.getPrivileges()) {
                    if (p.getName().equals(privilege.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    role.getPrivileges().add(privilege);
                }
            }
            // remove unused privileges
            List<Privilege> privilegeListForRemoving = new ArrayList<>();
            for (Privilege p : role.getPrivileges()) {
                boolean found = false;
                for (Privilege privilege : privileges) {
                    if (p.getName().equals(privilege.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    privilegeListForRemoving.add(p);
                }
            }
            for (Privilege privilege : privilegeListForRemoving) {
                role.getPrivileges().remove(privilege);
            }
        }
        roleRepository.save(role);
        return role;
    }

}
