package com.belvinard.userManagement.config;

import com.belvinard.userManagement.dtos.UserDTO;
import com.belvinard.userManagement.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
            mapper.map(User::getUserName, UserDTO::setUserName);
            mapper.map(User::getEmail, UserDTO::setEmail);
            // Ajoutez d'autres mappings si nécessaire
        });

        return new ModelMapper();
    }

}