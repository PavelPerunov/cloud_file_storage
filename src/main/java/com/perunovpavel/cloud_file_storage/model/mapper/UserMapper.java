package com.perunovpavel.cloud_file_storage.model.mapper;

import com.perunovpavel.cloud_file_storage.model.dto.UserRegisterRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserRegisterRequestDto userRegisterRequestDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    UserResponseDto toUserResponseDto(User user);
}
