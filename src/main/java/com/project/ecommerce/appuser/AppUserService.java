package com.project.ecommerce.appuser;

import com.project.ecommerce.appuser.user.AppUserDTO;
import com.project.ecommerce.util.PageDetails;

public interface AppUserService
{
    PageDetails<AppUserDTO> getAllUsers(int pageNum, int pageSize, String sortBy, String sortOrder);
}
