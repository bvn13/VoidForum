package ru.bvn13.voidforum.services;

import org.springframework.stereotype.Service;

@Service
public class PrivilegeService {
    public static final String PRIVILEGE_OWNER = "OWNER_PRIVILEGE";
    public static final String PRIVILEGE_ADMIN = "ADMIN_PRIVILEGE";
    public static final String PRIVILEGE_READ = "READ_PRIVILEGE";
    public static final String PRIVILEGE_WRITE = "WRITE_PRIVILEGE";
    public static final String PRIVILEGE_WRITE_HTML = "WRITE_PRIVILEGE_HTML";
    public static final String PRIVILEGE_BLOCKED = "BLOCKED_PRIVILEGE";
}
