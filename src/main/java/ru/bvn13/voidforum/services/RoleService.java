package ru.bvn13.voidforum.services;

import org.springframework.stereotype.Service;

@Service
public class RoleService {
    public static final String ROLE_OWNER = "ROLE_OWNER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_ADMIN_WITH_HTML_EDITOR = "ROLE_ADMIN_WITH_HTML_EDITOR";
    public static final String ROLE_USER  = "ROLE_USER";
    public static final String ROLE_USER_WITH_HTML_EDITOR  = "ROLE_USER_WITH_HTML_EDITOR";
    public static final String ROLE_VISITOR = "ROLE_VISITOR";
    public static final String ROLE_BLOCKED = "ROLE_BLOCKED";

}
