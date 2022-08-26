package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Email {

    private String from;

    private List<String> to;

    private List<String> cc;
    private List<String> cci;

    private String subject;

    private String message;

    private boolean isHtml;

    public Email() {
        this.to = new ArrayList<String>();
        this.cc = new ArrayList<String>();
        this.cci = new ArrayList<String>();
    }

    public Email(final String from, final String to, final String subject, final String message) {
        this.from = from;
        this.to = List.of(to);
        this.subject = subject;
        this.message = message;


    }
}
