package com.cs.ganda.validators;

import com.cs.ganda.datas.Regexp;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserNameValidator implements Predicate<String> {
    @Override
    public boolean test(String username) {
        Pattern pattern = Pattern.compile(Regexp.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
