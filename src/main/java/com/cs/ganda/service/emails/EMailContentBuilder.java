package com.cs.ganda.service.emails;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class EMailContentBuilder {
    private static final String LIEN_TEXTE = "lienTexte";
    private static final String LIEN_URL = "lienUrl";
    private static final String NOM_DESTINATAIRE = "nomDestinataire";
    private static final String PRENOM_DESTINATAIRE = "prenomDestinataire";
    private static final String MESSAGE = "message";
    private final TemplateEngine templateEngine;

    public EMailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    public String getTemplate(String templateId, Map<String, String> replacements) {

        Context context = new Context();
        context.setVariable(MESSAGE, replacements.get(MESSAGE));
        context.setVariable(PRENOM_DESTINATAIRE, replacements.get(PRENOM_DESTINATAIRE));
        context.setVariable(NOM_DESTINATAIRE, replacements.get(NOM_DESTINATAIRE));
        context.setVariable(LIEN_URL, replacements.get(LIEN_URL));
        context.setVariable(LIEN_TEXTE, replacements.get(LIEN_TEXTE));
        return templateEngine.process(templateId, context);
    }


}
