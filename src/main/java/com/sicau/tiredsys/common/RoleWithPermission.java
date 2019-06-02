package com.sicau.tiredsys.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhong  on 2019/3/14 13:37
 */

//权限表
public class RoleWithPermission {
    private String role;
    private String permission;

    public static Set<String> getPermissionsByRole(String role) {
        Set<String> permissions = new HashSet<>();
        switch (role) {
            case "ordinary":
                permissions.add("read");
                permissions.add("write");
                permissions.add("audit");
            case "admin":
                permissions.add("read");
                permissions.add("write");
                permissions.add("execute");
                break;
            case "vip":
                permissions.add("read");
                permissions.add("write");
                permissions.add("execute");
                break;
            case "guest":
                permissions.add("apply");
                permissions.add("read");
                permissions.add("write");
                break;
        }
        return permissions;
    }
}
