package com.project.ecommerce.appuser.user;


import com.project.ecommerce.appuser.address.UserAddress;
import com.project.ecommerce.appuser.role.UserRole;
import com.project.ecommerce.util.AppUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = AppUser.TABLE_NAME,
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
    })
public class AppUser
{
    public static final String TABLE_NAME = AppUtil.TABLE_NAME_PREFIX + "user";
    public static final String USER_TO_ROLE = TABLE_NAME + "_to_role";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String userName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    public AppUser(){}

    public AppUser(String name, String email, String pwd)
    {
        this.userName = name;
        this.email = email;
        this.password = pwd;
    }

    /*
    A role (like ADMIN, SELLER, etc.) can be shared across many users.
    A user can also have many roles.
    This is a many-to-many relationship → requires a join table to map the associations.
    The @JoinTable annotation is mandatory here, because JPA needs an intermediary table to link users and roles.
    */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = USER_TO_ROLE,
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<UserRole> roles;


    /*
    Each address belongs to only one user.
    But a user can have multiple addresses.
    This is a one-to-many relationship → handled by using a foreign key (user_id) in the UserAddress table.
    Since it’s a unidirectional OneToMany, no join table is needed — the foreign key in UserAddress is enough.
    */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private List<UserAddress> addresses;


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }


    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddress> addresses) {
        this.addresses = addresses;
    }
}
