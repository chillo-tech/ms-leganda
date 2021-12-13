package com.cs.ganda.document;

import com.cs.ganda.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "CONFIRMATION_TOKEN")
public class ConfirmationToken {

    @Id
    private String id;
    private String itemId;
    private String token;
    private String phone;
    private String phoneIndex = "+33";
    private String adId;
    private Status status;
    private Instant confirmedAt;
    private Instant creation;
    private Meal meal;
    private Profile profile;

}
