package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Profile;
import com.cs.ganda.dto.SearchParamsDTO;
import com.cs.ganda.enums.Status;
import com.cs.ganda.repository.AdRepository;
import com.cs.ganda.service.AdService;
import com.cs.ganda.service.CommonsMethods;
import com.cs.ganda.service.ImageService;
import com.cs.ganda.service.ProfileService;
import com.cs.ganda.service.emails.MailsService;
import com.cs.ganda.service.sms.SmsService;
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
    private final ProfileService profileService;
    private final SmsService smsService;
    private String sms;

    public AdServiceImpl(
            AdRepository adRepository,
            MailsService mailsService,
            ImageService imageService,
            CommonsMethods commonsMethods,
            MongoTemplate mongoTemplate,
            ProfileService profileService,
            SmsService smsService
    ) {
        super(adRepository);
        this.imageService = imageService;
        this.mailsService = mailsService;
        this.adRepository = adRepository;
        this.commonsMethods = commonsMethods;
        this.mongoTemplate = mongoTemplate;
        this.profileService = profileService;
        this.smsService = smsService;
    }

    @Override
    public void activate(ActivationData activationData) {
    }

    @Override
    public Ad create(Ad ad) throws UsernameNotFoundException {
        Objects.requireNonNull(ad.getName(), String.format(MISSING_FIELD, "Nom"));
        Instant currentStart = ad.getValidity().getStart();
        int hour = currentStart.atZone(ZoneOffset.UTC).getHour();
        int minute = currentStart.atZone(ZoneOffset.UTC).getMinute();
        int second = currentStart.atZone(ZoneOffset.UTC).getSecond();

        Instant newStart = ad.getValidity().getDate();
        newStart.atZone(ZoneOffset.UTC)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .toInstant();
        ad.getValidity().setStart(newStart);

        Profile profile = this.getAuthenticatedProfile();
        ad.setActive(TRUE);
        ad.setProfile(profile);

        ad.setCreation(Instant.now());
        Ad savedAd = this.adRepository.save(ad);

        this.mailsService.newPublication(savedAd);
        this.sms ="Bonjour Cher client,\n";
        this.sms += "une nouvelle annonce vient d'être créé près de chez vous.\n";
        this.sms += "titre de l'annonce :"+savedAd.getName()+"\n";
        this.sms += "description de l'annonce :"+savedAd.getDescription()+"\n";
        this.sms += "par :"+savedAd.getProfile().getLastName()+" "+savedAd.getProfile().getFirstName()+"\n\n\n";
        this.sms += "LE GANDA Votre coin qualité ";
        /**/

        List<Profile> profileList = profileService.findByAddress(savedAd.getAddress());
        if(!profileList.isEmpty()) {
            for (Profile p : profileList) {
                //verifier qu'il y'a une adresse mail associé au profile
                if (!p.getEmail().isEmpty()) {

                    //ne pas envoyé au créateur du post
                   // if (!p.getEmail().equals(savedAd.getProfile().getEmail())) {
                        this.mailsService.newPublication(savedAd, p.getEmail());
                        this.smsService.send(p.getPhoneIndex(),p.getPhone(),this.sms);
                   // }
                }
            }
        }

        /**/
        this.imageService.saveAdImages(ad);
        return savedAd;
    }


    @Override
    public List<Ad> search(SearchParamsDTO searchParams, int page, int size) {
        log.info("Recherche avec les critères {} {} {}", searchParams, page, size);
        Instant date = Instant.now();
        Query query = new Query(Criteria.where("active").is(TRUE));
        query.addCriteria(Criteria.where("validity.start").gte(date));

        if (searchParams.getQuery() != null && !searchParams.getQuery().trim().isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.orOperator(
                    Criteria.where("name").regex(searchParams.getQuery(), "i"),
                    Criteria.where("description").regex(searchParams.getQuery(), "i"),
                    Criteria.where("category.name").regex(searchParams.getQuery(),"i")
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

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(ASC, "validity.start"));
        query.with(pageRequest);
        return this.mongoTemplate.find(query, Ad.class);
    }

    @Override
    public List<Ad> findAllByProfileIdIn(List<String> ids) {
        return this.adRepository
                .findAllByProfileIdIn(ids)
                .filter(ad -> ad.getValidity().getStart().isAfter(Instant.now()))
                .collect(Collectors.toList());
    }

    @Override
    public Ad read(String id) {
        Ad ad = this.adRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Aucune entite ne correspond à l'id " + id));
        updateViews(ad.getId());
        return ad;
    }


    private void updateViews(String id) {
        Query query = new Query(where("id").is(id));
        Update update = new Update().inc("views", 1);
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
