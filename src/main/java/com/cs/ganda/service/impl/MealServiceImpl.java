package com.cs.ganda.service.impl;

import com.cs.ganda.document.*;
import com.cs.ganda.dto.MealDTO;
import com.cs.ganda.enums.Status;
import com.cs.ganda.mapper.MealMapper;
import com.cs.ganda.repository.MealRepository;
import com.cs.ganda.repository.PictureRepository;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.repository.ValidityRepository;
import com.cs.ganda.service.AddressService;
import com.cs.ganda.service.CommonsMethods;
import com.cs.ganda.service.MealService;
import com.cs.ganda.service.emails.MailsService;
import com.cs.ganda.service.sms.ConfirmationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cs.ganda.datas.Constant.MISSING_FIELD;
import static com.cs.ganda.enums.Status.ACTIVE;

@Slf4j
@Service
public class MealServiceImpl extends CRUDServiceImpl<Meal, String> implements MealService {

    public static final String USER_NOT_FOUND = "Aucun profile ne correspond à %s";
    private final MealMapper mealMapper;
    private final MealRepository mealRepository;
    private final ProfileRepository profileRepository;
    private final ValidityRepository validityRepository;
    private final PictureRepository pictureRepository;
    private final AddressService addressService;
    private final MailsService mailsService;
    private final ConfirmationTokenService confirmationTokenService;
    private final CommonsMethods commonsMethods;

    public MealServiceImpl(
            MealMapper mealMapper,
            AddressService addressService,
            PictureRepository pictureRepository,
            ProfileRepository profileRepository,
            ValidityRepository validityRepository,
            MealRepository mealRepository,
            ConfirmationTokenService confirmationTokenService,
            MailsService mailsService,
            CommonsMethods commonsMethods
    ) {
        super(mealRepository);
        this.confirmationTokenService = confirmationTokenService;
        this.mailsService = mailsService;
        this.addressService = addressService;
        this.mealRepository = mealRepository;
        this.pictureRepository = pictureRepository;
        this.validityRepository = validityRepository;
        this.profileRepository = profileRepository;
        this.commonsMethods = commonsMethods;
        this.mealMapper = mealMapper;
    }

    public Meal create(Meal meal) throws UsernameNotFoundException {
        Objects.requireNonNull(meal.getName(), String.format(MISSING_FIELD, "Nom"));

        Profile profile = meal.getProfile();
        meal.setStatus(ACTIVE);
        profile = this.profileRepository.findByPhone(profile.getPhone());
        if (profile == null) {
            profile = meal.getProfile();
        }
        profile.setActive(Boolean.FALSE);
        profile = this.profileRepository.save(profile);
        meal.setProfile(profile);

        Validity validity = this.validityRepository.save(meal.getValidity());
        meal.setValidity(validity);

        Address address = this.addressService.create(meal.getAddress());
        meal.setAddress(address);
        meal.setCreation(Instant.now());

        List<Picture> pictures = pictureRepository.saveAll(meal.getPictures());
        meal.setPictures(pictures);

        Meal savedMeal = this.mealRepository.save(meal);
        this.mailsService.newPublication(savedMeal);
        //this.confirmationTokenService.sendActivationCode(meal.getProfile());
        return savedMeal;
    }

    @Override
    public List<Meal> search() throws UsernameNotFoundException {
        //Set<Meal> meals = this.mealRepository.findAllByStatusInAndActiveOrderByCreation(Set.of(ACTIVE), Boolean.TRUE).collect(Collectors.toSet());
        List<Meal> meals = this.mealRepository.findAll().stream().collect(Collectors.toList());
        return meals;
    }

    @Override
    public Set<MealDTO> findMeals() throws UsernameNotFoundException {
        //Set<Meal> meals = this.mealRepository.findAllByStatusInAndActiveOrderByCreation(Set.of(ACTIVE), Boolean.TRUE).collect(Collectors.toSet());
        Set<MealDTO> meals = this.mealRepository.findAllByStatusInOrderByCreation(Set.of(ACTIVE)).map(MealMapper::toDto).collect(Collectors.toSet());
        return meals;
    }


    /**
     * Toutes les heures
     * <p>
     * Mis à jour du statut des campagnes actives
     */
    @Scheduled(cron = "@daily", zone = "Europe/Paris")
    public void updateMealStatus() {
        log.info("Mis à jour des campagnes à {}", Instant.now());
        final Stream<Meal> meals = this.mealRepository.findAllByStatusInOrderByCreation(Set.of(ACTIVE));
        final Set<Meal> mealsAsSet = meals.peek(meal -> {
            final Status status = this.commonsMethods.getStatusFromDates(meal.getValidity(), meal.getValidity());
            meal.setStatus(status);
        }).collect(Collectors.toSet());

        this.mealRepository.saveAll(mealsAsSet);
    }
}
