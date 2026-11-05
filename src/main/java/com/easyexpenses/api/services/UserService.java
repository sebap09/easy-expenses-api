package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Long findOrCreateUser(Jwt jwt){
        String identityIssuer = jwt.getIssuer().toString();
        String identitySubject = jwt.getSubject();
        User user = userRepository
                .findByIdentityIssuerAndIdentitySubject(identityIssuer, identitySubject)
                .orElseGet(() -> {
                    User newUser = new UserBuilder()
                            .identityIssuer(identityIssuer)
                            .identitySubject(identitySubject)
                                    .build();

                    UserProfile userProfile = new UserProfile();
                    newUser.setUserProfile(userProfile);
                    userRepository.save(newUser);

                    return newUser;
                });

        return user.getId();
    }
}
