package com.cs.ganda.service.emails;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Email;
import com.cs.ganda.document.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

@Component
public class BaseEmails {

    private static final String ADD_TEMPLATE = "add.html";
    private static final String ACCOUNT_TEMPLATE = "compte.html";
    private static final String LIEN_TEXTE = "lienTexte";
    private static final String NEW_PUBLICATION_LINK = "Cliquez ici pour voir l'annonce";
    private static final String LIEN_URL = "lienUrl";
    private static final String NOM_DESTINATAIRE = "nomDestinataire";
    private static final String PRENOM_DESTINATAIRE = "prenomDestinataire";
    private static final String TITRE = "titre";
    private static final String MESSAGE = "message";
    private static final String EMAIL_DESTINATAIRE = "email";
    private  Profile profile;
    private final EMailContentBuilder eMailContentBuilder;
    private final String annoncesUrl;
    private final String activationUrl;
    private final String from;

    public BaseEmails(
            EMailContentBuilder eMailContentBuilder,
            @Value("${spring.mail.from:contact@leganda.com}") String from,
            @Value("${spring.mail.annonces-url}") String annoncesUrl,
            @Value("${spring.mail.activation-url}") String activationUrl
    ) {
        this.eMailContentBuilder = eMailContentBuilder;
        this.annoncesUrl = annoncesUrl;
        this.activationUrl = activationUrl;
        this.from = from;
    }

    public Email newPublication(Ad ad) {

        Map<String, String> replacements = new HashMap<>();
        replacements.put(TITRE, "Une annonce vient d'être créée");
        replacements.put(MESSAGE, "Une annonce vient d'être créée");
        replacements.put(PRENOM_DESTINATAIRE, ad.getProfile().getFirstName());
        replacements.put(NOM_DESTINATAIRE, ad.getProfile().getLastName());
        //avant c'était from qui était appelé alors que from fait référence à contact@leganda.fr
        // il fallait tout simplement changer from par le mail de celui qui fait le post
        replacements.put(EMAIL_DESTINATAIRE, ad.getProfile().getEmail());
        replacements.put(LIEN_URL, String.format("%s/%s", annoncesUrl, ad.getId()));
        replacements.put(LIEN_TEXTE, NEW_PUBLICATION_LINK);

        return this.getEmail(replacements, ADD_TEMPLATE);
    }

    private Email getEmail(Map<String, String> replacements, String template) {
        String message = this.eMailContentBuilder.getTemplate(template, replacements);
        Email email = new Email(from, replacements.get(EMAIL_DESTINATAIRE), replacements.get(TITRE), message);
        email.setHtml(TRUE);
        return email;
    }

    public Email newProfile(Profile profile, String activationCode) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put(TITRE, "Activez votre compte");
        replacements.put(MESSAGE, "Une annonce vient d'être créée");
        replacements.put(PRENOM_DESTINATAIRE, profile.getFirstName());
        replacements.put(NOM_DESTINATAIRE, profile.getLastName());
        replacements.put(EMAIL_DESTINATAIRE, profile.getEmail());
        replacements.put(LIEN_URL, String.format("%s/%s", activationUrl, activationCode));
        replacements.put(LIEN_TEXTE, String.format("Veuillez saisir le code %s", activationCode));

        return this.getEmail(replacements, ACCOUNT_TEMPLATE);
    }
}
