package com.cs.ganda.datas;


public class Regexp {

    public static final String EMAIL_PATTERN = "^\\S+@\\S+$";

    /**
     * Min 6 caratères
     * Majuscue et chiffres
     */
    public static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
}
