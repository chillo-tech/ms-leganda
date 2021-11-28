package com.cs.ganda.controller.advices;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class SecurityControllerAdvice {

    Principal currentUser;

    /*
        @ModelAttribute
        CsrfToken csrfToken(ServerWebExchange exchange){
            CsrfToken csrfToken = exchange.getAttribute(CsrfToken.class.getName());
            exchange.getAttributes().put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, "TOKEN");
            return  csrfToken;
        }
    */
    @ModelAttribute("currentUser")
    Principal currentUser(Principal currentUser) {
        return currentUser;
    }
}
