package com.cs.ganda.service.impl;

import com.cs.ganda.document.Address;
import com.cs.ganda.document.Profile;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.service.AuthenticationDataService;
import com.cs.ganda.service.ProfileService;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

import static com.cs.ganda.enums.UserRole.USER;
import static java.lang.Boolean.FALSE;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    public static final String USER_NOT_FOUND = "Aucun profile ne correspond à %s";
    private final ProfileRepository profileRepository;
    private final AuthenticationDataService authenticationDataService;

    @Override
    public void register(Profile profile) {
        profile.setActive(FALSE);
        profile.setFullPhone(profile.getPhoneIndex() + profile.getPhone());
        profile.setRoles(Sets.newHashSet(USER));
        this.profileRepository.save(profile);
    }

    @Override
    public Profile update(Profile profile) {
        profile.setRoles(Sets.newHashSet(USER));
        profile.setFullPhone(profile.getPhoneIndex() + profile.getPhone());
        Profile currentProfile = this.findByPhoneAndPhoneIndex(profile.getPhone(), profile.getPhoneIndex());
        try {
            BeanUtils.copyProperties(currentProfile, profile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this.profileRepository.save(currentProfile);
    }

    @Override
    public Profile findByEmail(String email) {
        return this.profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public Profile findByPhoneAndPhoneIndex(String phone, String phoneIndex) {
        return this.profileRepository.findByPhoneAndPhoneIndex(phone, phoneIndex).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, phone)));
    }

    @Override
    public Profile findById(String id) {
        return this.profileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, "identifiant transmis")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.profileRepository.findByFullPhone(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username)));
    }

    @Override
    public void updateAddress(Address address) {
        Profile profile = this.authenticationDataService.getAuthenticatedProfile();
        profile.setAddress(address);
        this.profileRepository.save(profile);
    }

}
