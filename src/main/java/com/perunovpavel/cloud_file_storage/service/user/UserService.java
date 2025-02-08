package com.perunovpavel.cloud_file_storage.service.user;

import com.perunovpavel.cloud_file_storage.exception.UserAlreadyExistsException;
import com.perunovpavel.cloud_file_storage.model.dto.UserRegisterRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.model.entity.Role;
import com.perunovpavel.cloud_file_storage.model.entity.User;
import com.perunovpavel.cloud_file_storage.model.mapper.UserMapper;
import com.perunovpavel.cloud_file_storage.repository.RoleRepository;
import com.perunovpavel.cloud_file_storage.repository.UserRepository;
import com.perunovpavel.cloud_file_storage.service.core.BaseUserService;
import com.perunovpavel.cloud_file_storage.service.core.impl.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService extends BaseUserService {

    private final StorageServiceImpl storageService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       StorageServiceImpl storageService,
                       PasswordEncoder passwordEncoder,
                       UserMapper mapper) {
        super(userRepository, roleRepository);
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Transactional
    public UserResponseDto registration(UserRegisterRequestDto dto) {
        validateEmailForRegistration(dto);

        encodePassword(dto);

        User user = mapper.toEntity(dto);
        Role role = getRoleByRoleName("USER");
        user.setRole(role);

        userRepository.save(user);

        storageService.createMainUserFolder(user.getId());

        return mapper.toUserResponseDto(user);
    }


    private void validateEmailForRegistration(UserRegisterRequestDto userRegisterRequestDto) {
        findByEmail(userRegisterRequestDto.getEmail()).ifPresent(
                user -> {
                    throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
                }
        );
    }

    private void encodePassword(UserRegisterRequestDto userRegisterRequestDto) {
        String decodedPassword = userRegisterRequestDto.getPassword();
        userRegisterRequestDto.setPassword(passwordEncoder.encode(decodedPassword));
    }

}

