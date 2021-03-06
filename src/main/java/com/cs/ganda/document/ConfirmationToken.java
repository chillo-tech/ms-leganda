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
    private String token;
    private Status status;
    private Instant confirmedAt;
    private Instant creation;
    private Profile profile;

}
