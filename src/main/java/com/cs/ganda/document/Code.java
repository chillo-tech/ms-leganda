package com.cs.ganda.document;

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
@Document(collation = "CODE")
public class Code {
    @Id
    private String id;
    private String annonceur;
    private String encherisseur;
    private Instant creation;
    private Instant maj;
}
