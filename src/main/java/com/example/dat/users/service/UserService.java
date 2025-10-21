package com.example.dat.users.service;

import com.example.dat.res.Response;
import com.example.dat.users.dto.UpdatePasswordRequest;
import com.example.dat.users.dto.UserDTO;
import com.example.dat.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {


    User getCurrentUser();

    Response<UserDTO> getMyUserDetails();

    Response<UserDTO> getUserById(Long userId);

    Response<List<UserDTO>> getAllUsers();

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    Response<?> uploadProfilePicture(MultipartFile file);

    Response<?> uploadProfilePictureToS3(MultipartFile file);

}
