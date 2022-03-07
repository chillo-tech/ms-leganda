package com.cs.ganda.service.impl;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Favorite;
import com.cs.ganda.document.Profile;
import com.cs.ganda.enums.Action;
import com.cs.ganda.repository.FavoriteRepository;
import com.cs.ganda.service.AdService;
import com.cs.ganda.service.AuthenticationDataService;
import com.cs.ganda.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cs.ganda.enums.Action.ADD;
import static com.cs.ganda.enums.Action.REMOVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final AdService adService;
    private final FavoriteRepository favoriteRepository;
    private final AuthenticationDataService authenticationDataService;

    @Override
    public void toggle(String adId, Action action) {
        Profile profile = this.authenticationDataService.getAuthenticatedProfile();
        Ad ad = this.adService.read(adId);
        Favorite currentFavorite = this.favoriteRepository.findByProviderIdAndProfileId(ad.getProfile().getId(), profile.getId());

        if (Objects.equals(action, ADD) && currentFavorite == null) {
            Favorite favorite = new Favorite();
            favorite.setProfile(profile);
            favorite.setProvider(ad.getProfile());
            this.favoriteRepository.save(favorite);
        } else {
            log.warn("Favoris {} déjà associé à {}", adId, profile.getId());
        }
        if (Objects.equals(action, REMOVE)) {
            this.favoriteRepository.deleteByProviderIdAndProfileId(ad.getProfile().getId(), profile.getId());
        }
    }

    @Override
    public Set<String> search() {
        Profile profile = this.authenticationDataService.getAuthenticatedProfile();
        return this.favoriteRepository.findByProfileId(profile.getId()).map(favorite -> favorite.getProvider().getId()).collect(Collectors.toSet());
    }
}
