package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "AUTHENTICATION_DATA")
public class AuthenticationData {

    @Id
    private String id;
    @CreatedDate
    private Instant creation;
    private String accessToken;
    private String refreshToken;
    private String userId;

}
