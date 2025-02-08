package com.perunovpavel.cloud_file_storage.service.admin;

import com.perunovpavel.cloud_file_storage.model.entity.Role;
import com.perunovpavel.cloud_file_storage.model.entity.User;
import com.perunovpavel.cloud_file_storage.repository.RoleRepository;
import com.perunovpavel.cloud_file_storage.repository.UserRepository;
import com.perunovpavel.cloud_file_storage.service.core.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AdminService extends BaseUserService {

    @Autowired
    public AdminService(UserRepository userRepository, RoleRepository roleRepository) {
        super(userRepository, roleRepository);
    }

    @Transactional
    public void updateUserRole(String email, String roleName) {
        User user = getUserByEmail(email);
        Role role = getRoleByRoleName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void delete(String email) {
        User user = getUserByEmail(email);
        userRepository.delete(user);
    }
}
