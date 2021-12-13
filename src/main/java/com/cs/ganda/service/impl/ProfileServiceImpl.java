package com.cs.ganda.service.impl;

import com.cs.ganda.document.Profile;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.service.ProfileService;
import com.cs.ganda.service.sms.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.FALSE;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    public static final String USER_NOT_FOUND = "Aucun profile ne correspond à %s";
    private final ProfileRepository profileRepository;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public void register(Profile profile) {
        profile.setActive(FALSE);
        this.confirmationTokenService.sendActivationCode(profile);
        this.profileRepository.save(profile);
    }

    @Override
    public Profile findByEmail(String email) {
        return this.profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public Profile findByPhoneAndPhoneIndex(String phone, String phoneIndex) {
        return this.profileRepository.findTopByPhoneAndPhoneIndex(phone, phoneIndex).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, phone)));
    }

    public Profile findById(String id) {
        return this.profileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, "identifiant transmis")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] parts = username.split("_");
        return null; //this.profileRepository.findTopByPhoneAndPhoneIndex(parts[1], parts[0]).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, parts[1])));
    }


}
