package com.project.ecommerce.appuser.role;

import com.project.ecommerce.util.AppUtil;
import jakarta.persistence.*;

@Entity
@Table(name = UserRole.TABLE_NAME)
public class UserRole {

    public static final String TABLE_NAME = AppUtil.TABLE_NAME_PREFIX + "user_role";

    public UserRole(){}

    public UserRole(Role role) {
        this.roleName = role;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Role roleName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Role getRoleName() {
        return roleName;
    }

    public void setRoleName(Role roleName) {
        this.roleName = roleName;
    }

    public enum Role
    {
        ROLE_USER("user"),
        ROLE_SELLER("seller"),
        ROLE_ADMIN("admin");

        private String label;

        Role(String label)
        {
            this.label = label;
        }

        public String label()
        {
            return label;
        }

    }
}
