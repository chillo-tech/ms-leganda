package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "FAVORITE")
public class Favorite {
    @Id
    private String id;
    @DBRef
    private Profile provider;
    @DBRef
    private Profile profile;
    private Instant creation = Instant.now();
}
