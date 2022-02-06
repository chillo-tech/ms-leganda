package com.cs.ganda.service;

import com.cs.ganda.document.Validity;
import com.cs.ganda.enums.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static com.cs.ganda.enums.Status.ACTIVE;
import static com.cs.ganda.enums.Status.CLOSED;


@Component
public class CommonsMethods {

    public Status getStatusFromDates(final Validity start, final Validity end) {
        final Instant now = Instant.now();
        final long daysBeforeStartDate = ChronoUnit.DAYS.between(now, start.getDate());
        final long daysBeforeEndDate = ChronoUnit.DAYS.between(now, end.getDate());

        if (daysBeforeEndDate < 0) {
            return CLOSED;
        }
        return ACTIVE;
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public boolean stringIsNullOrEmpty(String entry) {
        return entry == null || entry.isEmpty() || entry.trim().isEmpty();
    }

    public String getRefreshToken() {
        return RandomStringUtils.randomAlphanumeric(20).toUpperCase();
    }


}
