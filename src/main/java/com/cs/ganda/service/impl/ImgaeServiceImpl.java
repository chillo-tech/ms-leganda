package com.cs.ganda.service.impl;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Picture;
import com.cs.ganda.repository.AdRepository;
import com.cs.ganda.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImgaeServiceImpl implements ImageService {

    private final String imagesFolder;
    private final String imagesHost;
    private final AdRepository adRepository;

    public ImgaeServiceImpl(
            AdRepository adRepository,
            @Value("${resources.images.folder}") String imagesFolder,
            @Value("${resources.images.host}") String imagesHost
    ) {
        this.adRepository = adRepository;
        this.imagesHost = imagesHost;
        this.imagesFolder = imagesFolder;
    }

    @Async
    @Override
    public void saveAdImages(Ad ad) {
        List<Picture> pictures = ad.getPictures().stream().map(picture -> {
            try {
                String location = System.getProperty("user.home") + "/" + imagesFolder + "/" + picture.getName() + ".jpg";
                String path = this.imagesHost + "/" + picture.getName() + ".jpg";
                picture.setUri(new URI(path));
                byte[] decodedBytes = Base64.getDecoder().decode(picture.getBase64());
                FileUtils.writeByteArrayToFile(new File(location), decodedBytes);
                picture.setBase64(picture.getBase64());
                return picture;
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        ad.setPictures(pictures);
        this.adRepository.save(ad);
    }
}
