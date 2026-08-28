package com.easyexpenses.api.services;

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
    public UserProfile findOrCreateUser(Jwt jwt){
        String identityIssuer = jwt.getIssuer().toString();
        String identitySubject = jwt.getSubject();

        return userRepository
                .findByIdentityIssuerAndIdentitySubject(identityIssuer, identitySubject)
                .map(User::getUserProfile)
                .orElseGet(() -> {
                    User user = new User();
                    user.setIdentityIssuer(identityIssuer);
                    user.setIdentitySubject(identitySubject);

                    UserProfile userProfile = new UserProfile();
                    user.setUserProfile(userProfile);
                    userRepository.save(user);

                    return userProfile;
                });
    }
}
