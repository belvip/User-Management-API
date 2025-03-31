package com.belvinard.userManagement.services.impl;

import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.exceptions.ResourceNotFoundException;
import com.belvinard.userManagement.model.User;

import com.belvinard.userManagement.repositories.UserRepository;
import com.belvinard.userManagement.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final   ModelMapper modelMapper;


    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (updatedUser.getUserName() != null) {
                        user.setUserName(updatedUser.getUserName());
                    }
                    if (updatedUser.getEmail() != null) {
                        user.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPassword() != null) {
                        user.setPassword(updatedUser.getPassword()); // Il faudra hasher le mot de passe
                    }
                    if (updatedUser.getRole() != null) {
                        user.setRole(updatedUser.getRole());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        userRepository.delete(user);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        // Mapper l'entité User vers le DTO UserDTO
        return modelMapper.map(user, UserDTO.class);
    }
//    public User getUserById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId",  userId));
//    }



}
