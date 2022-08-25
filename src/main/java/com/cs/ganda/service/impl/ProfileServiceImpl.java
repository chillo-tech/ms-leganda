package com.cs.ganda.service.impl;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Address;
import com.cs.ganda.document.Location;
import com.cs.ganda.document.Profile;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.service.AuthenticationDataService;
import com.cs.ganda.service.ProfileService;
import com.cs.ganda.service.emails.MailsService;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.cs.ganda.enums.UserRole.USER;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@AllArgsConstructor
@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    public static final String USER_NOT_FOUND = "Aucun profile ne correspond à %s";
    private final ProfileRepository profileRepository;
    private final AuthenticationDataService authenticationDataService;
    private final MongoTemplate mongoTemplate;
    private final MailsService mailsService;

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

    /**/
    @Override
    public List<Profile> findByAddress(Address address) {
        log.info("Les profiles par localisation {} ", address.getLocation());
        Query query = new Query(Criteria.where("active").is(TRUE));
        if (address.getLocation().getCoordinates()!=null){
            query.addCriteria(
                    Criteria.where("address.location.coordinates")
                            .withinSphere(
                                    new Circle(
                                            new Point(address.getLocation().getCoordinates()[0], address.getLocation().getCoordinates()[1]),
                                            new Distance(30, Metrics.KILOMETERS)
                                    )
                            )
            );
        }
        return this.mongoTemplate.find(query, Profile.class);
    }
    /**/

    @Override
    public Profile getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Profile) authentication.getPrincipal();
    }

}
