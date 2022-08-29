package com.cs.ganda.service.emails;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Email;
import com.cs.ganda.document.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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
    private static final String EMAIL_CCI = "cci";
    private final EMailContentBuilder eMailContentBuilder;
    private final String annoncesUrl;
    private final String activationUrl;
    private final String from;
    private Profile profile;

    public BaseEmails(
            final EMailContentBuilder eMailContentBuilder,
            @Value("${spring.mail.from:contact@leganda.com}") final String from,
            @Value("${spring.mail.annonces-url}") final String annoncesUrl,
            @Value("${spring.mail.activation-url}") final String activationUrl
    ) {
        this.eMailContentBuilder = eMailContentBuilder;
        this.annoncesUrl = annoncesUrl;
        this.activationUrl = activationUrl;
        this.from = from;
    }

    public Email newPublication(final Ad ad) {

        final Map<String, Object> replacements = new HashMap<>();
        replacements.put(TITRE, "Une annonce vient d'être créée");
        replacements.put(MESSAGE, "Une annonce vient d'être créée");
        replacements.put(PRENOM_DESTINATAIRE, ad.getProfile().getFirstName());
        replacements.put(NOM_DESTINATAIRE, ad.getProfile().getLastName());
        //avant c'était from qui était appelé alors que from fait référence à contact@leganda.fr
        // il fallait tout simplement changer from par le mail de celui qui fait le post
        replacements.put(EMAIL_DESTINATAIRE, ad.getProfile().getEmail());
        replacements.put(LIEN_URL, String.format("%s/%s", this.annoncesUrl, ad.getId()));
        replacements.put(LIEN_TEXTE, NEW_PUBLICATION_LINK);

        return this.getEmail(replacements, ADD_TEMPLATE);
    }

    public Email newPublication(final Ad ad, final List<String> emails) {
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put(TITRE, "Une annonce vient d'être créée");
        replacements.put(MESSAGE, "Près de chez vous!!!");
        replacements.put(PRENOM_DESTINATAIRE, ad.getProfile().getFirstName());
        replacements.put(NOM_DESTINATAIRE, ad.getProfile().getLastName());
        replacements.put(EMAIL_DESTINATAIRE, this.from);
        replacements.put(EMAIL_CCI, emails);
        replacements.put(LIEN_URL, String.format("%s/%s", this.annoncesUrl, ad.getId()));
        replacements.put(LIEN_TEXTE, NEW_PUBLICATION_LINK);

        return this.getEmail(replacements, ADD_TEMPLATE);
    }

    private Email getEmail(final Map<String, Object> replacements, final String template) {
        final String message = this.eMailContentBuilder.getTemplate(template, replacements);
        final Email email = new Email(this.from, replacements.get(EMAIL_DESTINATAIRE).toString(), replacements.get(TITRE).toString(), message);
        if (replacements.get(EMAIL_CCI) != null) {
            final List<String> cci = (List<String>) replacements.get(EMAIL_CCI);
            email.setCci(cci);
        }
        email.setHtml(TRUE);
        return email;
    }

    public Email newProfile(final Profile profile, final String activationCode) {
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put(TITRE, "Activez votre compte");
        replacements.put(MESSAGE, "Une annonce vient d'être créée");
        replacements.put(PRENOM_DESTINATAIRE, profile.getFirstName());
        replacements.put(NOM_DESTINATAIRE, profile.getLastName());
        replacements.put(EMAIL_DESTINATAIRE, profile.getEmail());
        replacements.put(LIEN_URL, String.format("%s/%s", this.activationUrl, activationCode));
        replacements.put(LIEN_TEXTE, String.format("Veuillez saisir le code %s", activationCode));

        return this.getEmail(replacements, ACCOUNT_TEMPLATE);
    }
}
