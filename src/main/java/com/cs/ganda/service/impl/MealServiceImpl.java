package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.document.Meal;
import com.cs.ganda.document.Profile;
import com.cs.ganda.dto.SearchParamsDTO;
import com.cs.ganda.enums.Status;
import com.cs.ganda.repository.MealRepository;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.service.CommonsMethods;
import com.cs.ganda.service.ImageService;
import com.cs.ganda.service.MealService;
import com.cs.ganda.service.emails.MailsService;
import com.cs.ganda.service.sms.ConfirmationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
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
import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Service
public class MealServiceImpl extends CRUDServiceImpl<Meal, String> implements MealService {
    private final MealRepository mealRepository;
    private final ProfileRepository profileRepository;
    private final MailsService mailsService;
    private final CommonsMethods commonsMethods;
    private final MongoTemplate mongoTemplate;
    private final ConfirmationTokenService confirmationTokenService;
    private final ImageService imageService;

    public MealServiceImpl(
            ProfileRepository profileRepository,
            MealRepository mealRepository,
            MailsService mailsService,
            ImageService imageService,
            ConfirmationTokenService confirmationTokenService,
            CommonsMethods commonsMethods,
            MongoTemplate mongoTemplate
    ) {
        super(mealRepository);
        this.imageService = imageService;
        this.mailsService = mailsService;
        this.confirmationTokenService = confirmationTokenService;
        this.mealRepository = mealRepository;
        this.profileRepository = profileRepository;
        this.commonsMethods = commonsMethods;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void activate(ActivationData activationData) {
        ConfirmationToken confirmationToken = this.confirmationTokenService.activate(
                activationData.getItemId(),
                activationData.getPhone(),
                activationData.getPhoneIndex(),
                activationData.getToken()
        );
        Meal meal = this.read(confirmationToken.getAdId());

        Update update = new Update().set("active", TRUE);
        this.mongoTemplate.update(Meal.class).matching(where("id").is(meal.getId()))
                .apply(update).first();
    }

    @Override
    public Meal create(Meal meal) throws UsernameNotFoundException {
        Objects.requireNonNull(meal.getName(), String.format(MISSING_FIELD, "Nom"));

        Profile profile = meal.getProfile();
        profile = this.profileRepository.findByPhone(profile.getPhone());
        if (profile == null) {
            profile = meal.getProfile();
        }
        profile.setActive(Boolean.FALSE);
        meal.setActive(Boolean.FALSE);
        meal.setProfile(profile);

        meal.setCreation(Instant.now());
        Meal savedMeal = this.mealRepository.save(meal);
        this.confirmationTokenService.sendAddActivationCode(
                savedMeal.getId(),
                meal.getProfile().getPhoneIndex(),
                meal.getProfile().getPhone(),
                savedMeal.getId()
        );
        this.mailsService.newPublication(savedMeal);
        this.imageService.saveMealImages(meal);
        return savedMeal;
    }


    @Override
    public List<Meal> search(SearchParamsDTO searchParams, int page, int size) throws UsernameNotFoundException {

        Instant date = Instant.now();
        Query query = new Query(Criteria.where("validity.date").gte(date));
        query.addCriteria(Criteria.where("active").is(TRUE));

        if (searchParams.getDate() != null) {
            query.addCriteria(Criteria.where("validity.date").gte(searchParams.getDate()));
        }

        if (searchParams.getQuery() != null) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(searchParams.getQuery()));
        }

        if (searchParams.getCoordinates() != null) {
            query.addCriteria(
                    Criteria.where("address.coordinates.coordinates")
                            .withinSphere(
                                    new Circle(
                                            new Point(searchParams.getCoordinates()[0], searchParams.getCoordinates()[1]),
                                            new Distance(30, Metrics.KILOMETERS)
                                    )
                            )
            );
        }

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(ASC, "validity.start"));
        query.with(pageRequest);
        return this.mongoTemplate.find(query, Meal.class);
    }

    @Override
    public Meal read(String id) {
        Meal meal = this.mealRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Aucune entite ne correspond à l'id " + id));
        updateViews(meal.getId());
        return meal;
    }

    private void updateViews(String id) {
        Query query = new Query(where("id").is(id));
        Update update = new Update().inc("views", 1);
        this.mongoTemplate.updateFirst(query, update, Meal.class);
    }

    /**
     * Tous les jours
     * <p>
     * Mis à jour du statut des articles actives
     */
    @Scheduled(cron = "@daily", zone = "Europe/Paris")
    public void updateMealStatus() {
        log.info("Mis à jour des articles à {}", Instant.now());
        final Stream<Meal> meals = this.mealRepository.findAllByStatusInOrderByCreation(Set.of(ACTIVE));
        final Set<Meal> mealsAsSet = meals.peek(meal -> {
            final Status status = this.commonsMethods.getStatusFromDates(meal.getValidity(), meal.getValidity());
            meal.setStatus(status);
        }).collect(Collectors.toSet());

        this.mealRepository.saveAll(mealsAsSet);
    }

    /**
     * Tous les jours
     * <p>
     * Mis à jour du statut des articles actives
     */
    @Scheduled(cron = "@daily", zone = "Europe/Paris")
    public void deleteMealStatus() {
        log.info("Suppresion des articles à {}", Instant.now());
        final List<String> ids = this.mealRepository.findAllByActive(Boolean.FALSE).map(Meal::getId).collect(Collectors.toList());
        this.mealRepository.deleteAllById(ids);
    }
}
