package com.project.ecommerce.appuser.user;


import com.project.ecommerce.appuser.address.UserAddressDTO;
import com.project.ecommerce.appuser.role.UserRole.Role;

import java.util.List;

public class AppUserDTO
{
    public long userId;
    public String userName;
    public String email;
    public String password;
    public List<Role> roleIds;
    public List<UserAddressDTO> userAddressDTOS;
}
