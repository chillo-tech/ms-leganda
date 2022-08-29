package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Address;
import com.cs.ganda.document.Profile;
import com.cs.ganda.dto.SearchParamsDTO;
import com.cs.ganda.enums.Status;
import com.cs.ganda.repository.AdRepository;
import com.cs.ganda.service.AdService;
import com.cs.ganda.service.CommonsMethods;
import com.cs.ganda.service.ImageService;
import com.cs.ganda.service.NotificationService;
import com.cs.ganda.service.ProfileService;
import com.cs.ganda.service.emails.MailsService;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cs.ganda.datas.Constant.MISSING_FIELD;
import static com.cs.ganda.enums.Status.ACTIVE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Service
public class AdServiceImpl extends CRUDServiceImpl<Ad, String> implements AdService {
    private final AdRepository adRepository;
    private final MailsService mailsService;
    private final CommonsMethods commonsMethods;
    private final MongoTemplate mongoTemplate;
    private final ImageService imageService;
    private final NotificationService notificationService;
    private final ProfileService profileService;

    public AdServiceImpl(
            final AdRepository adRepository,
            final MailsService mailsService,
            final ImageService imageService,
            final CommonsMethods commonsMethods,
            final MongoTemplate mongoTemplate,
            final NotificationService notificationService,
            final ProfileService profileService
    ) {
        super(adRepository);
        this.imageService = imageService;
        this.mailsService = mailsService;
        this.adRepository = adRepository;
        this.commonsMethods = commonsMethods;
        this.mongoTemplate = mongoTemplate;
        this.notificationService = notificationService;
        this.profileService = profileService;
    }

    @Override
    public void activate(final ActivationData activationData) {
    }

    @Override
    public Ad create(final Ad ad) throws UsernameNotFoundException {
        Objects.requireNonNull(ad.getName(), String.format(MISSING_FIELD, "Nom"));
        final Instant currentStart = ad.getValidity().getStart();
        final int hour = currentStart.atZone(ZoneOffset.UTC).getHour();
        final int minute = currentStart.atZone(ZoneOffset.UTC).getMinute();
        final int second = currentStart.atZone(ZoneOffset.UTC).getSecond();

        final Instant newStart = ad.getValidity().getDate();
        newStart.atZone(ZoneOffset.UTC)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .toInstant();
        ad.getValidity().setStart(newStart);

        final Profile profile = this.getAuthenticatedProfile();
        ad.setActive(TRUE);
        ad.setProfile(profile);

        ad.setCreation(Instant.now());
        final Ad savedAd = this.adRepository.save(ad);
        this.mailsService.newPublication(savedAd);
        this.imageService.saveAdImages(ad);
        this.notificationService.newAdNotificationToCustomers(savedAd);
        return savedAd;
    }

    @Override
    public Stream<Ad> search(final SearchParamsDTO searchParams, final int page, final int size) {
        log.info("Recherche avec les critères {} {} {}", searchParams, page, size);
        final Instant date = Instant.now();
        final Query query = new Query(Criteria.where("active").is(TRUE));
        query.addCriteria(Criteria.where("validity.start").gte(date));

        if (searchParams.getQuery() != null && !searchParams.getQuery().trim().isEmpty()) {
            final Criteria criteria = new Criteria();
            criteria.orOperator(
                    Criteria.where("name").regex(searchParams.getQuery(), "i"),
                    Criteria.where("description").regex(searchParams.getQuery(), "i"),
                    Criteria.where("category.name").regex(searchParams.getQuery(), "i")
            );
            query.addCriteria(criteria);
        }

        if (searchParams.getCoordinates() != null) {
            query.addCriteria(
                    Criteria.where("address.location.coordinates")
                            .withinSphere(
                                    new Circle(
                                            new Point(searchParams.getCoordinates()[0], searchParams.getCoordinates()[1]),
                                            new Distance(30, Metrics.KILOMETERS)
                                    )
                            )
            );
        }

        final Pageable pageRequest = PageRequest.of(page, size, Sort.by(ASC, "validity.start"));
        query.with(pageRequest);
        return this.mongoTemplate.find(query, Ad.class).stream();
    }

    @Override
    public List<Ad> findAllByProfileIdIn(final List<String> ids) {
        return this.adRepository
                .findAllByProfileIdIn(ids)
                .filter(ad -> ad.getValidity().getStart().isAfter(Instant.now()))
                .collect(Collectors.toList());
    }

    @Override
    public Stream<Address> findAdress(int size) {
        return this.adRepository.findAll().stream().map(Ad::getAddress);
    }

    @Override
    public Ad read(final String id) {
        final Ad ad = this.adRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Aucune entite ne correspond à l'id " + id));
        this.updateViews(ad.getId());
        return ad;
    }

    private void updateViews(final String id) {
        final Query query = new Query(where("id").is(id));
        final Update update = new Update().inc("views", 1);
        this.mongoTemplate.updateFirst(query, update, Ad.class);
    }

    /**
     * Tous les jours
     * <p>
     * Mis à jour du statut des articles actives
     */
    @Scheduled(cron = "@daily", zone = "Europe/Paris")
    public void updateAdStatus() {
        log.info("Mis à jour des articles à {}", Instant.now());
        final Stream<Ad> ads = this.adRepository.findAllByStatusInOrderByCreation(Set.of(ACTIVE));
        final Set<Ad> adsAsSet = ads.peek(ad -> {
            final Status status = this.commonsMethods.getStatusFromDates(ad.getValidity(), ad.getValidity());
            ad.setStatus(status);
        }).collect(Collectors.toSet());

        this.adRepository.saveAll(adsAsSet);
    }

    /**
     * Tous les jours
     * <p>
     * Mis à jour du statut des articles actives
     */
    @Scheduled(cron = "@daily", zone = "Europe/Paris")
    public void deleteAdStatus() {
        log.info("Suppression des articles à {}", Instant.now());
        final List<String> ids = this.adRepository.findAllByActive(FALSE).map(Ad::getId).collect(Collectors.toList());
        this.adRepository.deleteAllById(ids);
    }
}
